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
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseForeignKey;
import com.github.chrisblutz.jetway.database.annotations.DatabasePrimaryKey;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.database.keys.ForeignKeyData;
import com.github.chrisblutz.jetway.database.keys.Relationship;
import com.github.chrisblutz.jetway.features.Feature;
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
    private final Map<String, ForeignKeyData> foreignKeyMap = new HashMap<>();
    private String primaryKey;

    private SchemaTable[] parentDependencies = null;

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
     * This method gets the name of the foreign keys for this table,
     * or {@code null} if no foreign keys exist.
     *
     * @return The name of the foreign keys, or an empty {@link Set<String>} if no foreign keys exist
     */
    public Set<String> getForeignKeys() {

        return foreignKeyMap.keySet();
    }

    /**
     * This method gets the data for the specified foreign key, containing
     * the feature it links to and the relationship for the key (belongs to, uses, etc.).
     * <p>
     * If the given attribute is not a foreign key, this method returns
     * {@code null.}
     *
     * @param foreignKey the foreign key to use
     * @return The data for the foreign key, or {@code null}
     */
    public ForeignKeyData getForeignKeyData(String foreignKey) {

        return foreignKeyMap.get(foreignKey);
    }

    /**
     * This method checks if the specified name is either a primary or foreign key.
     *
     * @param attribute the attribute to check
     * @return {@code true} if the name is a primary or foreign key, {@code false} otherwise
     */
    public boolean isSpecialKey(String attribute) {

        return attribute.equals(getPrimaryKey()) || getForeignKeys().contains(attribute);
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

    /**
     * This method checks if this table has any {@link Relationship#BELONGS_TO BELONGS_TO}
     * relationships, which indicate that it has at least one parent table.
     *
     * @return {@code true} if this table has a parent, {@code false} otherwise
     */
    public boolean hasParentDependencies() {

        return getParentDependencies().length > 0;
    }

    /**
     * This method retrieves all tables that this table has a
     * {@link Relationship#BELONGS_TO BELONGS_TO} relationship with.
     *
     * @return The array of all parent tables
     */
    public SchemaTable[] getParentDependencies() {

        // Create cached value if it doesn't exist
        if (parentDependencies == null) {

            // If this table has no foreign keys, it has no dependencies
            if (getForeignKeys().isEmpty())
                return new SchemaTable[0];

            List<SchemaTable> parents = new ArrayList<>();

            // Otherwise, check if any foreign keys have BELONGS_TO relationships
            // If they do, this table has that table as a parent
            for (String foreignKey : getForeignKeys()) {

                ForeignKeyData data = getForeignKeyData(foreignKey);
                if (data.getRelationship() == Relationship.BELONGS_TO)
                    parents.add(data.getFeatureTable());
            }

            parentDependencies = parents.toArray(new SchemaTable[0]);
        }

        // Return cached value
        return parentDependencies;
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
    public static SchemaTable build(Class<? extends Feature> featureClass) {

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

    private static void buildColumnFromField(Class<? extends Feature> featureClass, Field field, SchemaTable table) {

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

        // Extract name and type data from column annotation
        DatabaseColumn columnDetails = field.getAnnotation(DatabaseColumn.class);

        String name = columnDetails.name();
        DatabaseType type = columnDetails.type();

        table.fieldMap.put(name, field);
        table.typeMap.put(name, type);

        // Check primary or foreign key status
        checkSpecialStatus(name, field, table);
    }

    private static void checkSpecialStatus(String name, Field field, SchemaTable table) {

        // Check for primary key annotation
        if (field.isAnnotationPresent(DatabasePrimaryKey.class))
            table.primaryKey = name;

        // Check for foreign key annotation, and extract feature class and relationship
        if (field.isAnnotationPresent(DatabaseForeignKey.class)) {

            DatabaseForeignKey foreignKeyDetails = field.getAnnotation(DatabaseForeignKey.class);

            Class<? extends Feature> foreignClass = foreignKeyDetails.value();
            Relationship relationship = foreignKeyDetails.relationship();

            table.foreignKeyMap.put(name, new ForeignKeyData(foreignClass, relationship));
        }
    }
}
