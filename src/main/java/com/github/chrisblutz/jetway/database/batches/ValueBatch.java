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

import java.util.*;

/**
 * This class handles batches of database data while
 * waiting for bulk database inserts.
 *
 * @author Christopher Lutz
 */
public class ValueBatch {

    private final Map<SchemaTable, List<Feature>> features = new HashMap<>();
    private final Map<SchemaTable, Set<Object>> primaryKeys = new HashMap<>();
    private int size = 0;

    /**
     * This method returns all {@link SchemaTable} instances
     * that have features waiting to be inserted into the database.
     *
     * @return The {@link Set} of the schema tables
     */
    public Set<SchemaTable> getFeatureSchemaTables() {

        return features.keySet();
    }

    /**
     * This method adds a new feature to the batch.
     *
     * @param table the {@link SchemaTable} for the feature
     * @param value the feature to add
     */
    public void addFeature(SchemaTable table, Feature value) {

        if (!features.containsKey(table))
            features.put(table, new ArrayList<>());

        features.get(table).add(value);
        size++;
    }

    /**
     * This method retrieves all features waiting to
     * be inserted into the database for the specified
     * {@link SchemaTable}.
     *
     * @param table the {@link SchemaTable} to look for
     * @return The array of features waiting to be inserted
     */
    public Feature[] getFeatures(SchemaTable table) {

        return features.getOrDefault(table, new ArrayList<>()).toArray(new Feature[0]);
    }

    /**
     * This method returns all {@link SchemaTable} instances
     * that have primary keys waiting to be inserted into the database.
     *
     * @return The {@link Set} of the schema tables
     */
    public Set<SchemaTable> getPrimaryKeySchemaTables() {

        return primaryKeys.keySet();
    }

    /**
     * This method adds a new primary key to the batch.
     *
     * @param table      the {@link SchemaTable} for the feature
     * @param primaryKey the primary key to add
     */
    public void addPrimaryKey(SchemaTable table, Object primaryKey) {

        if (!primaryKeys.containsKey(table))
            primaryKeys.put(table, new HashSet<>());

        primaryKeys.get(table).add(primaryKey);
    }

    /**
     * This method retrieves all primary keys waiting to
     * be inserted into the database for the specified
     * {@link SchemaTable}.
     *
     * @param table the {@link SchemaTable} to look for
     * @return The array of primary keys waiting to be inserted
     */
    public Object[] getPrimaryKeys(SchemaTable table) {

        return primaryKeys.getOrDefault(table, new HashSet<>()).toArray(new Object[0]);
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

    /**
     * This method clears the batch and resets the size to 0.
     */
    public void clear() {

        features.clear();
        primaryKeys.clear();
        size = 0;
    }
}
