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

import com.github.chrisblutz.jetway.aixm.annotations.AIXMAttribute;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMId;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMParent;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMFeatureException;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link FeatureMapping} serves as a container to track the mappings between
 * AIXM entries and feature fields.
 *
 * @author Christopher Lutz
 */
public class FeatureMapping {

    private final Map<Field, String> fieldMap = new HashMap<>();
    private Field idField, parentField;

    /**
     * This method retrieves all registered {@link Field}s.
     *
     * @return A {@link Set} containing all registered {@link Field}s
     */
    public Set<Field> getFields() {

        return fieldMap.keySet();
    }

    /**
     * This method retrieves the path to the data for the specified
     * {@link Field}.
     *
     * @param field the {@link Field} to find the path for
     * @return The path to the data
     */
    public String getPathForField(Field field) {

        return fieldMap.get(field);
    }

    /**
     * This method retrieves the {@link Field} defined to hold
     * the feature ID, annotated using {@link AIXMId}.
     *
     * @return The {@link Field} defined to hold the feature ID
     */
    public Field getIDField() {

        return idField;
    }

    /**
     * This method retrieves the {@link Field} defined to hold
     * the parent feature's ID, annotated using {@link AIXMParent}.
     * <p>
     * If this feature does not have a parent, this will return
     * {@code null}.
     *
     * @return The {@link Field} defined to hold the parent
     * feature's ID, or {@code null} if this feature does not
     * have a parent
     */
    public Field getParentField() {

        return parentField;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        for (Field field : fieldMap.keySet()) {

            string.append("Attribute{").append("name='").append(field.getName()).append("'")
                    .append(", path='").append(fieldMap.get(field)).append("'")
                    .append(", type=").append(field.getType().getName()).append("}\n");
        }

        return string.toString().trim();
    }

    /**
     * This method builds a new mapping based on the fields
     * in a feature class.  Only fields annotated with
     * {@link AIXMAttribute} are considered.
     *
     * @param featureClass the class to build from
     * @return The mapping for the {@link Field}s in the class
     */
    public static FeatureMapping build(Class<?> featureClass) {

        // Check that the class provided is actually an AIXM feature
        if (!featureClass.isAnnotationPresent(AIXMFeature.class))
            return null;

        // Generate new feature map
        FeatureMapping map = new FeatureMapping();

        for (Field field : featureClass.getFields())
            buildEntryFromField(featureClass, field, map);

        return map;
    }

    private static void buildEntryFromField(Class<?> featureClass, Field field, FeatureMapping map) {

        // Check if this attribute is the ID attribute
        if (field.isAnnotationPresent(AIXMId.class)) {
            if (field.getType() != String.class) {
                AIXMFeatureException exception = new AIXMFeatureException(featureClass, "ID field '" + field.getName() + "' must be of type String.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }

            map.idField = field;
            return;
        }

        // Check if this attribute is the parent attribute
        if (field.isAnnotationPresent(AIXMParent.class)) {
            if (field.getType() != String.class) {
                AIXMFeatureException exception = new AIXMFeatureException(featureClass, "Parent ID field '" + field.getName() + "' must be of type String.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }

            map.parentField = field;
            return;
        }

        // Check that the field provided is actually an AIXM attribute
        if (!field.isAnnotationPresent(AIXMAttribute.class))
            return;

        AIXMAttribute attributeDetails = field.getAnnotation(AIXMAttribute.class);

        String path = attributeDetails.value();

        map.fieldMap.put(field, path);
    }
}
