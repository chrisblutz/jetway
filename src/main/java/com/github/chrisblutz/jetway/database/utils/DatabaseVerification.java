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

/**
 * This class provides information about Jetway versioning
 * and database versioning, which helps determine whether
 * database dropping is required due to updates and
 * incompatibilities.
 *
 * @author Christopher Lutz
 */
public class DatabaseVerification {

    private static String currentJetwayVersion = null;
    private static boolean isVersionLoaded = false;

    /**
     * This method determines whether the Jetway version
     * stored in the database differs from the current version.
     * If it does, all database tables must be dropped before
     * they can be filled.
     *
     * @param databaseVersion the Jetway version as reported by
     *                        the database.  This can be {@code null}
     * @return {@code true} if the versions do not match, {@code false} if they do
     */
    public static boolean isDropRequired(String databaseVersion) {

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

            Package p = Package.getPackage("com.github.chrisblutz.jetway");
            if (p != null) {

                currentJetwayVersion = p.getImplementationVersion();
            }
        }

        return currentJetwayVersion;
    }
}
