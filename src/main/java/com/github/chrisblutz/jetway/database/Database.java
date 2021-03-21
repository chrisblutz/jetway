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

import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.database.batches.DatabaseBatching;
import com.github.chrisblutz.jetway.database.exceptions.DatabaseException;
import com.github.chrisblutz.jetway.database.managers.DatabaseManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.metadata.Metadata;
import com.github.chrisblutz.jetway.database.queries.DatabaseResult;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.Sort;
import com.github.chrisblutz.jetway.database.utils.DatabaseVerification;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.time.ZonedDateTime;

/**
 * This method handles all registration and use of {@link DatabaseManager} instances,
 * and contains utility methods for interacting with database managers.
 *
 * @author Christopher Lutz
 */
public final class Database {

    private Database() {}

    private static DatabaseManager currentManager;
    private static boolean forceRebuild = false;
    private static boolean ignoreDates = false, strictDates = false;

    /**
     * This method retrieves the currently-active database manager.
     *
     * @return The currently-active database manager
     */
    public static DatabaseManager getManager() {

        return currentManager;
    }

    /**
     * This method sets the currently-active database manager.
     *
     * @param manager the {@link DatabaseManager} to use
     */
    public static void setManager(DatabaseManager manager) {

        Database.currentManager = manager;
        JetwayLog.getDatabaseLogger().info("Selected " + manager.getClass().getSimpleName() + " as the database manager.");
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

        // Check if force-rebuild flag is set
        boolean drop = forceRebuild;
        if (drop)
            JetwayLog.getDatabaseLogger().info("Force-rebuild flag is set, rebuilding database...");

        // If force rebuild flag was not set, check for version mismatch
        if (!drop) {

            String version = getManager().getMetadata(Metadata.JETWAY_VERSION);
            boolean shouldDrop = DatabaseVerification.isRebuildRequired(version);

            if (shouldDrop) {
                JetwayLog.getDatabaseLogger().info("Mismatch of Jetway and database versions, rebuilding database...");
                JetwayLog.getDatabaseLogger().info("Current version is " + DatabaseVerification.getCurrentJetwayVersion() + ", database version is " + version + ".");
            }

            drop = shouldDrop;
        }

        // If rebuild flag is not set and Jetway is NOT ignoring effective date ranges, check if data is current
        if (!drop && !isIgnoringEffectiveRange() && !isValid()) {

            JetwayLog.getDatabaseLogger().info("Information in Jetway database is out-of-date, rebuilding database...");
            drop = true;
        }

        return drop;
    }

    /**
     * This method drops all tables in Jetway's database.
     * This effectively clears all of Jetway's data, and you
     * will need to re-initialize Jetway to recreate it.
     */
    public static void dropAllTables() {

        // If database manager is not connected, connect it and attempt to create the database
        // createDatabase() shouldn't re-create a database that already exists
        if (!getManager().isConnected()) {

            getManager().setupConnection();
            getManager().createDatabase();
        }

        // Clear all metadata
        if (!getManager().clearMetadata())
            JetwayLog.getDatabaseLogger().warn("Failed to clear metadata.");

        // Drop all feature tables
        dropAllFeatureTables();
    }

    private static void dropAllFeatureTables() {

        JetwayLog.getDatabaseLogger().info("Dropping all feature tables...");

        // Drop tables in bottom-up dependency order (starting with the child tables)
        for (SchemaTable table : SchemaManager.getChildFirstDependencyTree()) {

            JetwayLog.getDatabaseLogger().info("Dropping table '" + table.getTableName() + "'...");

            if (!getManager().dropTable(table))
                JetwayLog.getDatabaseLogger().warn("Table '" + table.getTableName() + "' was not dropped.");
        }
    }

    private static void storeJetwayVersion() {

        // Store the current Jetway version in the database
        if (DatabaseVerification.getCurrentJetwayVersion() != null) {
            JetwayLog.getDatabaseLogger().info("Storing Jetway version in the database...");

            // Set metadata for Jetway version
            if (!getManager().setMetadata(Metadata.JETWAY_VERSION, DatabaseVerification.getCurrentJetwayVersion()))
                JetwayLog.getDatabaseLogger().warn("Failed to update Jetway version metadata.");
        }
    }

