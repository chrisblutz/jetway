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
import com.github.chrisblutz.jetway.database.keys.ForeignKeyData;
import com.github.chrisblutz.jetway.database.keys.Relationship;
import com.github.chrisblutz.jetway.database.metadata.BasicMetadata;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.queries.*;
import com.github.chrisblutz.jetway.features.Feature;

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
     * This field defines the name to be used for the Jetway database.
     */
    protected static final String DATABASE_NAME = "jetway";

    private boolean connected = false;

    /**
     * This method sets the server address to be used
     * by this database manager.
     *
     * @param server the server address to use
     */
    public abstract void setServer(String server);

    /**
     * This method sets the port to be used by
     * this database manager.
     *
     * @param port the port number
     */
    public abstract void setPort(int port);

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
     * @param user the username to use
     */
    public abstract void setUser(String user);

    /**
     * This method sets the password to be used
     * by this database manager.
     *
     * @param password the password to use
     */
    public abstract void setPassword(String password);

    /**
     * This method initiates the connection to
     * the database.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean setupConnection() {

        connected = setupConnectionSpecific();
        return connected;
    }

    /**
     * This method is the manager-specific implementation for
     * {@link #setupConnection()}.
     * <p>
     * This method does the heavy-lifting, while the wrapper
     * tracks if the connection is open or not.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    protected abstract boolean setupConnectionSpecific();

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
    public void closeConnection() {

        connected = false;
        closeConnectionSpecific();
    }

    /**
     * This method is the manager-specific implementation for
     * {@link #closeConnection()} ()}.
     * <p>
     * This method does the heavy-lifting, while the wrapper
     * tracks if the connection is open or not.
     */
    protected abstract void closeConnectionSpecific();

    /**
     * This method determines if the database connection
     * is currently open.
     *
     * @return {@code true} if the database connection is open, {@code false} otherwise
     */
    public boolean isConnected() {

        return connected;
    }

    /**
     * This method retrieves a specific piece of metadata from the database.
     * <p>
     * If the metadata table does not exist, or if the specific metadata instance
     * has no stored value, this method will return {@code null}.
     *
     * @param metadata the metadata instance to retrieve
     * @param <T>      the type of metadata value being retrieved
     * @return The retrieved metadata information, or {@code null} if no metadata was found
     */
    public abstract <T> T getMetadata(BasicMetadata<T> metadata);

    /**
     * This method inserts a piece of metadata into the database.
     *
     * @param metadata the metadata instance to store
     * @param value    the metadata value to store
     * @param <T>      the type of metadata value being stored
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract <T> boolean setMetadata(BasicMetadata<T> metadata, T value);

    /**
     * This method clears all metadata stored in the database.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean clearMetadata();

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
     * This method inserts instances of a feature into the table defined
     * by the specified {@link SchemaTable}.
     * <p>
     * If an entry exists with the same primary key as one of the instances
     * given to this method, that entry should be updated instead.
     *
     * @param table  the {@link SchemaTable} to use
     * @param values the feature instances to insert
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean insertEntries(SchemaTable table, Feature[] values);

    /**
     * This method inserts new instances of a feature, consisting
     * only of the primary keys, into the table defined by the specified
     * {@link SchemaTable}, but <i>ONLY</i> if each primary key is not
     * already in the table.
     * <p>
     * This facilitates foreign key mapping within Jetway by creating
     * placeholder objects with primary keys to prevent issues if the
     * actual object for that foreign key is not loaded yet.
     *
     * @param table       the {@link SchemaTable} to use
     * @param primaryKeys the primary keys to insert
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public abstract boolean insertPrimaryKeys(SchemaTable table, Object[] primaryKeys);

    /**
     * This method executes a {@link Query} on the database and returns the
     * set of results obtained.
     * <p>
     * The order of results is defined by the specified {@link Sort} instance,
     * unless that instance is {@code null}, in which case the order
     * of the results is not guaranteed.
     *
     * @param table the table to query from
     * @param query the {@link Query} to run
     * @param sort  the {@link Sort} defining the order to use
     * @return The set of results from the query
     */
    public abstract DatabaseResult runQuery(SchemaTable table, Query query, Sort sort);

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

            MultiQuery multiQuery = (MultiQuery) query;
            buildReferencedTablesFromSubQueries(multiQuery, tables);

        } else if (query instanceof SingleQuery) {

            SingleQuery singleQuery = (SingleQuery) query;
            buildReferencedTablesFromForeignKeys(singleQuery, tables);
        }
    }

    private void buildReferencedTablesFromSubQueries(MultiQuery multiQuery, Set<SchemaTable> tables) {

        for (Query subQuery : multiQuery.getQueries())
            buildReferencedTablesSet(subQuery, tables);
    }

    private void buildReferencedTablesFromForeignKeys(SingleQuery singleQuery, Set<SchemaTable> tables) {

        Class<? extends Feature> feature = singleQuery.getFeature();
        SchemaTable table = SchemaManager.get(feature);
        addAllForeignTables(table, tables);
    }

    private void addAllForeignTables(SchemaTable table, Set<SchemaTable> tables) {

        // Add itself
        tables.add(table);

        // Process any foreign tables recursively
        for (String foreignKey : table.getForeignKeys()) {

            ForeignKeyData foreignKeyData = table.getForeignKeyData(foreignKey);

            // If the table "belongs to" the foreign feature, add it
            if (foreignKeyData.getRelationship() == Relationship.BELONGS_TO) {

                SchemaTable foreignTable = foreignKeyData.getFeatureTable();
                addAllForeignTables(foreignTable, tables);
            }
        }
    }
}
