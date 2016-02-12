/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation. For the full
 * license text, see http://www.gnu.org/licenses/gpl.html.
 */
package de.joergjahnke.common.emulation;

/**
 * Creates frequency data
 *
 * @author  Joerg Jahnke (joergjahnke@users.sourceforge.net)
 */
public interface FrequencyDataProducer {

    /**
     * Get the current sound frequency, e.g. 2000 Hz
     */
    public int getFrequency();

    /**
     * Get the current volume on a scale of 0-100
     */
    public int getVolume();

    /**
     * Get the General MIDI type of the frequency.
     * See <a href='http://en.wikipedia.org/wiki/General_MIDI'>http://en.wikipedia.org/wiki/General_MIDI</a> for more on General MIDI.
     *
     * @return  MIDI type
     */
    public int getType();
}
