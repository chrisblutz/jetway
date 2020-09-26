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
import com.mysql.cj.jdbc.MysqlDataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This class handles databases using MySQL.
 *
 * @author Christopher Lutz
 */
public class MySQLDataManager extends DatabaseManager {

    private final MysqlDataSource dataSource;
    private Connection connection;

    /**
     * Creates a new instance of this database manager
     */
    public MySQLDataManager() {

        dataSource = new MysqlDataSource();
    }

    @Override
    public String getCommandLineIdentifier() {

        return "mysql";
    }

    @Override
    public void setServer(String server) {

        dataSource.setServerName(server);
    }

    @Override
    public void setDatabaseName() {

        dataSource.setDatabaseName(DATABASE_NAME);
    }

    @Override
    public void setUser(String user) {

        dataSource.setUser(user);
    }

    @Override
    public void setPassword(String password) {

        dataSource.setPassword(password);
    }

    @Override
    public boolean setupConnection() {

        try {

            dataSource.setServerTimezone("UTC");
            connection = dataSource.getConnection();
            return true;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while connecting to the database.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public boolean createDatabase() {

        JetwayLog.getDatabaseLogger().info("Setting up database '" + DATABASE_NAME + "' if it doesn't exist already...");
        boolean create = execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME + ";");
        JetwayLog.getDatabaseLogger().info("Setting up MySQL to use '" + DATABASE_NAME + "'...");
        boolean use = execute("USE " + DATABASE_NAME + ";");
        return create && use;
    }

    @Override
    public void closeConnection() {

        try {

            if (connection != null)
                connection.close();

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while closing the connection to the database.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public String getJetwayVersion() {

        // Check to make sure that jetway_version table exists
        if (!checkVersionTableExists()) {
            JetwayLog.getDatabaseLogger().info("Jetway version table does not exist, defaulting to 'null' version.");
            return null;
        }

        // Select the current version from jetway_version
        JetwayLog.getDatabaseLogger().info("Retrieving Jetway version from database...");
        String query = "SELECT version FROM jetway_version;";
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

            String version = resultSet.getString("version");
            statement.close();

            return version;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while retrieving Jetway version data.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private boolean checkVersionTableExists() {

        // Check INFORMATION_SCHEMA to see if jetway_version table exists
        JetwayLog.getDatabaseLogger().info("Checking to see if Jetway version table exists...");
        String query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;";
        Statement statement = executeWithResult(query);

        // If statement is null (i.e. something went wrong during execution, return false
        if (statement == null)
            return false;

        try {

            // Search through available table names
            boolean exists = false;
            ResultSet resultSet = statement.getResultSet();
            String column;
            while (resultSet.next()) {
                column = resultSet.getString("TABLE_NAME");

                if (column.equals("jetway_version")) {
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

    @Override
    public boolean setJetwayVersion(String version) {

        // Drop the jetway_version table if it exists
        JetwayLog.getDatabaseLogger().info("Dropping Jetway version table if it exists...");
        String dropQuery = "DROP TABLE IF EXISTS jetway_version;";
        boolean drop = execute(dropQuery);

        // Recreate the jetway_version table
        JetwayLog.getDatabaseLogger().info("Recreating Jetway version table with 'version' column...");
        String createQuery = "CREATE TABLE IF NOT EXISTS jetway_version (\n\tversion VARCHAR(50) NOT NULL,\n\tPRIMARY KEY(version));";
        boolean create = execute(createQuery);

        // Insert current version into jetway_version table
        JetwayLog.getDatabaseLogger().info("Storing Jetway version in database...");
        String insertQuery = format("INSERT INTO jetway_version VALUES ({0});", formatAsSQLTypeStandalone(DatabaseType.STRING, version));
        boolean insert = execute(insertQuery);

        return drop && create && insert;
    }

    @Override
    public boolean buildTable(SchemaTable table) {

        String query = format("CREATE TABLE IF NOT EXISTS {0} (\n{1}\n);", table.getTableName(), getAttributesAsSQL(table));
        return execute(query);
    }

    @Override
    public boolean dropTable(SchemaTable table) {

        String query = format("DROP TABLE IF EXISTS {0};", table.getTableName());
        return execute(query);
    }

    @Override
    public boolean setForDrops(boolean isDropping) {

        // Disable or enable foreign key checks so that tables can be dropped in bulk
        JetwayLog.getDatabaseLogger().info("Turning MySQL foreign key checks " + (isDropping ? "off" : "on") + "...");
        String query = format("SET FOREIGN_KEY_CHECKS = {0};", isDropping ? 0 : 1);
        return execute(query);
    }

    private String getAttributesAsSQL(SchemaTable table) {

        // Start with primary key definition
        String primary = table.getPrimaryKey();
        String primarySQLType = getSQLType(table.getAttributeType(primary));
        StringBuilder attributes = new StringBuilder(format("\t{0} {1} NOT NULL", primary, primarySQLType));

        // Append foreign key definition if this table has one
        if (table.hasForeignKey()) {

            String foreign = table.getForeignKey();
            String foreignSQLType = getSQLType(table.getAttributeType(foreign));
            attributes.append(format(",\n\t{0} {1} NOT NULL", foreign, foreignSQLType));
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

        // If the table has a foreign key, declare that column
        if (table.hasForeignKey()) {

            String foreign = table.getForeignKey();
            String foreignTable = table.getForeignTable().getTableName();
            String foreignTablePrimary = table.getForeignTable().getPrimaryKey();
            attributes.append(format(",\n\tFOREIGN KEY ({0})\n\t\tREFERENCES {1}({2})\n\t\tON DELETE CASCADE",
                    foreign, foreignTable, foreignTablePrimary));
        }

        return attributes.toString();
    }

    private String getSQLType(DatabaseType type) {

        switch (type) {
            case INTEGER:
                return "INTEGER";
            case FLOAT:
                return "FLOAT";
            case DOUBLE:
                return "DOUBLE PRECISION";
            case STRING:
                return "VARCHAR(255)";
            case BOOLEAN:
                return "BIT";
            case TEXT:
            default:
                return "TEXT";
        }
    }

    @Override
    public boolean insertEntry(SchemaTable table, Object value) {

        String query = format("REPLACE INTO {0} VALUES ({1});", table.getTableName(), getValuesAsSQL(table, value));
        return execute(query);
    }

    @Override
    public DatabaseResult runQuery(SchemaTable table, Query query, Sort sort) {

        String queryString = buildFullQuery(table, query, sort);
        Statement statement = executeWithResult(queryString);

        DatabaseResult result = new DatabaseResult();

        if (statement == null)
            return result;

        try {

            ResultSet set = statement.getResultSet();
            while (set.next()) {

                Map<String, Object> currentResult = new HashMap<>();

                for (String attr : table.getAttributes()) {

                    Field f = table.getField(attr);
                    currentResult.put(attr, getFromResultSetAsFieldType(set, attr, f.getType()));
                }

                result.add(currentResult);
            }

            return result;

        } catch (SQLException e) {

            DatabaseException exception = new DatabaseException("An error occurred while selecting information from table '" + table.getTableName() + "'.", e);
            JetwayLog.getDatabaseLogger().warn(exception.getMessage(), exception);
            return result;
        }
    }

    private String buildFullQuery(SchemaTable table, Query query, Sort sort) {

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

        if (sort != null) {

            queryString += " ORDER BY ";
            queryString += sort.getTable().getTableName() + "." + sort.getAttribute();
            queryString += " ";

            switch (sort.getOrder()) {

                case ASCENDING:
                    queryString += "ASC";
                    break;

                case DESCENDING:
                    queryString += "DESC";
                    break;
            }
        }

        return queryString + ";";
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
            if (table.hasForeignKey()) {
                SchemaTable foreignTable = table.getForeignTable();
                joinConditions.add(table.getTableName() + "." + table.getForeignKey() + " = " + foreignTable.getTableName() + "." + foreignTable.getPrimaryKey());
            }
        }

        return joinConditions;
    }

    private String buildQuery(Query query) {

        if (query == null)
            return null;
        if (query instanceof AndQuery)
            return buildNestedQuery(((AndQuery) query).getQueries(), "AND");
        else if (query instanceof OrQuery)
            return buildNestedQuery(((OrQuery) query).getQueries(), "OR");
        else if (query instanceof SingleQuery)
            return buildSingleQuery((SingleQuery) query);
        else {
            // Unrecognized query type
            JetwayLog.getDatabaseLogger().warn("Invalid query type found: " + query.getClass().getName());
            return null;
        }
    }

    private String buildNestedQuery(List<Query> queries, String keyword) {

        List<String> parts = new ArrayList<>();
        for (Query query : queries) {

            if (query instanceof AndQuery)
                parts.add("(" + buildNestedQuery(((AndQuery) query).getQueries(), "AND") + ")");
            else if (query instanceof OrQuery)
                parts.add("(" + buildNestedQuery(((OrQuery) query).getQueries(), "OR") + ")");
            else if (query instanceof SingleQuery)
                parts.add(buildSingleQuery((SingleQuery) query));
            else {
                // Unrecognized query type
                JetwayLog.getDatabaseLogger().warn("Invalid query type found: " + query.getClass().getName());
            }
        }
        return String.join(" " + keyword + " ", parts);
    }

    private String buildSingleQuery(SingleQuery query) {

        SchemaTable table = SchemaManager.get(query.getFeature());
        String attr = table.getTableName() + "." + query.getAttribute();
        String expected = formatAsSQLTypeStandalone(table.getAttributeType(query.getAttribute()), query.getExpectedValue());
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
                return attr + " LIKE " + expected;
            default:
                // Invalid operation, shouldn't happen as operation is Enum type
                JetwayLog.getDatabaseLogger().warn("Invalid query operation found: " + query.getOperation().name());
                return null;
        }
    }

    private Object getFromResultSetAsFieldType(ResultSet set, String column, Class<?> type) throws SQLException {

        if (set.getObject(column) == null)
            return null;

        if (type == String.class)
            return set.getString(column);
        else if (type == Short.class)
            return set.getShort(column);
        else if (type == Integer.class)
            return set.getInt(column);
        else if (type == Long.class)
            return set.getLong(column);
        else if (type == Float.class)
            return set.getFloat(column);
        else if (type == Double.class)
            return set.getDouble(column);
        else if (type == Boolean.class)
            return set.getBoolean(column);
        else
            return DataConversion.getFromString(set.getString(column), type);
    }

    private String getValuesAsSQL(SchemaTable table, Object value) {

        // Start with primary key value
        String primary = table.getPrimaryKey();
        StringBuilder values = new StringBuilder(formatAsSQLType(table, primary, value));

        // Append foreign key value if this table has one
        if (table.hasForeignKey())
            values.append(format(", {0}", formatAsSQLType(table, table.getForeignKey(), value)));

        // Append all further attributes as long as they are not special (i.e. primary or foreign keys)
        for (String attribute : table.getAttributes()) {

            if (table.isSpecialKey(attribute))
                continue;

            values.append(format(", {0}", formatAsSQLType(table, attribute, value)));
        }

        return values.toString();
    }

    private String formatAsSQLType(SchemaTable table, String attribute, Object value) {

        try {

            Field field = table.getField(attribute);
            Object attributeValue = field.get(value);

            // If value is null, return NULL
            if (attributeValue == null)
                return "NULL";

            // Format object
            DatabaseType type = table.getAttributeType(attribute);
            return formatAsSQLTypeStandalone(type, attributeValue);

        } catch (IllegalAccessException e) {

            DatabaseException exception = new DatabaseException("An error occurred while retrieving information from the field for column '" + attribute + "'.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private String formatAsSQLTypeStandalone(DatabaseType type, Object value) {

        switch (type) {

            case INTEGER:
            case FLOAT:
            case DOUBLE:
                return value.toString();
            case STRING:
            case TEXT:
                return "\"" + value.toString().replace("\"", "\\\"") + "\"";
            case BOOLEAN:
                return (Boolean) value ? "1" : "0";
            default:
                return "NULL";
        }
    }

    private boolean execute(String query) {

        try {

            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
            return true;

        } catch (SQLException e) {

            throwQueryException(e, query);
            return false;
        }
    }

    private Statement executeWithResult(String query) {

        try {

            Statement statement = connection.createStatement();
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
