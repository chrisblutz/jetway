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
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the batching of database entries
 * into chunks of data for a database manager to handle.
 *
 * @author Christopher Lutz
 */
public final class DatabaseBatching {

    private DatabaseBatching() {}

    private static final int THREAD_COUNT_DEFAULT = 1000;
    private static final int TIMEOUT_MILLIS_DEFAULT = 1000;
    private static final int BATCH_LIMIT_DEFAULT = 1000;

    // Number of threads for batching system
    private static int threadCount = THREAD_COUNT_DEFAULT;
    // Time-out for database batch termination
    private static long timeoutMillis = TIMEOUT_MILLIS_DEFAULT;
    // Maximum features in a batch
    private static int batchLimit = BATCH_LIMIT_DEFAULT;

    // Track number of batches submitted
    private static int batchCount = 0;
    // Batch data (features, primary keys, etc.)
    private static BatchData batch = new BatchData();
    // Thread pool for parallelization of batches
    private static ExecutorService threadPool;

    /**
     * This method initializes the batching system and
     * sets up the batch thread pool, which will contain
     * the number of threads defined by {@link #setThreadCount(int)}.
     */
    public static void initialize() {

        // Initialize thread pool
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * This method adds a new feature instance to the current batch,
     * and if the batch hits the limit defined by
     * {@link #setBatchLimit(int)} (default is 1,000)
     * it commits the data to the database.
     *
     * @param table the {@link SchemaTable} for the feature
     * @param value the feature to add
     */
    public static void addValues(SchemaTable table, Feature value) {

        // Add the new value
        batch.addFeature(table, value);

        // If batch size is at the limit, commit the batch to the database
        if (batch.size() >= batchLimit)
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

        // Add one batch to the total count
        batchCount++;

        // Submit a batch task for the current batch to the thread pool
        threadPool.submit(new BatchTask(batch));

        // Create a new batch entry
        batch = new BatchData();
    }

    /**
     * This method retrieves the total number of batches of information submitted
     * to the database.
     *
     * @return The number of batches submitted.
     */
    public static int getBatchCount() {

        return batchCount;
    }

    /**
     * This method defines how many threads the batching
     * system should use when submitting batches to the
     * database.
     * <p>
     * The default value used is 8 threads.
     *
     * @param threadCount the number of threads
     */
    public static void setThreadCount(int threadCount) {

        DatabaseBatching.threadCount = threadCount;
    }

    /**
     * This method defines how long the batch system should
     * wait after a shutdown is requested before it begins
     * force-stopping the batch thread pool.
     * <p>
     * The default value used is 10,000 milliseconds.
     *
     * @param timeoutMillis the timeout in milliseconds
     */
    public static void setTimeoutMillis(long timeoutMillis) {

        DatabaseBatching.timeoutMillis = timeoutMillis;
    }

    /**
     * This method defines how the maximum number of entries for
     * a batch.  When a batch reaches this limit, it will be
     * submitted to the database. Larger batch limits mean fewer
     * batches, but each batch is larger.  Smaller batch limits
     * mean more batches, but each batch is smaller.
     * <p>
     * The default value is 1,000 features.
     *
     * @param batchLimit the maximum feature count for a batch
     */
    public static void setBatchLimit(int batchLimit) {

        DatabaseBatching.batchLimit = batchLimit;
    }

    /**
     * This method sends a shutdown request to the batch thread
     * pool and waits for it to terminate.  If the thread pool
     * does not terminate within the time specified by
     * {@link #setTimeoutMillis(long)}, or if the thread is
     * interrupted, the thread pool is force-stopped.
     */
    public static void awaitTermination() {

        JetwayLog.getDatabaseLogger().info("Waiting for database batching to complete...");
        threadPool.shutdown();
        try {

            // If thread pool does not shut down within the allocated time, force it to shut down
            if (!threadPool.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                JetwayLog.getDatabaseLogger().warn("Time-out exceeded, force-stopping batch thread pool...");
                threadPool.shutdownNow();
            }

        } catch (InterruptedException e) {

            // If the thread is interrupted, force the thread pool to shut down
            JetwayLog.getDatabaseLogger().warn("Thread interrupted, force-stopping batch thread pool...");
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method resets the batching system for Jetway's database operations.
     */
    public static void reset() {

        // If thread pool is not shut down, shut it down
        if (threadPool != null && !threadPool.isShutdown())
            threadPool.shutdown();

        // Reset configuration
        threadCount = THREAD_COUNT_DEFAULT;
        timeoutMillis = TIMEOUT_MILLIS_DEFAULT;
        batchLimit = BATCH_LIMIT_DEFAULT;

        // Reset batch count and create a new batch object
        batchCount = 0;
        batch = new BatchData();
    }
}
