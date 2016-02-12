/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation. For the full
 * license text, see http://www.gnu.org/licenses/gpl.html.
 */
package de.joergjahnke.c64;

import de.joergjahnke.common.io.Serializable;
import de.joergjahnke.common.io.SerializationUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Sprite class to handle all data for sprites
 * 
 * @author Joerg Jahnke (joergjahnke@users.sourceforge.net)
 */
public class Sprite implements Serializable {

    /**
     * Sprite position
     */
    private int x,  y;
    /**
     * Is the sprite current enabled
     */
    private boolean isEnabled;
    /**
     * Do we have a multi-color sprite?
     */
    private boolean isMultiColor = false;
    /**
     * Is the sprite expanded horizontally?
     */
    private boolean isExpandX = false;
    /**
     * Is the sprite expanded vertically?
     */
    private boolean isExpandY = false;
    /**
     * Does the sprite have priority over the screen background?
     */
    private boolean hasPriority = false;
    /**
     * The sprite colors
     */
    private int[] colors = new int[4];
    /**
     * Is sprite DMA on/off
     */
    private boolean isPainting = false;
    /**
     * Current and previous pointer to sprite data
     */
    private int pointer,  lastPointer;
    /**
     * 24 bit register with current line's sprite data
     */
    private int lineData;
    /**
     * The data bit (+1) we are reading next
     */
    private int bitRead;
    /**
     * Data byte to fetch next
     */
    private int nextByte;
    /**
     * This is the first read of a line for an Y-expanded sprite?
     */
    private boolean isFirstYRead = true;
    /**
     * Was the sprite modified in a way that the characters behind it need to be repainted?
     */
    protected boolean needsCharCacheRefresh = false;
    /**
     * Memory where the sprite data is located
     */
    private final short[] memory;

    /**
     * Create a new sprite
     *
     * @param   memory  memory to read sprite data from
     */
    public Sprite(final short[] memory) {
        this.memory = memory;
    }

    /**
     * Get the X-coordinate of the sprite
     */
    public final int getX() {
        return this.x;
    }

    /**
     * Set the X-coordinate of the sprite
     */
    public final void setX(final int x) {
        if (this.isEnabled && this.x != x) {
            this.needsCharCacheRefresh = true;
        }
        this.x = x;
    }

    /**
     * Get the Y-coordinate of the sprite
     */
    public final int getY() {
        return this.y;
    }

    /**
     * Set the Y-coordinate of the sprite
     */
    public final void setY(final int y) {
        if (this.isEnabled && this.y != y) {
            this.needsCharCacheRefresh = true;
        }
        this.y = y;
    }

    /**
     * Has the sprite priority over the screen background?
     */
    public final boolean hasPriority() {
        return this.hasPriority;
    }

    /**
     * Set whether the sprite has priority over the screen background
     * 
     * @param   hasPriority true if the sprite has priority over the screen content
     */
    public final void setPriority(final boolean hasPriority) {
        if (this.isEnabled && this.hasPriority != hasPriority) {
            this.needsCharCacheRefresh = true;
        }
        this.hasPriority = hasPriority;
    }

    /**
     * Get a color from the sprite
     *
     * @param   n   color ID (0-3)
     */
    public final int getColor(final int n) {
        return this.colors[n];
    }

    /**
     * Set a color for the sprite
     *
     * @param   n   color ID (0-3)
     * @param   c   RGB color
     */
    public final void setColor(final int n, final int c) {
        this.colors[n] = c;
    }

    /**
     * Check whether the sprite is currently enabled
     */
    public final boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * Set whether the sprite is currently enabled
     */
    public final void setEnabled(final boolean enabled) {
        if (this.isEnabled != enabled) {
            this.needsCharCacheRefresh = true;
        }
        this.isEnabled = enabled;
    }

    /**
     * Check whether the sprite is currently expanded horizontally
     */
    public final boolean isExpandX() {
        return this.isExpandX;
    }

    /**
     * Set whether the sprite is currently expanded horizontally
     */
    public final void setExpandX(final boolean expandX) {
        if (this.isEnabled && this.isExpandX != expandX) {
            this.needsCharCacheRefresh = true;
            if (!isLineFinished()) {
                if (expandX) {
                    this.bitRead <<= 1;
                } else {
                    this.bitRead >>= 1;
                }
            }
        }
        this.isExpandX = expandX;
    }

    /**
     * Check whether the sprite is currently expanded vertically
     */
    public final boolean isExpandY() {
        return this.isExpandY;
    }

    /**
     * Set whether the sprite is currently expanded vertically
     */
    public final void setExpandY(final boolean expandY) {
        if (this.isEnabled && this.isExpandY != expandY) {
            this.needsCharCacheRefresh = true;
        }
        this.isExpandY = expandY;
    }

    /**
     * Check whether the sprite is multi-colored
     */
    public final boolean isMulticolor() {
        return this.isMultiColor;
    }

