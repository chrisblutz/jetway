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

import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

/**
 * This class handles the batching of database entries
 * into chunks of data for a database manager to handle.
 *
 * @author Christopher Lutz
 */
public class DatabaseBatching {

    /**
     * This constant determines the size limit for batches
     * before they are inserted into the database.
     */
    public static final int BATCH_LIMIT = 1000;

    private static final ValueBatch batch = new ValueBatch();

    /**
     * This method adds a new feature instance to the current batch,
     * and if the batch hits the limit defined in {@link #BATCH_LIMIT},
     * it commits the data to the database.
     *
     * @param table the {@link SchemaTable} for the feature
     * @param value the feature to add
     */
    public static void addValues(SchemaTable table, Feature value) {

        // Add the new value
        batch.addFeature(table, value);

        // If batch size is at the limit, commit the batch to the database
        if (batch.size() >= BATCH_LIMIT)
            commitBatch();
    }

    /**
     * This method adds a primary key to the current batch to help
     * prevent foreign key conflicts.
     * <p>
     * These primary keys do not impact the size of a batch.
     *
     * @param table      the {@link SchemaTable} the primary key refers to
     * @param primaryKey the primary key to add
     */
    public static void addPrimaryKey(SchemaTable table, Object primaryKey) {

        // Add the new primary key
        batch.addPrimaryKey(table, primaryKey);
    }

    /**
     * This method cleans up all in-progress batches and commits
     * them to the database.
     */
    public static void finalizeBatches() {

        // If batch isn't empty, commit it to the database
        if (batch.size() > 0)
            commitBatch();
    }

    private static void commitBatch() {

        // Insert all necessary primary keys to meet foreign key constraints
        for (SchemaTable table : batch.getPrimaryKeySchemaTables()) {

            Object[] keys = batch.getPrimaryKeys(table);
            if (!Database.getManager().insertPrimaryKeys(table, keys))
                JetwayLog.getDatabaseLogger().warn("Failed to insert " + keys.length + " primary keys into the '" + table.getTableName() + "' table.");
        }

        // Insert all features
        for (SchemaTable table : batch.getFeatureSchemaTables()) {

            Feature[] features = batch.getFeatures(table);
            if (!Database.getManager().insertEntries(table, features))
                JetwayLog.getDatabaseLogger().warn("Failed to insert " + features.length + " features into the '" + table.getTableName() + "' table.");
        }

        // Remove all entries from the batch
        batch.clear();
    }
}
