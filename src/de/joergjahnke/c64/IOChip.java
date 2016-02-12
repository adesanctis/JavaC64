/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation. For the full
 * license text, see http://www.gnu.org/licenses/gpl.html.
 */
package de.joergjahnke.c64;

/**
 * Defines operations common for an IO chip<br>
 * <br>
 * A good description of the registers of the C64 IO chips can be found at
 * <a href='http://www.unusedino.de/ec64/technical/project64/memory_maps.html'>http://www.unusedino.de/ec64/technical/project64/memory_maps.html</a> (English) or
 * <a href='http://www.infinite-loop.at/Power64/Documentation/Power64-LiesMich/AD-Spezialbausteine.html'>http://www.infinite-loop.at/Power64/Documentation/Power64-LiesMich/AD-Spezialbausteine.html</a> (German).<br>
 *
 * @author Joerg Jahnke (joergjahnke@users.sourceforge.net)
 */
public interface IOChip {

    /**
     * Read from IO chip
     * 
     * @param   register    register to read from
     * @return data read
     */
    public abstract int readRegister(int register);

    /**
     * Write to IO chip
     * 
     * @param   register    register to write to
     * @param   data    data to write
     */
    public abstract void writeRegister(int register, short data);

    /**
     * Get the CPU cycles count when the next update of this IO chip is required
     *
     * @return  CPU cycles count of next update
     */
    public abstract long getNextUpdate();

    /**
     * Check if IO/VIC chip needs to be updated and do the update if necessary
     * 
     * @param   cycles  current CPU count, used for synchronization
     */
    public abstract void update(long cycles);

    /**
     * Reset IO chip
     */
    public abstract void reset();
}