    /**
     * Set whether the sprite is multi-colored
     */
    public final void setMulticolor(final boolean multicolor) {
        this.isMultiColor = multicolor;
    }

    /**
     * Check whether the currently painted line has been finished
     */
    public final boolean isLineFinished() {
        return this.bitRead <= 0;
    }

    /**
     * Check whether the sprite is currently being painted
     */
    public final boolean isPainting() {
        return this.isPainting;
    }

    /**
     * Set whether the sprite is currently being painted
     */
    public final void setPainting(final boolean painting) {
        if (!painting) {
            this.needsCharCacheRefresh = false;
        }
        this.isPainting = painting;
    }

    /**
     * Check whether we just painted the last byte of the sprite
     */
    public final boolean isBeyondLastByte() {
        return this.nextByte >= 63;
    }

    /**
     * Initialize the update for the sprite
     */
    public final void initUpdate() {
        this.nextByte = 0;
        this.isPainting = false;
        this.lineData = 0;
    }

    /**
     * Start with painting the sprite
     */
    public final void initPainting() {
        this.nextByte = 0;
        this.isPainting = true;
        this.isFirstYRead = true;
    }

    /**
     * Set the address we read the sprite data from
     *
     * @param   pointer address to read from
     */
    public final void setDataPointer(final int pointer) {
        this.pointer = pointer;
    }

    /**
     * Read sprite data for the current line. This prepares the sprite for subsequent calls
     * of getPixel where this data is used.
     */
    public final void readLineData() {
        // read the three bytes of sprite data for the current line
        final int nextByte_ = this.nextByte;

        this.lineData = ((this.memory[this.pointer + nextByte_] & 0xff) << 16) | ((this.memory[this.pointer + nextByte_ + 1] & 0xff) << 8) | this.memory[this.pointer + nextByte_ + 2];

        // we have to read this line again if the Y-expansion is set and this was the first read
        if (this.isExpandY) {
            if (!this.isFirstYRead) {
                this.nextByte += 3;
            }
            this.isFirstYRead = !this.isFirstYRead;
        } else {
            this.nextByte += 3;
        }

        // the pointer has been modified since the last read?
        if (this.pointer != this.lastPointer) {
            // then clear char cache behind the sprite to repaint the characters
            this.needsCharCacheRefresh = true;
            this.lastPointer = this.pointer;
        }

        // we start with reading the highest bit
        this.bitRead = this.isExpandX ? 48 : 24;
    }

    /**
     * Read the color ID of the next pixel of the current sprite line
     *
     * @return  0 for a transparent pixel, otherwise the color ID that can be used
     *          for an access to getColor
     * @see de.joergjahnke.c64.Sprite#getColor
     */
    public final int getNextPixel() {
        // the last bit was already read?
        if (isLineFinished()) {
            // then indicate a transparent pixel
            return 0;
        } else {
            // proceed to the next bit
            --this.bitRead;

            // the bitRead value is initially double for X-expanded sprites,
            // so we have to halve that again to get the correct shift value
            final int shift = this.isExpandX ? this.bitRead >> 1 : this.bitRead;

            // return bits for the sprite, depending on the color mode
            if (this.isMultiColor) {
                // we return the two relevant bits, i.e. a value between 0 and 3
                return (this.lineData >> (shift & 0xfe)) & 3;
            } else {
                // we return 0 or 2, so we get the relevant bit and multiply it by 2
                return ((this.lineData >> shift) & 1) << 1;
            }
        }
    }

    // implementation of the Serializable interface
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeBoolean(this.hasPriority);
        out.writeBoolean(this.isEnabled);
        out.writeBoolean(this.isExpandX);
        out.writeBoolean(this.isExpandY);
        out.writeBoolean(this.isFirstYRead);
        out.writeBoolean(this.isMultiColor);
        out.writeBoolean(this.isPainting);
        out.writeBoolean(this.needsCharCacheRefresh);
        SerializationUtils.serialize(out, this.colors);
        out.writeInt(this.bitRead);
        out.writeInt(this.lastPointer);
        out.writeInt(this.lineData);
        out.writeInt(this.nextByte);
        out.writeInt(this.pointer);
    }

    public void deserialize(DataInputStream in) throws IOException {
        this.x = in.readInt();
        this.y = in.readInt();
        this.hasPriority = in.readBoolean();
        this.isEnabled = in.readBoolean();
        this.isExpandX = in.readBoolean();
        this.isExpandY = in.readBoolean();
        this.isFirstYRead = in.readBoolean();
        this.isMultiColor = in.readBoolean();
        this.isPainting = in.readBoolean();
        this.needsCharCacheRefresh = in.readBoolean();
        SerializationUtils.deserialize(in, this.colors);
        this.bitRead = in.readInt();
        this.lastPointer = in.readInt();
        this.lineData = in.readInt();
        this.nextByte = in.readInt();
        this.pointer = in.readInt();
    }
}
