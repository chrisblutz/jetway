/*
 * Copyright 2021 Christopher Lutz
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
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

/**
 * This class performs database insertion operations
 * for a specific batch of feature and primary key data.
 * <p>
 * {@code BatchTask} instances are meant to be executed
 * on separate threads.
 *
 * @author Christopher Lutz
 */
public class BatchTask implements Runnable {

    private final BatchData batch;

    /**
     * This constructor creates a new task that contains
     * the specific database batch data.
     *
     * @param batch the database {@link BatchData} to be submitted
     */
    public BatchTask(BatchData batch) {

        this.batch = batch;
    }

    @Override
    public void run() {

        // Split batch data into primary keys and features
        batch.split();

        // Insert all necessary primary keys to meet foreign key constraints, in top-down dependency order (parents first)
        // Then, insert all full features
        for (SchemaTable table : SchemaManager.getParentFirstDependencyTree()) {

            Object[] keys = batch.getPrimaryKeys(table);
            Feature[] features = batch.getFeatures(table);

            // Check that there are keys to insert (avoid empty database operations)
            if (keys.length != 0)
                if (!Database.getManager().insertPrimaryKeys(table, keys))
                    JetwayLog.getDatabaseLogger().warn("Failed to insert " + keys.length + " primary keys into the '" + table.getTableName() + "' table.");

            // Check that there are features to insert (avoid empty database operations)
            if (features.length != 0)
                if (!Database.getManager().insertEntries(table, features))
                    JetwayLog.getDatabaseLogger().warn("Failed to insert " + features.length + " features into the '" + table.getTableName() + "' table.");
        }
    }
}
