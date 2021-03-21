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

import com.github.chrisblutz.jetway.conversion.DataConversion;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.exceptions.DatabaseException;
import com.github.chrisblutz.jetway.database.keys.ForeignKeyData;
import com.github.chrisblutz.jetway.database.keys.Relationship;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.metadata.BasicMetadata;
import com.github.chrisblutz.jetway.database.metadata.Metadata;
import com.github.chrisblutz.jetway.database.queries.*;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This class manages SQL database connections, and is
 * extended by specific database manager classes to offer
 * manager-specific functionality (like type definitions, etc.).
 *
 * @author Christopher Lutz
 */
public abstract class SQLDatabaseManager extends DatabaseManager {

    /**
     * This field defines the name of the table containing Jetway's
     * metadata information.
     */
    protected static final String METADATA_TABLE = "metadata";

    /**
     * This field defines the name of the primary key used in the metadata table
     */
    protected static final String METADATA_KEY = "id";

    /**
     * This field defines the type of the primary key used in the metadata table
     */
    protected static final DatabaseType METADATA_KEY_TYPE = DatabaseType.INTEGER;

    /**
     * This field defines the value of the primary key used in the metadata table,
     * since this is a singleton table (only one row)
     */
    protected static final int METADATA_KEY_VALUE = 0;

    /**
     * This method retrieves the SQL {@link Connection} to the database.
     *
     * @return The {@link Connection} to the database
     */
    protected abstract Connection getConnection();

    /**
     * This method gets the SQL type that should be used to store values
     * of the specified {@link DatabaseType}.
     *
     * @param type the database type
     * @return The SQL type used to store values
     */
    protected abstract String getSQLType(DatabaseType type);

    /**
     * This method converts the specified {@link String}, which is a
     * representation of a value of the specified {@link DatabaseType},
     * to the {@link String} representation of the SQL value.  This
     * may include adding quotes to a string/text value, altering
     * capitalization for keywords, etc.
     * <p>
     * These values are used in SQL {@code INSERT} statements, so they
     * should include all necessary punctuation or delimiters (i.e.
     * quotation marks for string values, etc.").
     *
     * @param type  the {@link DatabaseType} of the value
     * @param value the value itself as a {@link String}
     * @return The value formatted as a SQL type
     */
    protected abstract String formatAsSQLType(DatabaseType type, String value);

    /**
     * This method converts the specified {@link Object}, which is a
     * value of the specified {@link DatabaseType}, to the {@link String}
     * representation of the SQL value.
     * <p>
     * These values are used in SQL {@code INSERT} statements, so they
     * should include all necessary punctuation or delimiters (i.e.
     * quotation marks for string values, etc.").
     * <p>
     * Internally, this method converts the value into a {@link String}
     * using {@link DataConversion}'s string conversion methods,
     * then passes that {@link String} off to
     * {@link #formatAsSQLType(DatabaseType, String)}
     * for final formatting.
     *
     * @param type  the {@link DatabaseType} of the value
     * @param value the value itself
     * @return The value formatted as a SQL type
     */
    protected String formatAsSQLType(DatabaseType type, Object value) {

        // Convert to string and pass to database-specific manager
        // for final formatting
        String string = DataConversion.convertToString(value);
        return formatAsSQLType(type, string);
    }

    @Override
    public boolean createDatabase() {

        // Create database unless it already exists
        JetwayLog.getDatabaseLogger().info("Setting up database '" + DATABASE_NAME + "' if it doesn't exist already...");
        boolean create = execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME + ";");

