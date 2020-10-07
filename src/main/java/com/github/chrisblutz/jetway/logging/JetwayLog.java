/*
 * Copyright 2020 Christopher Lutz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chrisblutz.jetway.logging;

import com.github.chrisblutz.jetway.database.utils.DatabaseVerification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class handles the loggers for Jetway.
 *
 * @author Christopher Lutz
 */
public final class JetwayLog {

    private JetwayLog() {}

    private static Logger jetwayLogger = null;
    private static Logger databaseLogger = null;

    /**
     * This method returns the main Jetway logger.
     *
     * @return The Jetway logger
     */
    public static Logger getJetwayLogger() {

        if (jetwayLogger == null) {

            jetwayLogger = LogManager.getLogger("  Jetway");
            logSystemInformation();
        }

        return jetwayLogger;
    }

    /**
     * This method returns the logger for the database
     * side of Jetway.
     * <p>
     * This is the logger that should be utilized for database
     * managers.
     *
     * @return The database logger
     */
    public static Logger getDatabaseLogger() {

        if (databaseLogger == null) {

            databaseLogger = LogManager.getLogger("Database");
        }

        return databaseLogger;
    }

    private static void logSystemInformation() {

        // Log basic information about system
        getJetwayLogger().info("Jetway Version:  " + DatabaseVerification.getCurrentJetwayVersion());
        getJetwayLogger().info("Java Version:    " + System.getProperty("java.version"));
        getJetwayLogger().info("Java Vendor:     " + System.getProperty("java.vendor"));
        getJetwayLogger().info("OS Name:         " + System.getProperty("os.name"));
        getJetwayLogger().info("OS Version:      " + System.getProperty("os.version"));
        getJetwayLogger().info("OS Architecture: " + System.getProperty("os.arch"));

        long memoryLimit = Runtime.getRuntime().maxMemory();
        getJetwayLogger().info("VM Memory Limit: " + (memoryLimit == Long.MAX_VALUE ? "None" : formatMemoryAsString(memoryLimit)));
        getJetwayLogger().info("");
    }

    private static String formatMemoryAsString(long bytes) {

        // Declare all possible suffixes
        String[] suffixes = new String[]{"bytes", "KB", "MB", "GB", "TB", "PB"};

        double bytesDouble = bytes;
        int level;
        for (level = 0; level < suffixes.length; level++) {

            // Decrease orders of magnitude until the smallest level is reached
            if (bytesDouble < 1024)
                break;

            bytesDouble /= 1024;
        }

        // Format with suffix
        return String.format("%.3f " + suffixes[level], bytesDouble);
    }
}
