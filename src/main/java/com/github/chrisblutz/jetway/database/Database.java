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
package com.github.chrisblutz.jetway.database;

import com.github.chrisblutz.jetway.cli.CLI;
import com.github.chrisblutz.jetway.database.exceptions.DatabaseException;
import com.github.chrisblutz.jetway.database.managers.DatabaseManager;
import com.github.chrisblutz.jetway.database.managers.MySQLDatabaseManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.queries.DatabaseResult;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.Sort;
import com.github.chrisblutz.jetway.database.utils.DatabaseVerification;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.util.ArrayList;
import java.util.List;

/**
 * This method handles all registration and use of {@link DatabaseManager} instances,
 * and contains utility methods for interacting with database managers.
 *
 * @author Christopher Lutz
 */
public class Database {

    private static final List<DatabaseManager> managers = new ArrayList<>();
    private static DatabaseManager currentManager;

    /**
     * This method registers a new database manager.
     *
     * @param manager The database manager to register
     */
    public static void register(DatabaseManager manager) {

        managers.add(manager);
    }

    /**
     * This method retrieves the currently-active database manager.
     *
     * @return The currently-active database manager
     */
    public static DatabaseManager getManager() {

        return currentManager;
    }

    /**
     * This method sets the currently-active database manager
     * based on the provided identifier.
     *
     * @param identifier the command-line identifier for the requested
     *                   database manager
     */
    public static void setManager(String identifier) {

        for (DatabaseManager manager : managers) {

            if (manager.getCommandLineIdentifier().equalsIgnoreCase(identifier)) {

                Database.currentManager = manager;
                JetwayLog.getDatabaseLogger().info("Selected " + manager.getClass().getSimpleName() + " as the database manager.");
            }
        }

        if (getManager() == null) {
            DatabaseException exception = new DatabaseException("No database manager was found for identifier '" + identifier + "'.");
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * This method initializes the connection to the database, creates the
     * Jetway database if required, and then drops or creates any tables
     * as necessary.  It also handles version-checking between Jetway
     * and the Jetway database to check if a rebuild is required due
     * to updates or version incompatibility.
     * <p>
     * The return value from this method indicates if a full rebuild
     * of the tables from the source AIXM data is required.
     *
     * @return {@code true} if a full AIXM rebuild is necessary, {@code false} otherwise
     */
    public static boolean initializeDatabase() {

        // Create Jetway database if it doesn't exist
        setupDatabase();

        // Set the database manager to use the Jetway database schema, then open the connection
        getManager().setDatabaseName();
        if (getManager().setupConnection()) {

            // Check if rebuild of database tables is needed
            if (isRebuildNeeded()) {

                // Drop all database tables (if they exist)
                dropAllTables();

                // Store the current Jetway version in the database
                storeJetwayVersion();

                // Create all database tables
                buildAllTables();

                // AIXM rebuild required
                return true;

            } else {

                JetwayLog.getDatabaseLogger().info("Database version matches Jetway version, no database rebuild required.");
            }

        } else {

            JetwayLog.getDatabaseLogger().warn("Database connection failed.");
        }

        // AIXM rebuild not required (or database setup failed)
        return false;
    }

    private static void setupDatabase() {

        // Set up basic connection to create the database
        JetwayLog.getDatabaseLogger().info("Setting up initial connection for database creation...");
        if (getManager().setupConnection()) {
            JetwayLog.getDatabaseLogger().warn("Database connection failed.");

            // Create the database
            JetwayLog.getDatabaseLogger().info("Creating database...");
            if (getManager().createDatabase()) {

                // Close the connection
                JetwayLog.getDatabaseLogger().info("Closing initial connection...");
                getManager().closeConnection();

            } else {

                JetwayLog.getDatabaseLogger().warn("Database creation failed.");
            }

        } else {

            JetwayLog.getDatabaseLogger().warn("Database connection failed.");
        }
    }

    private static boolean isRebuildNeeded() {

        // Check if --rebuild/-r CLI option is present
        boolean drop = CLI.Options.isRebuildRequired();
        if (drop)
            JetwayLog.getDatabaseLogger().info("Command-line --rebuild/-r option present, rebuilding...");

        // If CLI option was not present, check for version mismatch
        if (!drop) {
            boolean shouldDrop = DatabaseVerification.isRebuildRequired(getManager().getJetwayVersion());
            if (shouldDrop)
                JetwayLog.getDatabaseLogger().info("Mismatch of Jetway and database versions, rebuilding...");

            drop = shouldDrop;
        }

        return drop;
    }

    private static void dropAllTables() {

        // Set database manager to drop tables in bulk
        JetwayLog.getDatabaseLogger().info("Setting up database to drop tables...");
        if (getManager().setForDrops(true)) {

            // Drop Jetway version table
            if (!getManager().dropVersionTable())
                JetwayLog.getDatabaseLogger().warn("Failed to drop Jetway version table.");

            // Drop all feature tables
            dropAllFeatureTables();

            // Reset database manager after dropping tables in bulk
            JetwayLog.getDatabaseLogger().info("Resetting database after dropping tables...");
            if (!getManager().setForDrops(false))
                JetwayLog.getDatabaseLogger().warn("Failed to reset database after dropping tables.");

        } else {

            JetwayLog.getDatabaseLogger().warn("Failed to set database up to drop tables.");
        }
    }

    private static void dropAllFeatureTables() {

        for (Class<?> featureClass : SchemaManager.getFeatures()) {

            SchemaTable table = SchemaManager.get(featureClass);
            JetwayLog.getDatabaseLogger().info("Dropping table '" + table.getTableName() + "'...");

            if (!getManager().dropTable(table))
                JetwayLog.getDatabaseLogger().warn("Table '" + table.getTableName() + "' was not dropped.");
        }
    }

    private static void storeJetwayVersion() {

        // Store the current Jetway version in the database
        if (DatabaseVerification.getCurrentJetwayVersion() != null) {
            JetwayLog.getDatabaseLogger().info("Storing Jetway version in the database...");

            if (!getManager().setJetwayVersion(DatabaseVerification.getCurrentJetwayVersion()))
                JetwayLog.getDatabaseLogger().warn("Failed to insert Jetway version into database.");
        }
    }

    private static void buildAllTables() {

        JetwayLog.getDatabaseLogger().info("Building all tables...");
        for (Class<?> featureClass : SchemaManager.getFeatures()) {

            SchemaTable table = SchemaManager.get(featureClass);

            JetwayLog.getDatabaseLogger().info("Building table '" + table.getTableName() + "'...");

            if (!getManager().buildTable(table))
                JetwayLog.getDatabaseLogger().warn("Table '" + table.getTableName() + "' could not be built.");
        }
    }

    /**
     * This method selects a single instance of the specified feature that
     * fit the {@link Query} provided.  The specific feature returned if
     * multiple exist is database manager-specific.
     * <p>
     * There is no guarantee that the feature returned will be the first one in the
     * database, and there is no guarantee that this method will return the same
     * feature twice when running the same query.
     * <p>
     * If an error occurs while selecting an instance or no instances exist
     * fitting the {@link Query}, {@code null} will be returned.
     *
     * @param type  the feature type to select
     * @param query the {@link Query} to execute
     * @param <T>   the feature type
     * @return the feature instance selected, or {@code null} if none were selected
     */
    public static <T> T select(Class<T> type, Query query) {

        SchemaTable table = SchemaManager.get(type);
        DatabaseResult result = getManager().runQuery(table, query, null);
        try {

            // Return a single instance
            return result.construct(type, table);

        } catch (IllegalAccessException | InstantiationException e) {

            DatabaseException exception = new DatabaseException("An error occurred while selecting a feature of type '" + type.getSimpleName() + "'.", e);
            JetwayLog.getDatabaseLogger().warn(exception.getMessage(), exception);
            return null;
        }
    }

    /**
     * This method selects all instances of the specified feature that
     * fit the {@link Query} provided.
     * <p>
     * The order of the array returned is not guaranteed and may vary
     * by database manager.
     * <p>
     * If an error occurs while selecting the instances or no instances exist,
     * an empty array will be returned.
     *
     * @param type  the feature type to select
     * @param query the {@link Query} to execute
     * @param <T>   the feature type
     * @return the array of features selected
     */
    public static <T> T[] selectAll(Class<T> type, Query query) {

        return selectAll(type, query, null);
    }

    /**
     * This method selects all instances of the specified feature that
     * fit the {@link Query} provided, sorted based on the defined
     * {@link Sort} instance.
     * <p>
     * If an error occurs while selecting the instances or no instances exist,
     * an empty array will be returned.
     *
     * @param type  the feature type to select
     * @param query the {@link Query} to execute
     * @param sort  the {@link Sort} defining the order to use
     * @param <T>   the feature type
     * @return the array of features selected
     */
    public static <T> T[] selectAll(Class<T> type, Query query, Sort sort) {

        SchemaTable table = SchemaManager.get(type);
        DatabaseResult result = getManager().runQuery(table, query, sort);

        try {

            // Build results into an array
            return result.constructAll(type, table);

        } catch (IllegalAccessException | InstantiationException e) {

            DatabaseException exception = new DatabaseException("An error occurred while selecting features of type '" + type.getSimpleName() + "'.", e);
            JetwayLog.getDatabaseLogger().warn(exception.getMessage(), exception);
            return result.getEmptyArray(type);
        }
    }

    /**
     * This method registers all default database managers.
     */
    public static void registerAll() {

        JetwayLog.getDatabaseLogger().info("Registering default database managers...");

        register(new MySQLDatabaseManager());
    }

    /**
     * This method resets the database management registry (after closing
     * any open connections).
     */
    public static void reset() {

        // Close the connection if it exists
        if (getManager() != null)
            getManager().closeConnection();

        // Clear registered database managers
        currentManager = null;
        managers.clear();
    }
}
