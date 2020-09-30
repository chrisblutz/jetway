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
package com.github.chrisblutz.jetway.aixm.mappings;

import com.github.chrisblutz.jetway.aixm.AIXMFeatureManager;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMRoot;
import com.github.chrisblutz.jetway.aixm.crawling.AIXMData;
import com.github.chrisblutz.jetway.aixm.crawling.AIXMInstance;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.lang.reflect.Field;

/**
 * This class represents an AIXM feature and its information.
 *
 * @author Christopher Lutz
 */
public class FeatureEntry {

    private String name;
    private String id;
    private Class<? extends Feature> featureClass;
    private FeatureMapping mapping;
    private String rootPath = null;
    private Class<? extends Feature> parent = null;
    private SchemaTable schemaTable;

    private String currentId = null;

    /**
     * This method gets the name used to identify this
     * feature in AIXM files.
     *
     * @return The AIXM name for this feature
     */
    public String getName() {

        return name;
    }

    /**
     * This method gets the regular expression that should be used
     * to determine if an AIXM feature is of this type.  This is necessary because
     * some AIXM features share a single name but are differentiated based on ID, such as
     * {@link com.github.chrisblutz.jetway.features.Runway Runway} and
     * {@link com.github.chrisblutz.jetway.features.RunwayEnd RunwayEnd}, which use the same AIXM feature
     * ({@code Runway}), but are set apart by ID.
     * <p>
     * The ID regular expression should omit the numbers/underscores at the end, as these are handled
     * internally.
     * <p>
     * For example, the ID regular expression for {@link com.github.chrisblutz.jetway.features.RunwayEnd RunwayEnd}
     * is {@code (RWY_BASE_END|RWY_RECIPROCAL_END)}.
     *
     * @return The regular expression for the ID for this feature type
     */
    public String getId() {

        return id;
    }

    /**
     * This method creates a new instance of the feature class for this feature.
     *
     * @return A new instance of the feature class
     * @throws IllegalAccessException if the constructor is not accessible
     * @throws InstantiationException if an error occurs while creating the instance
     */
    public Object instantiate() throws IllegalAccessException, InstantiationException {

        return featureClass.newInstance();
    }

    /**
     * This method checks if this feature is the root
     * feature for its file.
     *
     * @return {@code true} if this feature is root, {@code false} otherwise
     */
    public boolean isRoot() {

        return getRootPath() != null;
    }

    /**
     * This method gets the name of the file containing information
     * about this feature and its children.
     * <p>
     * If this feature is not root, this method returns {@code null}.
     *
     * @return The file name for this feature, or {@code null}
     */
    public String getRootPath() {

        return rootPath;
    }

    /**
     * This method checks if this feature is a child of
     * another feature (i.e. if this feature belongs to another one, like
     * {@link com.github.chrisblutz.jetway.features.Runway Runway} belongs
     * to {@link com.github.chrisblutz.jetway.features.Airport}).
     *
     * @return {@code true} if this feature is a child, {@code false} otherwise
     */
    public boolean isChild() {

        return getParentClass() != null;
    }

    /**
     * This method gets the parent class for this feature.
     * <p>
     * This method returns {@code null} if this feature is not a
     * child feature.
     *
     * @return The parent class for this feature, or {@code null}
     */
    public Class<? extends Feature> getParentClass() {

        return parent;
    }

    /**
     * This method gets the entry for the parent feature type.
     * <p>
     * This method returns {@code null} if this feature is not a
     * child feature.
     *
     * @return The entry for the parent feature type, or {@code null}
     */
    public FeatureEntry getParentEntry() {

        return AIXMFeatureManager.get(getParentClass());
    }

    /**
     * This method retrieves the {@link FeatureMapping} associated
     * with this feature type.
     *
     * @return The {@link FeatureMapping} for this feature
     */
    public FeatureMapping getMapping() {

        return mapping;
    }

    /**
     * This method gets the {@link SchemaTable} associated with this
     * feature.
     * <p>
     * This method returns {@code null} if no {@link SchemaTable} is linked
     * to this feature type.
     *
     * @return The {@link SchemaTable} for this feature type, or {@code null}
     */
    public SchemaTable getSchemaTable() {

        return schemaTable;
    }

    /**
     * This method fills in field values for an instance of this feature type
     * using information from AIXM data.
     *
     * @param featureInstance the instance of this feature type
     * @param instanceData    the AIXM data to use
     */
    public void fillFieldsFromInstance(Object featureInstance, AIXMInstance instanceData) {

        // Loop through all defined attributes
        for (Field field : mapping.getFields()) {

            try {

                String path = mapping.getPathForField(field);

                AIXMData data = getData(instanceData, path);

                // Fill in the field
                Object value = data.get(field.getType());
                field.set(featureInstance, value);

            } catch (IllegalAccessException e) {

                AIXMException exception = new AIXMException("An error occurred while populating fields for feature '" + featureClass.getSimpleName() + "'.", e);
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }
        }
    }

    private AIXMData getData(AIXMInstance instanceData, String path) {

        // Check for the Feature or Extension prefix
        if (path.startsWith("Extension/")) {

            path = path.substring("Extension/".length());
            return extract(instanceData.extension(), path);

        } else if (path.startsWith("Feature/")) {

            path = path.substring("Feature/".length());
            return extract(instanceData, path);
        }

        return extract(instanceData, path);
    }

    private AIXMData extract(AIXMData data, String path) {

        return data.crawl(path);
    }

    /**
     * This method gets the ID of the most recently accessed instance
     * of this feature.  This value is used when determining parent IDs.
     *
     * @return The ID of the most recently accessed instance
     */
    public String getCurrentID() {

        return currentId;
    }

    /**
     * This method updates the ID of the most recently accessed instance
     * of this feature.
     *
     * @param currentId The ID of the most recently accessed instance
     */
    public void updateCurrentID(String currentId) {

        this.currentId = currentId;
    }

    @Override
    public String toString() {

        String string = "Feature{" +
                "name='" + name + "'" +
                ", id='" + id + "'" +
                (rootPath == null ? "" : ", root='" + rootPath + "'") +
                (parent == null ? "" : ", parent=" + parent.getName()) + "}\n";
        string += mapping.toString();

        return string.trim();
    }

    /**
     * This method builds a new entry based on the information in
     * the provided class.  Classes passed to this method should
     * be annotated using {@link AIXMFeature}.
     *
     * @param featureClass the class to build from
     * @return the entry built from the class
     */
    public static FeatureEntry build(Class<? extends Feature> featureClass) {

        // Check that the class provided is actually an AIXM feature
        if (!featureClass.isAnnotationPresent(AIXMFeature.class))
            return null;

        // Generate new feature entry
        FeatureEntry entry = new FeatureEntry();

        AIXMFeature featureDetails = featureClass.getAnnotation(AIXMFeature.class);

        entry.name = featureDetails.name();
        entry.id = featureDetails.id();
        entry.featureClass = featureClass;
        entry.mapping = FeatureMapping.build(featureClass);

        // Root path handling and child handling
        if (featureClass.isAnnotationPresent(AIXMRoot.class)) {

            AIXMRoot rootDetails = featureClass.getAnnotation(AIXMRoot.class);
            entry.rootPath = rootDetails.value();

        } else if (featureDetails.parent() != Feature.class) {

            // If parent is not "Feature" (i.e. if it has a valid parent)
            // then set it
            entry.parent = featureDetails.parent();
        }

        // Fetch the schema table for database information on this feature
        entry.schemaTable = SchemaManager.get(featureClass);

        return entry;
    }
}
