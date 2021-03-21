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
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class handles all {@link SchemaTable} instances, as well as their
 * table-to-feature mappings.
 *
 * @author Christopher Lutz
 */
public final class SchemaManager {

    private SchemaManager() {}

    private static final List<Class<? extends Feature>> featureList = new ArrayList<>();
    private static final Map<Class<? extends Feature>, SchemaTable> classToSchemaTableMap = new HashMap<>();
    private static final Map<SchemaTable, List<SchemaTable>> childSchemaTableMap = new HashMap<>();

    // Use top-down dependency tree for insertions (i.e. insert parents first, then children)
    // Use bottom-up dependency tree for deletions (i.e. delete children first, then parents)
    private static SchemaTable[] topDownDependenceTree, bottomUpDependenceTree;

    /**
     * This method retrieves the registered {@link SchemaTable} for
     * the given class.
     *
     * @param featureClass the class to get the {@link SchemaTable} for
     * @return The {@link SchemaTable} associated with the specified class
     */
    public static SchemaTable get(Class<? extends Feature> featureClass) {

        return classToSchemaTableMap.get(featureClass);
    }

    /**
     * This method returns the full list of registered feature classes.
     * These classes should be annotated with
     * {@link com.github.chrisblutz.jetway.database.annotations.DatabaseTable DatabaseTable}.
     *
     * @return A {@link List} of all registered feature classes
     */
    public static List<Class<? extends Feature>> getFeatures() {

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
    public static void registerFeatureType(Class<? extends Feature> featureClass) {

        SchemaTable table = SchemaTable.build(featureClass);

        if (table != null) {

            // Log basic table information
            logTableInformation(table, featureClass);

            logAttributeInformation(table);

            featureList.add(featureClass);
            classToSchemaTableMap.put(featureClass, table);
            childSchemaTableMap.put(table, new ArrayList<>());

            // Check if this table has any parents
            if (table.hasParentDependencies()) {

                // For each parent, mark this table as its child
                for (SchemaTable parent : table.getParentDependencies())
                    childSchemaTableMap.get(parent).add(table);
            }
        }
    }

    private static void logTableInformation(SchemaTable table, Class<?> featureClass) {

        JetwayLog.getDatabaseLogger().info("Generated schema table for feature '" + featureClass.getSimpleName() + "' with name '" + table.getTableName() + "' and " + table.getAttributes().size() + " column(s).");

        if (JetwayLog.getDatabaseLogger().isDebugEnabled()) {
            JetwayLog.getDatabaseLogger().debug("Table information for '" + table.getTableName() + "':");
            JetwayLog.getDatabaseLogger().debug("\tPrimary Key: " + (table.getPrimaryKey() != null ? table.getPrimaryKey() : "None"));
            JetwayLog.getDatabaseLogger().debug("\tForeign Keys: " + (table.getForeignKeys().isEmpty() ? "None" : String.join(", ", table.getForeignKeys())));
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

    /**
     * This method retrieves the top-down dependency tree for all
     * {@link SchemaTable} entries.
     * <p>
     * A top-down dependency tree is constructed with the dependency-free
     * entries on top, so parent {@link SchemaTable} entries come
     * before child entries.  The exact ordering of tables is not
     * guaranteed, but if table A depends on table B, table A is guaranteed
     * to appear after table B in this list.
     * <p>
     * This style of dependency tree is useful during database insertion
     * operations, since inserting a child row requires its parent
     * to have been inserted prior.
     *
     * @return An array of {@link SchemaTable} entries in top-down dependency order
     */
    public static SchemaTable[] getParentFirstDependencyTree() {

        // If tree has not been generated, generate it
        if (topDownDependenceTree == null)
            generateDependenceTrees();

        return topDownDependenceTree;
    }

    /**
     * This method retrieves the bottom-up dependency tree for all
     * {@link SchemaTable} entries.
     * <p>
     * A bottom-up dependency tree is constructed with the dependent
     * entries on top, so child {@link SchemaTable} entries come
     * before their parent entries.  The exact ordering of tables is not
     * guaranteed, but if table A depends on table B, table A is guaranteed
     * to appear before table B in this list.
     * <p>
     * This style of dependency tree is useful during database deletion
     * operations, since removing a parent row requires its children
     * to have been removed prior.
     *
     * @return An array of {@link SchemaTable} entries in bottom-up dependency order
     */
    public static SchemaTable[] getChildFirstDependencyTree() {

        // If tree has not been generated, generate it
        if (bottomUpDependenceTree == null)
            generateDependenceTrees();

        return bottomUpDependenceTree;
    }

    /**
     * This method generates the dependency trees for all {@link SchemaTable}
     * entries.
     * <p>
     * See {@link #getParentFirstDependencyTree()} and {@link #getChildFirstDependencyTree()}
     * for specifics on each tree type.
     */
    public static void generateDependenceTrees() {

        // generateLeafFirstList() generates the bottom-up list (i.e. children first, then parents)
        List<SchemaTable> bottomUpList = generateLeafFirstList();

        // Reverse bottom-up list for top-down list
        List<SchemaTable> topDownList = new ArrayList<>(bottomUpList);
        Collections.reverse(topDownList);

        // Convert to arrays
        bottomUpDependenceTree = bottomUpList.toArray(new SchemaTable[0]);
        topDownDependenceTree = topDownList.toArray(new SchemaTable[0]);
    }

    private static List<SchemaTable> generateLeafFirstList() {

        // Find all tables who are not dependencies in any table (except the ones in the exclusions list)
        List<SchemaTable> tables = new ArrayList<>(classToSchemaTableMap.values()); // Store all tables as a list
        List<SchemaTable> results = new ArrayList<>(); // Store "tree" of results here (bottom up at this point)

        // When we're done, we should have used up all existing tables (i.e. they should be on the "tree")
        while (tables.size() > 0) {

            // Find one leaf, add it to the result "tree", and remove it from the waiting list
            for (int i = 0; i < tables.size(); i++) {

                SchemaTable table = tables.get(i);
                List<SchemaTable> children = childSchemaTableMap.get(table);

                // If no children, or all children are already in result "tree", add this table
                if (isLeafTable(children, results)) {

                    results.add(table);

                    // Remove this table from the waiting list and restart the search
                    tables.remove(i);
                    break;
                }
            }
        }

        return results;
    }

    private static boolean isLeafTable(List<SchemaTable> children, List<SchemaTable> exclusions) {

        // If all children are in the exclusion list, this is a leaf table
        // If one of the children is not, this is not a leaf table
        for (SchemaTable child : children)
            if (!exclusions.contains(child))
                return false;

        return true;
    }
}