    private static void buildAllTables() {

        JetwayLog.getDatabaseLogger().info("Building all tables...");
        for (Class<? extends Feature> featureClass : SchemaManager.getFeatures()) {

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
    public static <T extends Feature> T select(Class<T> type, Query query) {

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
    public static <T extends Feature> T[] selectAll(Class<T> type, Query query) {

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
    public static <T extends Feature> T[] selectAll(Class<T> type, Query query, Sort sort) {

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
     * This method forces the database manager
     * to drop all existing tables and rebuild them
     * from the AIXM data.
     */
    public static void forceRebuild() {

        forceRebuild = true;
    }

    /**
     * This method sets whether or not Jetway will consider the effective
     * date range of data when loading/reloading the database.
     * <p>
     * The metadata for the database provides the date when the information
     * in the database takes effect, as well as the date when it goes out of date.
     * <p>
     * If {@code true}, Jetway will not consider these dates when choosing whether
     * or not to reload it's data.
     * <p>
     * If {@code false}, Jetway will reload the data in the database if the current
     * data is out of date.
     * <p>
     * If this value is set to {@code true}, then {@link #setStrictEffectiveRangeEnforcement(boolean)}
     * will have no effect.
     *
     * @param ignore {@code true} to ignore dates, {@code false} to consider them
     * @see #isValid()
     */
    public static void setIgnoreEffectiveRange(boolean ignore) {

        ignoreDates = ignore;
    }

    /**
     * This method checks whether or not Jetway will consider the effective
     * date range of data when loading/reloading the database.
     * <p>
     * See {@link #setIgnoreEffectiveRange(boolean)} for information on what
     * each value means.
     *
     * @return {@code true} if effective range is ignored, {@code false} otherwise.
     * @see #isValid()
     * @see #setIgnoreEffectiveRange(boolean)
     */
    public static boolean isIgnoringEffectiveRange() {

        return ignoreDates;
    }

    /**
     * This method sets whether or not Jetway will throw an exception when the current data
     * in the database is out of date, and either the source of data provided to
     * {@link com.github.chrisblutz.jetway.Jetway#setAIXMSource(AIXMSource) Jetway.setAIXMSource(AIXMSource)}
     * is either {@code null} or out of date.
     * <p>
     * If {@code true}, Jetway will throw an exception when it cannot find a currently-valid
     * source of data.
     * <p>
     * If {@code false}, Jetway will log a warning that it cannot find a currently-valid source
     * of data, but will allow execution to proceed.
     * <p>
     * If {@link #setIgnoreEffectiveRange(boolean)} is set to {@code true}, this method has no
     * effect.
     *
     * @param strict {@code true} to strictly enforce effective ranges, {@code false} to leniently
     *               enforce these ranges
     * @see #isValid()
     */
    public static void setStrictEffectiveRangeEnforcement(boolean strict) {

        strictDates = strict;
    }

    /**
     * This method checks whether or not Jetway will throw an exception when the current data
     * in the database is out of date, and either the source of data provided to
     * {@link com.github.chrisblutz.jetway.Jetway#setAIXMSource(AIXMSource) Jetway.setAIXMSource(AIXMSource)}
     * is either {@code null} or out of date.
     * <p>
     * See {@link #setStrictEffectiveRangeEnforcement(boolean)} for information on what
     * each value means.
     *
     * @return {@code true} if effective range enforcement is strict, {@code false} otherwise.
     * @see #isValid()
     * @see #setStrictEffectiveRangeEnforcement(boolean) E
     */
    public static boolean isEffectiveRangeEnforcementStrict() {

        return strictDates;
    }

    /**
     * This method resets the database manager after closing
     * any open connections.
     */
    public static void reset() {

        // Close the connection if it exists
        if (getManager() != null)
            getManager().closeConnection();

        // Clear registered database managers
        currentManager = null;

        // Reset force rebuild flag
        forceRebuild = false;

        // Reset effective date flags
        ignoreDates = false;
        strictDates = false;

        // Reset batch information
        DatabaseBatching.reset();
    }

    /**
     * This method returns the date that the currently-loaded
     * data goes into effect.  This is determined from the NASR
     * data provided by the FAA.
     * <p>
     * Each NASR subscription file is valid for 28 days.
     *
     * @return The date this data goes into effect
     */
    public static ZonedDateTime getValidFrom() {

        return getManager().getMetadata(Metadata.EFFECTIVE_FROM_DATE);
    }

    /**
     * This method returns the date that the currently-loaded
     * data becomes out-of-date.  This is determined from the NASR
     * data provided by the FAA.
     * <p>
     * Each NASR subscription file is valid for 28 days.
     *
     * @return The date this data becomes out-of-date
     */
    public static ZonedDateTime getValidTo() {

        return getManager().getMetadata(Metadata.EFFECTIVE_TO_DATE);
    }

    /**
     * This method determines if the data in the database is currently valid.
     * Data is defined to be valid when the current date is within the 'valid from'
     * and 'valid to' range provided by the FAA for each NASR subscription release.
     * <p>
     * Each NASR subscription file is valid for 28 days.
     *
     * @return {@code true} if the data is valid, {@code false} otherwise
     * @see #getValidFrom()
     * @see #getValidTo()
     */
    public static boolean isValid() {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = getValidFrom();
        ZonedDateTime to = getValidTo();

        // If either end of the range is null, the data is not valid
        if (from == null || to == null)
            return false;

        // Check to make sure that the current time is within the effective range
        return now.isAfter(from) && now.isBefore(to);
    }
}
