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

import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.exceptions.DatabaseException;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class handles databases using MySQL.
 *
 * @author Christopher Lutz
 */
public class MySQLDatabaseManager extends SQLDatabaseManager {

    private final MysqlDataSource dataSource;
    private Connection connection;

    /**
     * Creates a new instance of this database manager
     */
    public MySQLDatabaseManager() {

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

            DatabaseException exception = new DatabaseException("An error occurred while connecting to the MySQL database.", e);
            JetwayLog.getDatabaseLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    public boolean setForDrops(boolean isDropping) {

        // Disable or enable foreign key checks so that tables can be dropped in bulk
        JetwayLog.getDatabaseLogger().info("Turning MySQL foreign key checks " + (isDropping ? "off" : "on") + "...");
        String query = format("SET FOREIGN_KEY_CHECKS = {0};", isDropping ? 0 : 1);
        return execute(query);
    }

    @Override
    public Connection getConnection() {

        return connection;
    }

    @Override
    protected String getSQLType(DatabaseType type) {

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
                return "BOOLEAN";
            case TEXT:
            default:
                return "TEXT";
        }
    }

    @Override
    protected String formatAsSQLType(DatabaseType type, Object value) {

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
}