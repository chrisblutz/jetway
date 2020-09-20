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
package com.github.chrisblutz.jetway.database.mappings;

import com.github.chrisblutz.jetway.aixm.exceptions.AIXMFeatureException;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.utils.TypeUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class represents a schema for a database table, based on
 * classes/fields annotated with {@link DatabaseTable} and
 * {@link DatabaseColumn} respectively.
 *
 * @author Christopher Lutz
 */
public class SchemaTable {

    private String tableName;
    private final Map<String, Field> fieldMap = new HashMap<>();
    private final Map<String, DatabaseType> typeMap = new HashMap<>();
    private final List<SchemaTable> childTables = new ArrayList<>();
    private String primaryKey, foreignKey;
    private Class<?> foreignClass;

    /**
     * This method retrieves the {@link Field} linked
     * to the specified column name.
     *
     * @param attribute the column name
     * @return The {@link Field} associated with the column name
     */
    public Field getField(String attribute) {

        return fieldMap.get(attribute);
    }

    /**
     * This method gets the name of the table for this schema.
     *
     * @return The table name
     */
    public String getTableName() {

        return tableName;
    }

    /**
     * This method retrieves all column names for this table.
     *
     * @return A {@link Set} containing all column names
     */
    public Set<String> getAttributes() {

        return fieldMap.keySet();
    }

    /**
     * This method gets the name of the primary key for this table.
     *
     * @return The name of the primary key
     */
    public String getPrimaryKey() {

        return primaryKey;
    }

    /**
     * This method checks if this table has a foreign key.
     *
     * @return {@code true} if a foreign key exists, {@code false} otherwise
     */
    public boolean hasForeignKey() {

        return getForeignKey() != null;
    }

    /**
     * This method gets the name of the foreign key for this table,
     * or {@code null} if no foreign key exists.
     *
     * @return The name of the foreign key, or {@code null}
     */
    public String getForeignKey() {

        return foreignKey;
    }

    /**
     * This method gets the table that the foreign key for this table
     * links to.  If this table has no foreign key, this method returns
     * {@code null.}
     *
     * @return The table linked to by the foreign key, or {@code null}
     */
    public SchemaTable getForeignTable() {

        return SchemaManager.get(foreignClass);
    }

    /**
     * This method checks if the specified name is either a primary or foreign key.
     *
     * @param attribute the attribute to check
     * @return {@code true} if the name is a primary or foreign key, {@code false} otherwise
     */
    public boolean isSpecialKey(String attribute) {

        return attribute.equals(getPrimaryKey()) || attribute.equals(getForeignKey());
    }

    /**
     * This method retrieves the {@link DatabaseType} for the specified column.
     *
     * @param attribute the column name to check
     * @return The {@link DatabaseType} for the column
     */
    public DatabaseType getAttributeType(String attribute) {

        return typeMap.get(attribute);
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("SchemaTable{name='" + tableName + "'}\n");

        for (String name : fieldMap.keySet()) {

            string.append("Column{name='").append(name).append("', type=")
                    .append(typeMap.get(name).name()).append("}\n");
        }

        return string.toString().trim();
    }

    /**
     * This method builds a new schema based on fields in the specified class.
     * Only fields annotated with {@link DatabaseColumn} are considered.
     * <p>
     * The class provided should also be annotated with {@link DatabaseTable}.
     *
     * @param featureClass the class to build from
     * @return The schema built from the specified class
     */
    public static SchemaTable build(Class<?> featureClass) {

        // Check that the class provided defines a database table
        if (!featureClass.isAnnotationPresent(DatabaseTable.class))
            return null;

        // Generate a new schema table
        SchemaTable table = new SchemaTable();

        DatabaseTable tableDetails = featureClass.getAnnotation(DatabaseTable.class);

        table.tableName = tableDetails.value();

        // Extract columns from fields
        for (Field field : featureClass.getFields())
            buildColumnFromField(featureClass, field, table);

        return table;
    }

    private static void buildColumnFromField(Class<?> featureClass, Field field, SchemaTable table) {

        // Check that the field provided is actually a database column
        if (!field.isAnnotationPresent(DatabaseColumn.class)) {
            return;
        }

        // If type is primitive, throw an error (we need null values to be acceptable in all database fields)
        if (TypeUtils.isPrimitive(field.getType())) {
            AIXMFeatureException exception = new AIXMFeatureException(featureClass, "Database attribute field '" + featureClass.getSimpleName() + "' cannot be a primitive type.  Use wrapper classes instead (i.e. 'Integer' instead of 'int').");
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }

        DatabaseColumn columnDetails = field.getAnnotation(DatabaseColumn.class);

        String name = columnDetails.name();
        DatabaseType type = columnDetails.type();
        boolean primary = columnDetails.primary();
        boolean foreign = columnDetails.foreign();
        Class<?> foreignClass = columnDetails.foreignClass();

        table.fieldMap.put(name, field);
        table.typeMap.put(name, type);

        if (primary)
            table.primaryKey = name;

        if (foreign) {
            table.foreignKey = name;
            table.foreignClass = foreignClass;
        }
    }
}
