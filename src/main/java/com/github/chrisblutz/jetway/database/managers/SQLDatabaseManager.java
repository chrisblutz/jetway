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
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.database.queries.*;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.utils.ResultPair;

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
     * This field defines the name of the Jetway version table in the database.
     */
    protected static final String VERSION_TABLE = "jetway_version";
    /**
     * This field defines the name of the column containing the version of
     * Jetway used to generate the database.
     */
    protected static final String VERSION_COLUMN = "version";

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
     * This method converts the specified {@link Object}, which is a
     * value of the specified {@link DatabaseType}, to the {@link String}
     * representation of the SQL value.
     * <p>
     * These values are used in SQL {@code INSERT} statements, so they
     * should include all necessary punctuation or delimiters (i.e.
     * quotation marks for string values, etc.").
     *
     * @param type  the {@link DatabaseType} of the value
     * @param value the value itself
     * @return The value formatted as a SQL type
     */
    protected abstract String formatAsSQLType(DatabaseType type, Object value);

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
    public void closeConnection() {

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
    public String getJetwayVersion() {

        // Check that version table exists in the database
        if (!checkVersionTableExists()) {
            JetwayLog.getDatabaseLogger().info("Jetway version table does not exist, defaulting to 'null' version.");
            return null;
        }

        // Select the current version from the version table
        return getVersionFromVersionTable();
    }

    private boolean checkVersionTableExists() {

        // Check INFORMATION_SCHEMA to see if version table exists
        JetwayLog.getDatabaseLogger().info("Checking to see if Jetway version table exists...");
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;";
        Statement statement = executeWithResult(query);

        // If statement is null (i.e. something went wrong during execution, return false
        if (statement == null)
            return false;

        return checkResultsForTable(statement);
    }

    private boolean checkResultsForTable(Statement statement) {

        try {

            // Search through available table names for the version table name
            ResultSet resultSet = statement.getResultSet();

            boolean exists = false;
            while (resultSet.next()) {
                String column = resultSet.getString("TABLE_NAME");

                // If column name is found, set exists to true
                if (column.equals(VERSION_TABLE)) {
                    exists = true;
                    break;
                }
            }

            statement.close();
            return exists;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while checking if Jetway version data exists.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private String getVersionFromVersionTable() {

        // Select the current version from the version table
        JetwayLog.getDatabaseLogger().info("Retrieving Jetway version from database...");
        String query = "SELECT " + VERSION_COLUMN + " FROM " + VERSION_TABLE + ";";
        Statement statement = executeWithResult(query);

        // If statement is null (i.e. something went wrong during execution, return null
        if (statement == null)
            return null;

        try {

            ResultSet resultSet = statement.getResultSet();

            // If result set has no next line, then the table does not contain a version
            if (!resultSet.next()) {
                JetwayLog.getDatabaseLogger().info("No version data present in the database, defaulting to 'null' version.");
                return null;
            }

            String version = resultSet.getString(VERSION_COLUMN);

            statement.close();
            return version;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while retrieving Jetway version data.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public boolean setJetwayVersion(String version) {

        // Create the jetway_version table if it doesn't exist
        JetwayLog.getDatabaseLogger().info("Recreating Jetway version table with '" + VERSION_COLUMN + "' column...");
        String createQuery = "CREATE TABLE IF NOT EXISTS " + VERSION_TABLE + " (\n\t" + VERSION_COLUMN +
                " VARCHAR(50) NOT NULL,\n\tPRIMARY KEY(" + VERSION_COLUMN + "));";
        boolean create = execute(createQuery);

        // Insert current version into jetway_version table
        JetwayLog.getDatabaseLogger().info("Inserting Jetway version into table '" + VERSION_TABLE + "'...");
        String insertQuery = format("INSERT INTO " + VERSION_TABLE + " VALUES ({0});", formatAsSQLType(DatabaseType.STRING, version));
        boolean insert = execute(insertQuery);

        return create && insert;
    }

    @Override
    public boolean dropVersionTable() {

        // Drop the version table if it exists
        JetwayLog.getDatabaseLogger().info("Dropping Jetway version table if it exists...");
        String dropQuery = "DROP TABLE IF EXISTS " + VERSION_TABLE + ";";
        return execute(dropQuery);
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
            attributes.append(format(",\n\t{0} {1} NULL", foreignKey, foreignSQLType));
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

            SchemaTable foreignTable = table.getForeignTable(foreignKey);
            String foreignTableName = foreignTable.getTableName();
            String foreignTablePrimary = foreignTable.getPrimaryKey();
            attributes.append(format(",\n\tFOREIGN KEY ({0})\n\t\tREFERENCES {1}({2})\n\t\tON DELETE CASCADE",
                    foreignKey, foreignTableName, foreignTablePrimary));
        }

        return attributes.toString();
    }

    @Override
    public boolean dropTable(SchemaTable table) {

        String query = format("DROP TABLE IF EXISTS {0};", table.getTableName());
        return execute(query);
    }

    @Override
    public boolean insertEntry(SchemaTable table, Object value) {

        // First is attribute list, second is value list
        ResultPair<String, String> results = getValuesAsSQL(table, value);

        // Insert into database, replacing any entries already present
        String query = format("REPLACE INTO {0} ({1}) VALUES ({2});", table.getTableName(), results.getFirst(), results.getSecond());
        return execute(query);
    }

    private ResultPair<String, String> getValuesAsSQL(SchemaTable table, Object value) {

        // Start with primary key value
        String primary = table.getPrimaryKey();
        StringBuilder attributes = new StringBuilder(primary);
        StringBuilder values = new StringBuilder(formatAttributeAsSQLType(table, primary, value));

        // Append foreign key values if this table has any
        for (String foreignKey : table.getForeignKeys()) {

            attributes.append(", ").append(foreignKey);
            values.append(", ").append(formatAttributeAsSQLType(table, foreignKey, value));
        }

        // Append all further attributes as long as they are not special (i.e. primary or foreign keys)
        for (String attribute : table.getAttributes()) {

            if (table.isSpecialKey(attribute))
                continue;

            attributes.append(", ").append(attribute);
            values.append(", ").append(formatAttributeAsSQLType(table, attribute, value));
        }

        return new ResultPair<>(attributes.toString(), values.toString());
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

    @Override
    public boolean insertPrimaryKey(SchemaTable table, Object primaryKey) {

        String keyAttribute = table.getPrimaryKey();
        String formattedKey = formatAsSQLType(table.getAttributeType(keyAttribute), primaryKey);

        // Insert key into table, and if it already exists, set the ID to itself (i.e. change nothing)
        String query = format("INSERT INTO {0} ({1}) VALUES ({2}) ON DUPLICATE KEY UPDATE {1} = {1};", table.getTableName(), table.getPrimaryKey(), formattedKey);
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

                SchemaTable foreignTable = table.getForeignTable(foreignKey);

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
