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
package com.github.chrisblutz.jetway.database.utils;

import com.github.chrisblutz.jetway.Jetway;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides information about Jetway versioning
 * and database versioning, which helps determine whether
 * database rebuilds are required due to updates and
 * incompatibilities.
 *
 * @author Christopher Lutz
 */
public final class DatabaseVerification {

    private DatabaseVerification() {}

    private static String currentJetwayVersion = null;
    private static boolean isVersionLoaded = false;

    /**
     * This method determines whether the Jetway version
     * stored in the database differs from the current version.
     * If it does, all database tables must be rebuilt
     * from source AIXM data.
     *
     * @param databaseVersion the Jetway version as reported by
     *                        the database.  This can be {@code null}
     * @return {@code true} if the versions do not match, {@code false} if they do
     */
    public static boolean isRebuildRequired(String databaseVersion) {

        if (databaseVersion == null)
            return true;

        return !databaseVersion.equals(getCurrentJetwayVersion());
    }

    /**
     * This method determines the current Jetway version.
     *
     * @return The current version of Jetway
     */
    public static String getCurrentJetwayVersion() {

        if (!isVersionLoaded) {

            isVersionLoaded = true;

            try {

                // Load resource as input stream, then load it into a Properties instance
                InputStream stream = Jetway.class.getResourceAsStream("/version.properties");

                Properties properties = new Properties();
                properties.load(stream);

                currentJetwayVersion = properties.getProperty("version");

            } catch (IOException e) {

                JetwayLog.getJetwayLogger().warn("Could not read version file.  (" + e.getMessage() + ")");
            }

            // If either unit-test utility property is set, alter the version accordingly
            if (System.getProperty("FORCE_JETWAY_VERSION") != null)
                currentJetwayVersion = System.getProperty("FORCE_JETWAY_VERSION");
            else if (System.getProperty("FORCE_NULL_JETWAY_VERSION") != null)
                currentJetwayVersion = null;
        }

        return currentJetwayVersion;
    }

    /**
     * This method resets currently-loaded version information.
     */
    public static void reset() {

        isVersionLoaded = false;
        currentJetwayVersion = null;
    }
}
