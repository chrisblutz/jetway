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
package com.github.chrisblutz.jetway.aixm;

import com.github.chrisblutz.jetway.aixm.crawling.AIXMInstance;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import gov.faa.aixm51.SubscriberFileComponentPropertyType;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This class serves as the entry point for the AIXM-loading functionality
 * for Jetway.
 *
 * @author Christopher Lutz
 */
public class AIXM {

    private static int totalCount = 0;
    private static int totalPropertiesTraversed = 0, totalPropertiesCount = 0, propertiesPercentIncrement = 0;

    /**
     * This method begins the load sequence for all AIXM features
     * that are registered via {@link AIXMFeatureManager}.  It
     * iterates over all "root" features and loads that feature and all
     * of its children from the feature's file.
     */
    public static void load() {

        // For each root entry, perform the full load sequence
        for (FeatureEntry rootEntry : AIXMFeatureManager.getRootEntries())
            load(rootEntry);
    }

    /**
     * This method loads a specific root feature's file.
     * It first loads the file into memory using XMLBeans,
     * then parses each AIXM entry into a specific feature,
     * if applicable.
     *
     * @param rootEntry the feature type to load.  This feature
     *                  defines which file should be read.
     */
    public static void load(final FeatureEntry rootEntry) {

        // Check to make sure that entry is root
        if (!rootEntry.isRoot())
            return;

        JetwayLog.getJetwayLogger().info("Loading AIXM data in path '" + rootEntry.getRootPath() + "'...");

        // Reset count of entries
        totalCount = 0;
        totalPropertiesTraversed = 0;

        // Record start time
        long startTime = System.currentTimeMillis();

        // Load AIXM properties from file
        JetwayLog.getJetwayLogger().info("Loading AIXM file into memory...");
        final SubscriberFileComponentPropertyType[] properties = AIXMFiles.loadAIXMFile(rootEntry.getRootPath());
        totalPropertiesCount = properties.length;
        propertiesPercentIncrement = totalPropertiesCount / 10;

        // Find all possible AIXM features that may be in this file
        JetwayLog.getJetwayLogger().info("Retrieving all possible features for this file...");
        List<FeatureEntry> possibleEntries = AIXMFeatureManager.getPossibleEntries(rootEntry);

        long loadTime = System.currentTimeMillis();

        double totalLoadTime = (loadTime - startTime) / 1000d;
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Loaded AIXM file in %,.3f seconds.", totalLoadTime));
        JetwayLog.getJetwayLogger().info("Reading entries and submitting to the database (this will take a while)...");
        loadAll(properties, possibleEntries);

        // Calculate time spent in seconds
        long parseTime = System.currentTimeMillis();

        double totalParseTime = (parseTime - loadTime) / 1000d;
        double totalTime = (parseTime - startTime) / 1000d;

        // Print stats
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Generated %,d entries in %,.3f seconds.", totalCount, totalParseTime));
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Complete load time was %,.3f seconds.", totalTime));

        JetwayLog.getDatabaseLogger().info("Closing database connection...");
        Database.getManager().closeConnection();
    }

    private static void loadAll(SubscriberFileComponentPropertyType[] properties, List<FeatureEntry> possibleEntries) {

        for (SubscriberFileComponentPropertyType property : properties) {

            totalPropertiesTraversed++;
            // Every 1000 properties, print a progress report
            if (totalPropertiesTraversed % propertiesPercentIncrement == 0 && totalPropertiesTraversed / propertiesPercentIncrement != 10)
                JetwayLog.getJetwayLogger().info(new FormattedMessage("Progress: %d%% - %,d/%,d (%,d feature instances generated)", (totalPropertiesTraversed / propertiesPercentIncrement) * 10, totalPropertiesTraversed, totalPropertiesCount, totalCount));

            try {

                AIXMIdentifiedFeature feature = identify(property, possibleEntries);

                if (feature == null)
                    continue;

                AIXMInstance instance = convertToInstance(feature);

                // Create instance of the identified feature and fill in fields
                Object featureInstance = feature.getEntry().instantiate();
                fillInData(feature, instance, featureInstance);

                // Enter information into database
                if (!Database.getManager().insertEntry(feature.getEntry().getSchemaTable(), featureInstance))
                    JetwayLog.getDatabaseLogger().warn("Failed to insert entry into database.  Feature: " + feature.getEntry().getName() + ", ID: " + feature.getId());

                // Update information for future children
                feature.getEntry().updateCurrentID(feature.getId());

                // Increment feature counter
                totalCount++;

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {

                AIXMException exception = new AIXMException("An error occurred while parsing AIXM data into separate features.", e);
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }

        JetwayLog.getJetwayLogger().info(new FormattedMessage("Progress: %d%% - %,d/%,d (%,d feature instances generated)", (totalPropertiesTraversed / propertiesPercentIncrement) * 10, totalPropertiesTraversed, totalPropertiesCount, totalCount));
    }

    private static void fillInData(AIXMIdentifiedFeature feature, AIXMInstance instance, Object featureInstance) throws IllegalAccessException {

        // Fill in the ID field if present
        if (feature.getEntry().getMapping().getIDField() != null) {

            feature.getEntry().getMapping().getIDField().set(featureInstance, feature.getId());
        }

        // Fill in the parent field if present
        if (feature.getEntry().getMapping().getParentField() != null) {

            String parentId = feature.getEntry().getParentEntry().getCurrentID();
            feature.getEntry().getMapping().getParentField().set(featureInstance, parentId);
        }

        // Fill in fields
        feature.getEntry().fillFieldsFromInstance(featureInstance, instance);
    }

    private static AIXMInstance convertToInstance(AIXMIdentifiedFeature feature) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object[] timeSliceArray = (Object[]) feature.getValue().getClass().getMethod("getTimeSliceArray").invoke(feature.getValue());
        Object abstractTimeSlice = timeSliceArray[0];
        Object timeSlice = abstractTimeSlice.getClass().getMethod("get" + feature.getEntry().getName() + "TimeSlice").invoke(abstractTimeSlice);
        return new AIXMInstance(timeSlice, feature.getEntry());
    }

    private static AIXMIdentifiedFeature identify(SubscriberFileComponentPropertyType property, List<FeatureEntry> possibleEntries) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        for (FeatureEntry entry : possibleEntries) {

            // Check for the correct tag name
            Object value = property.getClass().getMethod("get" + entry.getName()).invoke(property);
            if (value != null) {

                // Extract ID and match it
                String id = (String) value.getClass().getMethod("getId").invoke(value);
                if (id.matches(entry.getId() + "_[0-9_]+"))
                    return new AIXMIdentifiedFeature(id, value, entry);
            }
        }

        return null;
    }
}
