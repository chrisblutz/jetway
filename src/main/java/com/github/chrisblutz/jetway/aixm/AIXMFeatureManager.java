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

import com.github.chrisblutz.jetway.aixm.exceptions.AIXMFeatureException;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.util.*;

/**
 * This class is responsible for managing all
 * {@link com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature} classes.
 * The features are registered with this class, which tracks
 * file-to-feature and class-to-feature mappings.
 *
 * @author Christopher Lutz
 */
public final class AIXMFeatureManager {

    private AIXMFeatureManager() {}

    private static final Map<String, List<FeatureEntry>> fileToFeatureMap = new HashMap<>();
    private static final Map<Class<? extends Feature>, FeatureEntry> classToFeatureEntryMap = new HashMap<>();

    /**
     * This method retrieves all AIXM file names present and used by
     * different features.
     *
     * @return A {@link Set} containing the file names
     */
    public static Set<String> getAIXMFiles() {

        return fileToFeatureMap.keySet();
    }

    /**
     * This method retrieves all possible feature types given a specific AIXM file.
     *
     * @param aixmFile the AIXM file name
     * @return A {@link List} containing all possible entries for that AIXM file
     */
    public static List<FeatureEntry> getPossibleEntries(String aixmFile) {

        return fileToFeatureMap.get(aixmFile);
    }

    /**
     * This method retrieves the registered {@link FeatureEntry} for the
     * specified class.
     *
     * @param featureClass the class to retrieve the feature for
     * @return The {@link FeatureEntry} associated with the specified class
     */
    public static FeatureEntry get(Class<? extends Feature> featureClass) {

        return classToFeatureEntryMap.get(featureClass);
    }

    /**
     * This method registers a new AIXM feature using a class annotated with
     * {@link com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature AIXMFeature}.
     * It builds a {@link FeatureEntry} instance using the data provided in
     * the annotations and the fields in the class.
     *
     * @param featureClass the class to build a feature out of
     */
    public static void registerFeatureType(Class<? extends Feature> featureClass) {

        FeatureEntry entry = FeatureEntry.build(featureClass);

        if (entry != null) {

            logFeatureInformation(entry, featureClass);

            // Add the current entry to the list for the AIXM file (creating the list if it doesn't exist)
            if (!fileToFeatureMap.containsKey(entry.getAIXMFile()))
                fileToFeatureMap.put(entry.getAIXMFile(), new ArrayList<>());
            fileToFeatureMap.get(entry.getAIXMFile()).add(entry);

            // Register the current class to the built feature
            classToFeatureEntryMap.put(featureClass, entry);

            // Check if the AIXM file for this feature is null
            if (entry.getAIXMFile() == null) {

                // Since this feature has a null AIXM file, it cannot be accessed
                AIXMFeatureException exception = new AIXMFeatureException(featureClass, "Feature '" + featureClass.getSimpleName() + "' is inaccessible because its AIXM file is 'null'.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }
    }

    private static void logFeatureInformation(FeatureEntry entry, Class<? extends Feature> featureClass) {

        JetwayLog.getJetwayLogger().info("Generated feature entry for feature '" + featureClass.getSimpleName() + "' with name '" + entry.getName() + "' and " + entry.getMapping().getFields().size() + " field(s).");

        if (JetwayLog.getJetwayLogger().isDebugEnabled()) {
            JetwayLog.getJetwayLogger().debug("Feature information for '" + featureClass.getSimpleName() + "':");
            JetwayLog.getJetwayLogger().debug("\tAIXM File: " + entry.getAIXMFile());
        }
    }

    /**
     * This method clears the feature entry registry.
     */
    public static void reset() {

        fileToFeatureMap.clear();
        classToFeatureEntryMap.clear();
    }
}
