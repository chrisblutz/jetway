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

import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles all {@link SchemaTable} instances, as well as their
 * table-to-feature mappings.
 *
 * @author Christopher Lutz
 */
public final class SchemaManager {

    private SchemaManager() {}

    private static final List<Class<?>> featureList = new ArrayList<>();
    private static final Map<Class<?>, SchemaTable> classToSchemaTableMap = new HashMap<>();

    /**
     * This method retrieves the registered {@link SchemaTable} for
     * the given class.
     *
     * @param featureClass the class to get the {@link SchemaTable} for
     * @return The {@link SchemaTable} associated with the specified class
     */
    public static SchemaTable get(Class<?> featureClass) {

        return classToSchemaTableMap.get(featureClass);
    }

    /**
     * This method returns the full list of registered feature classes.
     * These classes should be annotated with
     * {@link com.github.chrisblutz.jetway.database.annotations.DatabaseTable DatabaseTable}.
     *
     * @return A {@link List} of all registered feature classes
     */
    public static List<Class<?>> getFeatures() {

        return featureList;
    }

    /**
     * This method registers a new feature class, and generates the {@link SchemaTable}
     * for it.  Classes registered with this method should have the
     * {@link com.github.chrisblutz.jetway.database.annotations.DatabaseTable DatabaseTable}
     * annotation.
     *
     * @param featureClass the feature class to register
     */
    public static void registerFeatureType(Class<?> featureClass) {

        SchemaTable table = SchemaTable.build(featureClass);

        if (table != null) {

            // Log basic table information
            logTableInformation(table, featureClass);

            logAttributeInformation(table);

            featureList.add(featureClass);
            classToSchemaTableMap.put(featureClass, table);
        }
    }

    private static void logTableInformation(SchemaTable table, Class<?> featureClass) {

        JetwayLog.getDatabaseLogger().info("Generated schema table for feature '" + featureClass.getSimpleName() + "' with name '" + table.getTableName() + "' and " + table.getAttributes().size() + " columns.");

        if (JetwayLog.getDatabaseLogger().isDebugEnabled()) {
            JetwayLog.getDatabaseLogger().debug("Table information for '" + table.getTableName() + "':");
            JetwayLog.getDatabaseLogger().debug("\tPrimary Key: " + (table.getPrimaryKey() != null ? table.getPrimaryKey() : "None"));
            JetwayLog.getDatabaseLogger().debug("\tForeign Key: " + (table.getForeignKey() != null ? table.getForeignKey() : "None"));
            JetwayLog.getDatabaseLogger().debug("\tColumns:");
        }
    }

    private static void logAttributeInformation(SchemaTable table) {

        for (String attribute : table.getAttributes()) {
            Field f = table.getField(attribute);

            if (JetwayLog.getDatabaseLogger().isDebugEnabled())
                JetwayLog.getDatabaseLogger().debug("\t\t" + attribute + ": \t" + table.getAttributeType(attribute).name() + ", based on field '" + f.getName() + "' of type " + f.getType().getSimpleName());
        }
    }

    /**
     * This method clears the schema table registry.
     */
    public static void reset() {

        featureList.clear();
        classToSchemaTableMap.clear();
    }
}
