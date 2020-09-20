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
package com.github.chrisblutz.jetway.database.queries;

import com.github.chrisblutz.jetway.database.mappings.SchemaTable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents the result of a database
 * query.  It contains a list of attribute mappings,
 * where each individual mapping represents a single
 * instance of a feature.
 *
 * @author Christopher Lutz
 */
public class DatabaseResult {

    private final List<Map<String, Object>> results = new ArrayList<>();

    /**
     * This method adds a new attribute mapping
     * to this result.
     *
     * @param result the attribute-value mapping
     */
    public void add(Map<String, Object> result) {

        results.add(result);
    }

    /**
     * This method creates an empty array for the specified
     * feature type.
     *
     * @param type the feature type class
     * @param <T>  the feature type
     * @return The empty array created
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getEmptyArray(Class<T> type) {

        return (T[]) Array.newInstance(type, 0);
    }

    /**
     * This method uses all of the attribute-value mappings this result
     * contains to construct an array of feature instances.
     * <p>
     * Each attribute-value mapping becomes a single instance.
     * <p>
     * If no mappings are present, an empty array is returned.
     *
     * @param type  the feature type class
     * @param table the table the query used
     * @param <T>   the feature type
     * @return The array of constructed features
     * @throws IllegalAccessException if the constructor or field is not accessible
     * @throws InstantiationException if an error occurs while creating the instance
     */
    public <T> T[] constructAll(Class<T> type, SchemaTable table) throws IllegalAccessException, InstantiationException {

        // Track current ID so we don't duplicate objects
        Object currentPrimary = null;
        List<T> resultList = new ArrayList<>();
        for (Map<String, Object> currentMap : results) {

            // If this is the same ID as the previous object, ignore it (prevents duplication due to joins)
            if (currentMap.get(table.getPrimaryKey()).equals(currentPrimary))
                continue;

            resultList.add(constructSingle(type, table, currentMap));
            currentPrimary = currentMap.get(table.getPrimaryKey());
        }

        T[] resultArray = getEmptyArray(type);
        return resultList.toArray(resultArray);
    }

    /**
     * This method uses a single attribute-value mapping this result
     * contains to construct a feature instances.
     * <p>
     * If no mappings are present, {@code null} is returned.
     *
     * @param type  the feature type class
     * @param table the table the query used
     * @param <T>   the feature type
     * @return The constructed feature, or {@code null}
     * @throws IllegalAccessException if the constructor or field is not accessible
     * @throws InstantiationException if an error occurs while creating the instance
     */
    public <T> T construct(Class<T> type, SchemaTable table) throws InstantiationException, IllegalAccessException {

        if (results.isEmpty())
            return null;

        return constructSingle(type, table, results.get(0));
    }

    private <T> T constructSingle(Class<T> type, SchemaTable table, Map<String, Object> result) throws IllegalAccessException, InstantiationException {

        T newInstance = type.newInstance();

        for (String attribute : table.getAttributes()) {

            Field field = table.getField(attribute);
            Object value = result.get(attribute);
            field.set(newInstance, value);
        }

        return newInstance;
    }
}