        // Set SQL manager to use the Jetway database
        JetwayLog.getDatabaseLogger().info("Setting up SQL to use database '" + DATABASE_NAME + "'...");
        boolean use = execute("USE " + DATABASE_NAME + ";");
        return create && use;
    }

    @Override
    protected void closeConnectionSpecific() {

        try {

            if (getConnection() != null)
                getConnection().close();

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while closing the connection to the SQL database.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public <T> T getMetadata(BasicMetadata<T> metadata) {

        // If metadata table does not exist, return null
        if (!getMetadataTableExists())
            return null;

        // Extract value from table
        String value = getMetadataColumnValue(metadata.getName());

        // If value is null, return null (don't try to convert)
        if (value == null)
            return null;

        // If value is not null, convert to type and return
        return metadata.fromString(value);
    }

    @Override
    public <T> boolean setMetadata(BasicMetadata<T> metadata, T value) {

        // Create metadata table (will not do anything if table already exists)
        if (!createMetadataTable())
            return false;

        // If value is null, insert null into table.  Otherwise convert to String.
        String valueString = value != null ? metadata.toString(value) : null;

        return setMetadataColumnValue(metadata.getName(), valueString);
    }

    @Override
    public boolean clearMetadata() {

        // Drop the metadata table
        JetwayLog.getDatabaseLogger().info("Dropping metadata table if it exists...");
        String dropQuery = "DROP TABLE IF EXISTS " + METADATA_TABLE + ";";
        return execute(dropQuery);
    }

    private boolean getMetadataTableExists() {

        // Select the table name from the database schema
        JetwayLog.getDatabaseLogger().info("Checking to see if metadata table exists...");
        String query = "SELECT COUNT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = \"" + METADATA_TABLE + "\";";
        Statement statement = executeWithResult(query);

        // If statement is null, check failed
        if (statement == null)
            return false;

        try {

            ResultSet resultSet = statement.getResultSet();

            // If result set is null, check failed
            if (resultSet == null)
                return false;

            // Check for a row
            if (!resultSet.next())
                return false;

            // Check that the count is 1
            return resultSet.getObject("COUNT(TABLE_NAME)", Integer.class).equals(1);

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while checking if metadata table exists.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private boolean createMetadataTable() {

        // Create the jetway_version table if it doesn't exist
        JetwayLog.getDatabaseLogger().info("Creating metadata table...");
        String query = format("CREATE TABLE IF NOT EXISTS {0} (\n{1}\n);", METADATA_TABLE, getMetadataAttributes());
        return execute(query);
    }

    private String getMetadataAttributes() {

        // Get SQL types for the primary key and all metadata columns
        String primarySQLType = getSQLType(METADATA_KEY_TYPE);
        String metadataSQLType = getSQLType(DatabaseType.STRING);

        // Start string with primary key entry
        StringBuilder attributes = new StringBuilder(format("\t{0} {1} NOT NULL", METADATA_KEY, primarySQLType));

        // Create new line for each metadata column
        for (BasicMetadata<?> metadata : Metadata.METADATA)
            attributes.append(format(",\n\t{0} {1}", metadata.getName(), metadataSQLType));

        // Declare the primary key column
        attributes.append(format(",\n\tPRIMARY KEY ({0})", METADATA_KEY));

        return attributes.toString();
    }

    private String getMetadataColumnValue(String column) {

        // Convert primary key to SQL string
        String primaryKey = formatAsSQLType(METADATA_KEY_TYPE, METADATA_KEY_VALUE);

        // Pull value from table (extract singleton row)
        String query = format("SELECT {0} FROM {1} WHERE {2} = {3};", column, METADATA_TABLE, METADATA_KEY, primaryKey);
        Statement statement = executeWithResult(query);

        // If statement is null, return null
        if (statement == null)
            return null;

        try {

            ResultSet resultSet = statement.getResultSet();

            // If result set is null, return null
            if (resultSet == null)
                return null;

            // Check for a row, and if one does not exist, return null
            if (!resultSet.next())
                return null;

            // Extract column value as string
            return resultSet.getString(column);

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while retrieving '" + column + "' metadata.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private boolean setMetadataColumnValue(String column, String value) {

        // Convert primary key to SQL string
        String primaryKey = formatAsSQLType(METADATA_KEY_TYPE, METADATA_KEY_VALUE);

        // Convert value to SQL value
        String sqlValue = formatAsSQLType(DatabaseType.STRING, value);

        // Create list of attributes/values, in this case it will be primary key followed by the attribute being changed
        String[] attributes = new String[]{METADATA_KEY, column};
        String[] values = new String[]{primaryKey, sqlValue};

        // Insert entry into database, and if it already exists, update column to new value
        String query = format("INSERT INTO {0} ({1}) VALUES\n\t({2})\nON DUPLICATE KEY UPDATE\n\t{3} = {4};", METADATA_TABLE, String.join(", ", attributes), String.join(",\n\t", values), column, sqlValue);
        return execute(query);
    }

    @Override
    public boolean buildTable(SchemaTable table) {

        String query = format("CREATE TABLE IF NOT EXISTS {0} (\n{1}\n);", table.getTableName(), getAttributesAsSQL(table));
        return execute(query);
    }

    private String getAttributesAsSQL(SchemaTable table) {

        // Start with primary key definition
        String primary = table.getPrimaryKey();
        String primarySQLType = getSQLType(table.getAttributeType(primary));
        StringBuilder attributes = new StringBuilder(format("\t{0} {1} NOT NULL", primary, primarySQLType));

        // Append foreign key definitions if this table has any
        for (String foreignKey : table.getForeignKeys()) {

            String foreignSQLType = getSQLType(table.getAttributeType(foreignKey));
            attributes.append(format(",\n\t{0} {1}", foreignKey, foreignSQLType));
        }

        // Append all further attribute definitions  as long as they are not special (i.e. primary or foreign keys)
        for (String attribute : table.getAttributes()) {

            if (table.isSpecialKey(attribute))
                continue;

            String attributeSQLType = getSQLType(table.getAttributeType(attribute));
            attributes.append(format(",\n\t{0} {1}", attribute, attributeSQLType));
        }

        // Declare the primary key column
        attributes.append(format(",\n\tPRIMARY KEY ({0})", primary));

        // If the table has any foreign key, declare them
        for (String foreignKey : table.getForeignKeys()) {

            ForeignKeyData foreignKeyData = table.getForeignKeyData(foreignKey);

            SchemaTable foreignTable = foreignKeyData.getFeatureTable();
            String foreignTableName = foreignTable.getTableName();
            String foreignTablePrimary = foreignTable.getPrimaryKey();
            String deleteOp = getDeleteOp(foreignKeyData.getRelationship());

            attributes.append(format(",\n\tFOREIGN KEY ({0})\n\t\tREFERENCES {1}({2})\n\t\tON DELETE {3}",
                    foreignKey, foreignTableName, foreignTablePrimary, deleteOp));
        }

        return attributes.toString();
    }

    private String getDeleteOp(Relationship relationship) {

        switch (relationship) {

            case USES:
                return "SET NULL";

            case BELONGS_TO:
            default:
                return "CASCADE";
        }
    }

    @Override
    public boolean dropTable(SchemaTable table) {

        String query = format("DROP TABLE IF EXISTS {0};", table.getTableName());
        return execute(query);
    }

    @Override
    public boolean insertEntries(SchemaTable table, Feature[] values) {

        // If there are no entries to insert, complete successfully
        if (values.length == 0)
            return true;

        // Retrieve list of attributes as an array (to preserve order)
        String[] attributes = table.getAttributes().toArray(new String[0]);

        // Get list of SQL values for all features
        String[] valueArray = new String[values.length];
        for (int i = 0; i < values.length; i++)
            valueArray[i] = getValuesAsSQL(attributes, table, values[i]);

        // Generate attribute mapping for ON DUPLICATE KEY UPDATE clause
        String duplicateUpdate = getOnDuplicateUpdateClause(attributes);

        // Insert all into database, replacing any entries already present
        String query = format("INSERT INTO {0} ({1}) VALUES\n\t{2}\nON DUPLICATE KEY UPDATE\n\t{3};", table.getTableName(), String.join(", ", attributes), String.join(",\n\t", valueArray), duplicateUpdate);
        return execute(query);
    }

    private String getValuesAsSQL(String[] attributeOrder, SchemaTable table, Object value) {

        String[] values = new String[attributeOrder.length];

        // Convert all values to SQL values
        for (int i = 0; i < attributeOrder.length; i++) {

            String attribute = attributeOrder[i];
            values[i] = formatAttributeAsSQLType(table, attribute, value);
        }

        return "(" + String.join(", ", values) + ")";
    }

    private String formatAttributeAsSQLType(SchemaTable table, String attribute, Object value) {

        try {

            Field field = table.getField(attribute);
            Object attributeValue = field.get(value);

            // If value is null, return NULL
            if (attributeValue == null)
                return "NULL";

            // Format object
            DatabaseType type = table.getAttributeType(attribute);
            return formatAsSQLType(type, attributeValue);

        } catch (IllegalAccessException e) {

            DatabaseException exception = new DatabaseException("An error occurred while retrieving information from the field for column '" + attribute + "'.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private String getOnDuplicateUpdateClause(String[] attributes) {

        // Generate attr=VALUES(attr) clause for every attribute
        String[] updates = new String[attributes.length];
        for (int i = 0; i < attributes.length; i++) {

            String attribute = attributes[i];
            updates[i] = attribute + " = VALUES(" + attribute + ")";
        }
        return String.join(",\n\t", updates);
    }

    @Override
    public boolean insertPrimaryKeys(SchemaTable table, Object[] primaryKeys) {

        // If there are no keys to insert, complete successfully
        if (primaryKeys.length == 0)
            return true;

        String keyAttribute = table.getPrimaryKey();

        // Generate keys as SQL values for each key
        String[] formattedKeys = new String[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++)
            formattedKeys[i] = "(" + formatAsSQLType(table.getAttributeType(keyAttribute), primaryKeys[i]) + ")";

        // Insert keys into table, and if one already exists, set the ID to itself (i.e. change nothing)
        String query = format("INSERT INTO {0} ({1}) VALUES {2} ON DUPLICATE KEY UPDATE {1} = {1};", table.getTableName(), table.getPrimaryKey(), String.join(", ", formattedKeys));
        return execute(query);
    }

    @Override
    public DatabaseResult runQuery(SchemaTable table, Query query, Sort sort) {

        String queryString = buildQueryAsString(table, query, sort);
        Statement statement = executeWithResult(queryString);

        DatabaseResult result = new DatabaseResult();

        if (statement == null)
            return result;

        try {

            // Fill in the result mapping using each row of the result set
            ResultSet set = statement.getResultSet();
            while (set.next()) {

                Map<String, Object> currentResult = new HashMap<>();

                // Fill in the map using all attributes in the table
                for (String attr : table.getAttributes()) {

                    Field f = table.getField(attr);
                    currentResult.put(attr, getObjectFromResultSetAsType(set, attr, f.getType()));
                }

                result.add(currentResult);
            }

            statement.close();
            return result;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while selecting information from table '" + table.getTableName() + "'.", e);
            JetwayLog.getDatabaseLogger().warn(exception.getMessage(), exception);
            return result;
        }
    }

    private String buildQueryAsString(SchemaTable table, Query query, Sort sort) {

        String queryString = "SELECT " + table.getTableName() + ".* FROM ";

        Set<SchemaTable> referencedTables = getReferencedTables(query);
        // Make sure selection table is included
        referencedTables.add(table);

        Set<String> tableNames = getTableNames(referencedTables);
        queryString += String.join(", ", tableNames);

        // Track if a "WHERE" has been inserted in the query yet
        boolean where = false;
        if (referencedTables.size() > 1) {

            Set<String> joinConditions = getJoinConditions(referencedTables);
            queryString += " WHERE " + String.join(" AND ", joinConditions);
            where = true;
        }

        String whereString = buildQuery(query);
        if (whereString != null) {
            if (where)
                queryString += " AND ";
            else
                queryString += " WHERE ";

            queryString += "(" + whereString + ")";
        }

        if (sort != null)
            queryString += " " + buildSortAsString(sort);

        return queryString + ";";
    }

    private String buildSortAsString(Sort sort) {

        String sortString = "ORDER BY ";
        sortString += sort.getTable().getTableName() + "." + sort.getAttribute();
        sortString += " ";

        switch (sort.getOrder()) {

            case ASCENDING:
                sortString += "ASC";
                break;

            case DESCENDING:
                sortString += "DESC";
                break;
        }

        return sortString;
    }

    private Set<String> getTableNames(Set<SchemaTable> referencedTables) {

        Set<String> names = new HashSet<>();

        for (SchemaTable table : referencedTables)
            names.add(table.getTableName());

        return names;
    }

    private Set<String> getJoinConditions(Set<SchemaTable> referencedTables) {

        Set<String> joinConditions = new HashSet<>();

        for (SchemaTable table : referencedTables) {

            // Go through any foreign keys that the current table has
            for (String foreignKey : table.getForeignKeys()) {

                SchemaTable foreignTable = table.getForeignKeyData(foreignKey).getFeatureTable();

                // If the foreign key refers to a table that is also referenced in the query, join on it
                if (referencedTables.contains(foreignTable))
                    joinConditions.add(table.getTableName() + "." + foreignKey + " = " +
                            foreignTable.getTableName() + "." + foreignTable.getPrimaryKey());
            }
        }

        return joinConditions;
    }

    private String buildQuery(Query query) {

        String result;
        if (query == null)
            result = null;
        else if (query instanceof AndQuery)
            result = buildNestedQuery(((AndQuery) query).getQueries(), "AND");
        else if (query instanceof OrQuery)
            result = buildNestedQuery(((OrQuery) query).getQueries(), "OR");
        else if (query instanceof SingleQuery)
            result = buildSingleQuery((SingleQuery) query);
        else {
            // Unrecognized query type
            JetwayLog.getDatabaseLogger().warn("Invalid query type found: " + query.getClass().getName());
            result = null;
        }

        return result;
    }

    private String buildNestedQuery(List<Query> queries, String keyword) {

        List<String> parts = new ArrayList<>();
        for (Query query : queries) {

            parts.add(buildNestedQueryComponent(query));
        }
        return String.join(" " + keyword + " ", parts);
    }

    private String buildNestedQueryComponent(Query query) {

        String result = "";
        if (query instanceof AndQuery) {

            AndQuery andQuery = (AndQuery) query;
            result = "(" + buildNestedQuery(andQuery.getQueries(), "AND") + ")";

        } else if (query instanceof OrQuery) {

            OrQuery orQuery = (OrQuery) query;
            result = "(" + buildNestedQuery(orQuery.getQueries(), "OR") + ")";

        } else if (query instanceof SingleQuery) {

            SingleQuery singleQuery = (SingleQuery) query;
            result = buildSingleQuery(singleQuery);

        } else {
            // Unrecognized query type
            JetwayLog.getDatabaseLogger().warn("Invalid query type found: " + query.getClass().getName());
        }

        return result;
    }

    private String buildSingleQuery(SingleQuery query) {

        SchemaTable table = SchemaManager.get(query.getFeature());
        String attr = table.getTableName() + "." + query.getAttribute();
        String expected = formatAsSQLType(table.getAttributeType(query.getAttribute()), query.getExpectedValue());

        switch (query.getOperation()) {
            case EQUALS:
                return attr + " = " + expected;
            case NOT_EQUALS:
                return attr + " <> " + expected;
            case GREATER_THAN:
                return attr + " > " + expected;
            case GREATER_THAN_EQUALS:
                return attr + " >= " + expected;
            case LESS_THAN:
                return attr + " < " + expected;
            case LESS_THAN_EQUALS:
                return attr + " <= " + expected;
            case LIKE:
                return attr + " LIKE " + expected; // TODO allow database managers to define LIKE delimiters
            default:
                // Invalid operation, shouldn't happen as operation is Enum type
                JetwayLog.getDatabaseLogger().warn("Invalid query operation found: " + query.getOperation().name());
                return null;
        }
    }

    private Object getObjectFromResultSetAsType(ResultSet set, String column, Class<?> type) throws SQLException {

        if (set.getObject(column) == null)
            return null;

        Object result;
        if (type == String.class)
            result = set.getString(column);
        else if (type == Short.class)
            result = set.getShort(column);
        else if (type == Integer.class)
            result = set.getInt(column);
        else if (type == Long.class)
            result = set.getLong(column);
        else if (type == Float.class)
            result = set.getFloat(column);
        else if (type == Double.class)
            result = set.getDouble(column);
        else if (type == Boolean.class)
            result = set.getBoolean(column);
        else
            result = DataConversion.getFromString(set.getString(column), type);

        return result;
    }

    /**
     * This method executes a SQL query and returns
     * if the operation was successful or not.  It
     * ignores the results of the execution, so if
     * results are needed, use {@link #executeWithResult(String)}.
     *
     * @param query the query to run
     * @return {@code true} if the query succeeded, {@code false} otherwise
     */
    protected boolean execute(String query) {

        try {

            // Execute the query and ignore the results
            Statement statement = getConnection().createStatement();
            statement.execute(query);
            statement.close();
            return true;

        } catch (SQLException e) {

            throwQueryException(e, query);
            return false;
        }
    }

    /**
     * This method executes a SQL query and returns the
     * resulting {@link Statement}.
     * <p>
     * It is the responsibility of the caller to close the
     * {@link Statement} when it is no longer needed, as
     * this method leaves it open for further operations.
     *
     * @param query the query to run
     * @return The resulting {@link Statement}
     */
    protected Statement executeWithResult(String query) {

        try {

            // Execute the query and return the results
            Statement statement = getConnection().createStatement();
            statement.execute(query);

            return statement;

        } catch (SQLException e) {

            throwQueryException(e, query);
            return null;
        }
    }

    private void throwQueryException(SQLException e, String query) {

        DatabaseException exception = new DatabaseException("An error occurred while executing a query.  Check the log file for the specific query.", e);
        JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
        JetwayLog.getDatabaseLogger().error("------[ BEGIN FAILED QUERY ]------\n" + query);
        JetwayLog.getDatabaseLogger().error("------[  END FAILED QUERY  ]------");
        throw exception;
    }
}
