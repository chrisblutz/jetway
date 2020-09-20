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
package com.github.chrisblutz.jetway.database.managers;

import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.queries.DatabaseResult;
import com.github.chrisblutz.jetway.database.queries.MultiQuery;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.SingleQuery;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines the framework for database managers
 * that interface with various database software.
 * <p>
 * Database managers do not necessarily need to fully
 * implement every method if their respective database
 * software does not require it.
 *
 * @author Christopher Lutz
 */
public abstract class DatabaseManager {

    /**
     * This field defines the name to be used for the Jetway database
     */
    public static final String DATABASE_NAME = "jetway";

    /**
     * This method gets the command-line identifier used to
     * select this manager.
     *
     * @return The identifier for this manager
     */
    public abstract String getCommandLineIdentifier();

    /**
     * This method sets the server address to be used
     * by this database manager.
     *
     * @param server The server address to use
     */
    public abstract void setServer(String server);

    /**
     * This method sets the database name to be used by
     * this database manager.  This is always the value
     * defined in {@link #DATABASE_NAME}.
     */
    public abstract void setDatabaseName();

    /**
     * This method sets the username to be used
     * by this database manager.
     *
     * @param user The username to use
     */
    public abstract void setUser(String user);

    /**
     * This method sets the password to be used
     * by this database manager.
     *
     * @param password The password to use
     */
    public abstract void setPassword(String password);

    /**
     * This method initiates the connection to
     * the database.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean setupConnection();

    /**
     * This method creates the Jetway database
     * if it does not exist.
     * <p>
     * This method should not re-create the database if
     * it already exists.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean createDatabase();

    /**
     * This method closes the connection to
     * the database.
     */
    public abstract void closeConnection();

    /**
     * This method retrieves the Jetway version stored in the database.
     * <p>
     * This value is used to determine if Jetway versions have changed,
     * and if any tables need to be dropped before data can be loaded.
     * <p>
     * If no version is stored, return {@code null}.
     *
     * @return The Jetway version stored in the database
     */
    public abstract String getJetwayVersion();

    /**
     * This method stores the specified Jetway version in the database
     * for future use.
     * <p>
     * This value will be used in future runs to determine if
     * Jetway versions have changed.
     *
     * @param version the Jetway version to store
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean setJetwayVersion(String version);

    /**
     * This method builds a table in the database as defined
     * by the specified {@link SchemaTable}.
     * <p>
     * This method should not re-create the table if
     * it already exists.
     *
     * @param table the {@link SchemaTable} to use
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean buildTable(SchemaTable table);

    /**
     * This method drops the table in the database defined
     * by the specified {@link SchemaTable}.
     * <p>
     * If the table does not exist, this method does nothing.
     *
     * @param table the {@link SchemaTable} to drop
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean dropTable(SchemaTable table);

    /**
     * This method is a utility method that allows database
     * managers to set themselves up for dropping multiple
     * tables at once.
     * <p>
     * For instance, {@link MySQLDataManager} uses this method
     * to disable/re-enable foreign key checks that would normally
     * prohibit bulk table dropping.
     *
     * @param isDropping {@code true} if this method should set up
     *                   to drop tables, {@code false} if
     *                   this method should reset after
     *                   dropping tables
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean setForDrops(boolean isDropping);

    /**
     * This method inserts an instance of a feature into the table defined
     * by the specified {@link SchemaTable}.
     * <p>
     * If another entry exists with the same primary key, that entry
     * should be updated instead.
     *
     * @param table the {@link SchemaTable} to use
     * @param value the feature instance to insert
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean insertEntry(SchemaTable table, Object value);

    /**
     * This method executes a {@link Query} on the database and returns the
     * set of results obtained.
     * <p>
     * The order of results is not necessarily guaranteed.
     *
     * @param table the table to query from
     * @param query the {@link Query} to run
     * @return The set of results from the query
     */
    public abstract DatabaseResult runQuery(SchemaTable table, Query query);

    /**
     * This utility method provides a wrapper for {@link MessageFormat#format(String, Object...)}
     * to simplify query-building.
     *
     * @param format    the format string
     * @param arguments the objects to insert
     * @return The formatted string
     * @see MessageFormat#format(String, Object...)
     */
    protected String format(String format, Object... arguments) {

        return MessageFormat.format(format, arguments);
    }

    /**
     * This method compiles a set of all of the tables
     * referenced in the conditions of a {@link Query}.
     *
     * @param query the {@link Query} to check
     * @return A {@link Set} containing all referenced {@link SchemaTable}s
     */
    protected Set<SchemaTable> getReferencedTables(Query query) {

        Set<SchemaTable> tables = new HashSet<>();
        buildReferencedTablesSet(query, tables);
        return tables;
    }

    private void buildReferencedTablesSet(Query query, Set<SchemaTable> tables) {

        if (query instanceof MultiQuery) {

            for (Query subQuery : ((MultiQuery) query).getQueries())
                buildReferencedTablesSet(subQuery, tables);

        } else if (query instanceof SingleQuery) {

            Class<?> feature = ((SingleQuery) query).getFeature();
            SchemaTable table = SchemaManager.get(feature);
            tables.add(table);

            while (table.hasForeignKey()) {
                table = table.getForeignTable();
                tables.add(table);
            }
        }
    }
}
