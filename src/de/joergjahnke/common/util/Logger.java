/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation. For the full
 * license text, see http://www.gnu.org/licenses/gpl.html.
 */
package de.joergjahnke.common.util;

/**
 * Interface for objects which log messages for a specific system or component
 *
 * @author  Joerg Jahnke (joergjahnke@users.sourceforge.net)
 */
public interface Logger {

    /**
     * An information message
     */
    public final static int INFO = 1;
    /**
     * A warning message
     */
    public final static int WARNING = 2;
    /**
     * An error message
     */
    public final static int ERROR = 4;
    /**
     * Combination of all message types
     */
    public final static int ALL = INFO | WARNING | ERROR;

    /**
     * Logs a given message of a given type e.g. INFO, WARNING, ERROR
     *
     * @param   message message to log
     * @param   type    message type e.g. INFO
     */
    public void log(Object message, int type);

    /**
     * Log an INFO message
     *
     * @param   message message to log
     */
    public void info(Object message);

    /**
     * Log a WARNING message
     *
     * @param   message message to log
     */
    public void warning(Object message);

    /**
     * Log an ERROR message
     *
     * @param   message message to log
     */
    public void error(Object message);

    /**
     * Enable/disable verbose logging
     *
     * @param   enabled true to enable verbose logging, false to disable
     */
    public void setVerbose(boolean enabled);

    /**
     * Check whether verbose logging is enabled
     *
     * @return  true if verbose logging is enabled, otherwise false
     */
    public boolean isVerbose();
}
