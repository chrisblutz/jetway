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
package com.github.chrisblutz.jetway;

import com.github.chrisblutz.jetway.aixm.AIXM;
import com.github.chrisblutz.jetway.aixm.AIXMFeatureManager;
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.conversion.DataConversion;
import com.github.chrisblutz.jetway.conversion.DefaultConverters;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.managers.DatabaseManager;
import com.github.chrisblutz.jetway.database.utils.DatabaseVerification;
import com.github.chrisblutz.jetway.features.*;

/**
 * This class serves as the main entry point for Jetway.
 *
 * @author Christopher Lutz
 */
public class Jetway {

    private static AIXMSource aixmSource;

    /**
     * This method is the entry point for Jetway.  It loads converts
     * and database managers, then registers features.  Then, it
     * initiates the AIXM loading sequence.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        initialize();
    }

    /**
     * This method initializes Jetway's registries and begins loading
     * AIXM data.
     */
    public static void initialize() {

        DefaultConverters.registerAll();

        FeatureManager.register(Airport.class);
        FeatureManager.register(Runway.class);
        FeatureManager.register(RunwayEnd.class);
        FeatureManager.register(RunwayDirection.class);

        // Initialize the database, and if necessary, rebuild the tables from source AIXM data
        boolean rebuild = Database.initializeDatabase();
        if (rebuild)
            AIXM.load();
    }

    /**
     * This method sets the {@link AIXMSource} that will
     * be used to load AIXM feature data.
     *
     * @param aixmSource the {@link AIXMSource} to use
     */
    public static void setAIXMSource(AIXMSource aixmSource) {

        Jetway.aixmSource = aixmSource;
    }

    /**
     * This method retrieves the {@link AIXMSource} being
     * be used to load AIXM feature data.
     *
     * @return the {@link AIXMSource} being used
     */
    public static AIXMSource getAIXMSource() {

        return aixmSource;
    }

    /**
     * This method forces Jetway to rebuild its
     * database tables and reload AIXM data from
     * its source.
     */
    public static void forceDatabaseRebuild() {

        Database.forceRebuild();
    }

    /**
     * This method sets the {@link DatabaseManager}
     * that Jetway will use to handle database operations.
     *
     * @param manager the {@link DatabaseManager} to use
     */
    public static void setDatabaseManager(DatabaseManager manager) {

        Database.setManager(manager);
    }

    /**
     * This method sets the server name for the database
     * that Jetway will use.
     *
     * @param server the database server name
     */
    public static void setDatabaseServer(String server) {

        Database.getManager().setServer(server);
    }

    /**
     * This method sets the user for the database
     * that Jetway will use.
     *
     * @param user the database user
     */
    public static void setDatabaseUser(String user) {

        Database.getManager().setUser(user);
    }

    /**
     * This method sets the password for the database
     * that Jetway will use.
     *
     * @param password the database password
     */
    public static void setDatabasePassword(String password) {

        Database.getManager().setPassword(password);
    }

    /**
     * This method resets all of Jetway's registries.
     */
    public static void reset() {

        setAIXMSource(null);

        AIXMFeatureManager.reset();
        SchemaManager.reset();
        Database.reset();
        DataConversion.reset();
        DatabaseVerification.reset();
    }
}
