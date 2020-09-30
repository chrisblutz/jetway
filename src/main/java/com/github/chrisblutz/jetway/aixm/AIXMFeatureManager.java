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
 * root status and class-to-feature mappings.
 *
 * @author Christopher Lutz
 */
public final class AIXMFeatureManager {

    private AIXMFeatureManager() {}

    private static final Map<FeatureEntry, List<FeatureEntry>> childFeatureMap = new HashMap<>();
    private static final Map<Class<? extends Feature>, FeatureEntry> classToFeatureEntryMap = new HashMap<>();

    /**
     * This method retrieves {@link FeatureEntry} instances for all
     * classes where the {@link com.github.chrisblutz.jetway.aixm.annotations.AIXMRoot AIXMRoot}
     * annotation is present.  These specific features represent the root features
     * in each file, and are usually the parent features for other feature types.
     *
     * @return A {@link Set} containing the root {@link FeatureEntry} instances
     */
    public static Set<FeatureEntry> getRootEntries() {

        return childFeatureMap.keySet();
    }

    /**
     * This method retrieves all possible feature types given a specific root feature.
     * These features are features that exist in the same file as the root feature,
     * and are generally child features of that root feature.
     *
     * @param rootEntry the root {@link FeatureEntry}
     * @return A {@link List} containing all possible entries for that root's file
     */
    public static List<FeatureEntry> getPossibleEntries(FeatureEntry rootEntry) {

        return childFeatureMap.get(rootEntry);
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

            FeatureEntry rootParent = getRootParent(entry);

            logFeatureInformation(entry, rootParent, featureClass);

            classToFeatureEntryMap.put(featureClass, entry);

            boolean root = checkForRoot(entry, featureClass);
            boolean child = checkForParent(entry, rootParent);

            // Check if this feature is orphaned (no parent and not root)
            boolean orphaned = !(root || child);

            if (orphaned) {

                // Since this feature is neither a root nor a child, throw an error
                AIXMFeatureException exception = new AIXMFeatureException(featureClass, "This feature is neither a root nor a child.  It will not be loaded.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }
    }

    private static boolean checkForRoot(FeatureEntry entry, Class<? extends Feature> featureClass) {

        if (entry.isRoot()) {

            JetwayLog.getJetwayLogger().info("Feature '" + featureClass.getSimpleName() + "' has been registered as a root feature with file '" + entry.getRootPath() + "'.");

            List<FeatureEntry> possibleEntries = new ArrayList<>();
            possibleEntries.add(entry);

            childFeatureMap.put(entry, possibleEntries);
            return true;
        }

        // Feature was not root, so return false
        return false;
    }

    private static boolean checkForParent(FeatureEntry entry, FeatureEntry rootParent) {

        if (entry.isChild() && rootParent != null) {

            // Register this entry as a sub-feature of its parent
            childFeatureMap.get(rootParent).add(entry);
            return true;
        }

        // Feature had no parent, so return false
        return false;
    }

    private static void logFeatureInformation(FeatureEntry entry, FeatureEntry rootParent, Class<? extends Feature> featureClass) {

        JetwayLog.getJetwayLogger().info("Generated feature entry for feature '" + featureClass.getSimpleName() + "' with name '" + entry.getName() + "' and " + entry.getMapping().getFields().size() + "' fields.");

        if (JetwayLog.getJetwayLogger().isDebugEnabled()) {
            JetwayLog.getJetwayLogger().debug("Feature information for '" + featureClass.getSimpleName() + "':");
            JetwayLog.getJetwayLogger().debug("\tRoot:  " + (entry.isRoot() ? "Yes, '" + entry.getRootPath() + "'" : "No"));
            JetwayLog.getJetwayLogger().debug("\tChild: " + (entry.isChild() ? "Yes, " + entry.getParentClass().getSimpleName() : "No"));
            JetwayLog.getJetwayLogger().debug("\tRoot AIXM Parent: " + (rootParent != null ? rootParent.getName() : "None"));
        }
    }

    private static FeatureEntry getRootParent(FeatureEntry entry) {

        // If this entry is root, return
        if (entry.isRoot())
            return entry;

        // Otherwise, recursively work up the parent chain until a root is found
        if (entry.isChild() && classToFeatureEntryMap.containsKey(entry.getParentClass()))
            return getRootParent(classToFeatureEntryMap.get(entry.getParentClass()));

        // If the entry is neither root nor child, or the parent does not exist, return null
        return null;
    }

    /**
     * This method clears the feature entry registry.
     */
    public static void reset() {

        childFeatureMap.clear();
        classToFeatureEntryMap.clear();
    }
}
