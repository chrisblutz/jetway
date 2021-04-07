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
import com.github.chrisblutz.jetway.database.batches.DatabaseBatching;
import com.github.chrisblutz.jetway.database.enforcement.Enforcement;
import com.github.chrisblutz.jetway.database.metadata.Metadata;
import com.github.chrisblutz.jetway.exceptions.JetwayException;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import gov.faa.aixm51.SubscriberFileComponentPropertyType;
import gov.faa.aixm51.SubscriberFileType;
import org.apache.logging.log4j.message.FormattedMessage;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

/**
 * This class serves as the entry point for the AIXM-loading functionality
 * for Jetway.
 *
 * @author Christopher Lutz
 */
public final class AIXM {

    private AIXM() {}

    private static int totalCount = 0;

    /**
     * This method begins the load sequence for all registered AIXM files.
     */
    public static void load() {

        // For each AIXM file, perform the full load sequence
        for (String aixmFile : AIXMFeatureManager.getAIXMFiles())
            load(aixmFile);
    }

    /**
     * This method loads a specific AIXM file.
     * It first loads the file into memory using XMLBeans,
     * then parses each AIXM entry into a specific feature,
     * if applicable.
     *
     * @param aixmFile the AIXM file to load
     */
    public static void load(String aixmFile) {

        JetwayLog.getJetwayLogger().info("Loading AIXM data in path '" + aixmFile + "'...");

        // Reset count of entries
        totalCount = 0;

        // Record start time
        long startTime = System.currentTimeMillis();

        // Load AIXM properties from file
        JetwayLog.getJetwayLogger().info("Loading AIXM file into memory...");
        final SubscriberFileType subFile = AIXMFiles.loadAIXMFile(aixmFile);

        // Extract date range when this NASR data is valid
        extractEffectiveRange(subFile);

        // Extract individual features as array
        final SubscriberFileComponentPropertyType[] properties = subFile.getMemberArray();

        // Find all possible AIXM features that may be in this file
        JetwayLog.getJetwayLogger().info("Retrieving all possible features for this file...");
        List<FeatureEntry> possibleEntries = AIXMFeatureManager.getPossibleEntries(aixmFile);

        long loadTime = System.currentTimeMillis();

        double totalLoadTime = (loadTime - startTime) / 1000d;
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Loaded AIXM file in %,.3f seconds.", totalLoadTime));
        JetwayLog.getJetwayLogger().info("Reading entries and submitting to the database (this will take a while)...");
        loadAll(properties, possibleEntries);

        // Wait for all batches to be completed
        DatabaseBatching.awaitTermination();

        // Calculate time spent in seconds
        long parseTime = System.currentTimeMillis();

        double totalParseTime = (parseTime - loadTime) / 1000d;
        double totalTime = (parseTime - startTime) / 1000d;

        // Print stats
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Generated %,d entries in %,.3f seconds (%,d total database batches).", totalCount, totalParseTime, DatabaseBatching.getBatchCount()));
        JetwayLog.getJetwayLogger().info(new FormattedMessage("Complete load time was %,.3f seconds.", totalTime));
    }

    private static void loadAll(SubscriberFileComponentPropertyType[] properties, List<FeatureEntry> possibleEntries) {

        // Loop through all components and try to load them
        for (SubscriberFileComponentPropertyType property : properties) {

            try {

                // Attempt to load current AIXM property as a feature
                loadAIXMFeature(property, possibleEntries);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {

                AIXMException exception = new AIXMException("An error occurred while parsing AIXM data into separate features.", e);
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }

        // Submit final batch if it has data
        DatabaseBatching.finalizeBatches();
    }

    private static void loadAIXMFeature(SubscriberFileComponentPropertyType property, List<FeatureEntry> possibleEntries) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        AIXMIdentifiedFeature feature = identify(property, possibleEntries);

        // If feature is null, don't try to load it
        if (feature == null)
            return;

        AIXMInstance instance = convertToInstance(feature);

        // Create instance of the identified feature and fill in fields
        Feature featureInstance = feature.getEntry().instantiate();
        fillInData(feature, instance, featureInstance);

        // Enter information into database
        DatabaseBatching.addValues(feature.getEntry().getSchemaTable(), featureInstance);

        // Increment feature counter
        totalCount++;
    }

    private static void fillInData(AIXMIdentifiedFeature feature, AIXMInstance instance, Feature featureInstance) throws IllegalAccessException {

        // Fill in the ID field if present
        if (feature.getEntry().getMapping().getIDField() != null) {

            feature.getEntry().getMapping().getIDField().set(featureInstance, feature.getId());
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

            // Attempt to load the property as the current feature entry type
            AIXMIdentifiedFeature feature = identifyFeature(property, entry);
            if (feature != null)
                return feature;
        }

        // If no features matched, return null
        return null;
    }

    private static AIXMIdentifiedFeature identifyFeature(SubscriberFileComponentPropertyType property, FeatureEntry entry) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Check for the correct tag name
        Object value = property.getClass().getMethod("get" + entry.getName()).invoke(property);
        if (value != null) {

            // Extract ID and match it
            String id = (String) value.getClass().getMethod("getId").invoke(value);
            if (id.matches(entry.getId() + "_[0-9_]+"))
                return new AIXMIdentifiedFeature(id, value, entry);
        }

        // If this feature did not match, return null
        return null;
    }

    private static void extractEffectiveRange(SubscriberFileType subFile) {

        // Extract "from" date (data is effective on and after this date)
        Calendar from = subFile.getValidFrom();

        // Calculate "to" date (data is only effective prior to this date)
        // In the subscriber file, this value is "null", so it is calculated as 28 days after the "from" date
        Calendar to = (Calendar) from.clone();
        to.add(Calendar.DATE, 28);

        // Convert Calendar instances into ZonedDateTime instances
        ZonedDateTime fromDate = ZonedDateTime.ofInstant(from.toInstant(), from.getTimeZone().toZoneId());
        ZonedDateTime toDate = ZonedDateTime.ofInstant(to.toInstant(), to.getTimeZone().toZoneId());

        // Update effective date metadata
        Database.getManager().setMetadata(Metadata.EFFECTIVE_FROM_DATE, fromDate);
        Database.getManager().setMetadata(Metadata.EFFECTIVE_TO_DATE, toDate);

        // Check if data being loaded is current or not (only if effective ranges are being considered)
        if (!Database.isValid() && Database.getEffectiveRangeEnforcement() != Enforcement.IGNORE) {

            // Log a warning that the data being loaded is out-of-date
            JetwayLog.getJetwayLogger().warn("*** Data being loaded is out-of-date. ***");
            JetwayLog.getJetwayLogger().warn("You may want to consider updating your AIXM source.");

            // If not, and enforcement is strict, throw an exception
            if (Database.getEffectiveRangeEnforcement() == Enforcement.STRICT) {

                JetwayException exception = new JetwayException("AIXM source is out-of-date.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }
    }
}
