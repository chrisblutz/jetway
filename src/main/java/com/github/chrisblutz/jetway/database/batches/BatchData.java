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
package com.github.chrisblutz.jetway.database.batches;

import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles batches of database data while
 * waiting for bulk database inserts.
 *
 * @author Christopher Lutz
 */
public class BatchData {

    private final Map<SchemaTable, Map<Object, Feature>> tableFeatureMap = new HashMap<>();
    private final Map<SchemaTable, Object[]> primaryKeys = new HashMap<>();
    private final Map<SchemaTable, Feature[]> features = new HashMap<>();
    private int size = 0;

    /**
     * This method adds a new feature to the batch.
     *
     * @param table the {@link SchemaTable} for the feature
     * @param value the feature to add
     */
    public void addFeature(SchemaTable table, Feature value) {

        if (!tableFeatureMap.containsKey(table))
            tableFeatureMap.put(table, new HashMap<>());

        // Insert feature with primary key into mapping
        tableFeatureMap.get(table).put(value.getId(), value);
        size++;
    }

    /**
     * This method retrieves all features waiting to
     * be inserted into the database for the specified
     * {@link SchemaTable}.
     * <p>
     * The {@link #split()} method must be called for this
     * method to return any useful results.
     *
     * @param table the {@link SchemaTable} to look for
     * @return The array of features waiting to be inserted
     */
    public Feature[] getFeatures(SchemaTable table) {

        return features.getOrDefault(table, new Feature[0]);
    }

    /**
     * This method adds a new primary key to the batch.
     *
     * @param table      the {@link SchemaTable} for the feature
     * @param primaryKey the primary key to add
     */
    public void addPrimaryKey(SchemaTable table, Object primaryKey) {

        if (!tableFeatureMap.containsKey(table))
            tableFeatureMap.put(table, new HashMap<>());

        // Insert feature with primary key into mapping, checking first to make sure it doesn't already exist
        tableFeatureMap.get(table).putIfAbsent(primaryKey, null);
    }

    /**
     * This method retrieves all primary keys waiting to
     * be inserted into the database for the specified
     * {@link SchemaTable}.
     * <p>
     * The {@link #split()} method must be called for this
     * method to return any useful results.
     *
     * @param table the {@link SchemaTable} to look for
     * @return The array of primary keys waiting to be inserted
     */
    public Object[] getPrimaryKeys(SchemaTable table) {

        return primaryKeys.getOrDefault(table, new Object[0]);
    }

    /**
     * This method splits the submitted batch data into primary key-only entries
     * and full feature entries.
     * <p>
     * These sets are then available using the {@link #getPrimaryKeys(SchemaTable)}
     * and {@link #getFeatures(SchemaTable)} methods.
     */
    public void split() {

        for (SchemaTable table : tableFeatureMap.keySet()) {

            List<Object> primaryKeyList = new ArrayList<>();
            List<Feature> featureList = new ArrayList<>();

            // Iterate over all features, and split the primary key-only entries from the rest
            for (Object primaryKey : tableFeatureMap.get(table).keySet()) {
                Feature feature = tableFeatureMap.get(table).get(primaryKey);

                // If feature is null, this is a primary key-only entry
                if (feature == null)
                    primaryKeyList.add(primaryKey);
                else
                    featureList.add(feature);
            }

            primaryKeys.put(table, primaryKeyList.toArray());
            features.put(table, featureList.toArray(new Feature[0]));
        }
    }

    /**
     * This method retrieves the size of the batch, which
     * consists of the number of features in the batch.
     * <p>
     * Primary keys do not impact the size.
     *
     * @return The size of the batch
     */
    public int size() {

        return size;
    }
}
