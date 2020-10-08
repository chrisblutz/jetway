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
package com.github.chrisblutz.jetway.aixm.crawling;

import com.github.chrisblutz.jetway.aixm.annotations.AIXMAttribute;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.conversion.DataConversion;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.batches.DatabaseBatching;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link AIXMData} instance represents a single entry
 * in an AIXM file.  It is usually identified via a path
 * as described in {@link AIXMAttribute#value()}.
 * <p>
 * This class provides helper methods for using XMLBeans
 * classes and instances containing AIXM data.
 *
 * @author Christopher Lutz
 */
public class AIXMData {

    private static final String PATH_REGEX = "([a-zA-Z0-9]+)\\[([0-9]+)]";
    private static final Pattern PATH_PATTERN = Pattern.compile(PATH_REGEX);

    private static final String ID_REGEX = ".*\\[@gml:id='([A-Z_]+_[0-9_]+)']";
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    /**
     * This object represents the AIXM data in raw form.  Methods in
     * this class or subclasses operate on this data but cannot
     * alter it directly.
     */
    protected final Object data;

    /**
     * This constructor generates a new instance
     * that uses the specified {@link Object} as its underlying
     * data.
     *
     * @param data the AIXM data to use
     */
    public AIXMData(Object data) {

        this.data = data;
    }

    /**
     * This method converts the underlying AIXM data into
     * the requested type using one of the
     * {@link com.github.chrisblutz.jetway.conversion.Converter Converter}
     * instances registered via the {@link DataConversion} class.
     * <p>
     * This method can return {@code null} if one of the following is true:
     * the underlying data is {@code null}, the underlying data cannot be converted
     * into the requested type, or no converter exists for the requested type.
     *
     * @param typeClass the type to convert into
     * @return The underlying data as the specified type (or {@code null})
     */
    public Object get(Class<?> typeClass) {

        return DataConversion.get(data, typeClass);
    }

    /**
     * This method extracts a foreign ID from the underlying AIXM data.
     * The underlying data is formatted as a URL to data in the AIXM file,
     * so this method pulls out the ID for the referenced feature.
     *
     * @param feature the type of feature the foreign ID references
     * @return The foreign ID as a String, or {@code null} if it couldn't be extracted
     */
    public String decodeID(Class<? extends Feature> feature) {

        try {

            // Pull 'href' attribute from XMLBeans property type and URL-decode it
            String href = data.getClass().getMethod("getHref").invoke(data).toString();
            href = URLDecoder.decode(href, "UTF-8");

            Matcher matcher = ID_PATTERN.matcher(href);
            if (matcher.matches()) {

                // Extract ID from regular expression
                String id = matcher.group(1);

                // Find schema table for feature
                SchemaTable table = SchemaManager.get(feature);

                // Insert placeholder key into database to avoid foreign key constraint issues
                DatabaseBatching.addPrimaryKey(table, id);

                return id;
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | UnsupportedEncodingException e) {

            AIXMException exception = new AIXMException("An error occurred while decoding a foreign ID for feature '" + feature.getSimpleName() + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }

        return null;
    }

    /**
     * This method extracts child data from within the current data
     * using a specific path.
     * <p>
     * The path is constructed as follows:
     * <p>
     * The first element may be either:<br>
     * - Feature, indicating that the data is present in the basic feature<br>
     * - Extension, indicating that the data is present in the AIXM extension
     * <p>
     * The following elements are the names of the 'get' methods used
     * to retrieve the information.
     * <p>
     * Each element of the path may also contain an index like this: [0]<br>
     * This indicates that the path returns a list/array and tells the parser
     * to retrieve the value in that list/array at the specific index.
     * <p>
     * Path elements should be separated by forward slashes (/).
     * <p>
     * Example path to the latitude in a
     * {@link com.github.chrisblutz.jetway.features.RunwayDirection RunwayDirection} feature:
     * <p>
     * {@code Extension/ElevatedPoint/Position/ListValue[1]}
     *
     * @param path the path to crawl to
     * @return The new instance constructed from the crawled data
     */
    public AIXMData crawl(String path) {

        try {

            // Extract first path element (i.e. the next step in the crawl)
            String subsequentCrawl = null;
            if (path.contains("/")) {

                String[] parts = path.split("/", 2);
                path = parts[0];
                subsequentCrawl = parts[1];
            }

            // Check if current path contains an index
            int index = -1;
            Matcher matcher = PATH_PATTERN.matcher(path);
            if (matcher.matches()) {

                path = matcher.group(1);
                index = Integer.parseInt(matcher.group(2));
            }

            return performCrawl(path, subsequentCrawl, index);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            AIXMException exception = new AIXMException("An error occurred while crawling to '" + path + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private AIXMData performCrawl(String path, String subsequentCrawl, int index) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Invoke next path method, and return null data instance if that path is invalid or null
        Object newData = data.getClass().getMethod("get" + path).invoke(data);
        if (newData == null)
            return AIXMNullData.getInstance();

        AIXMData initialResult = new AIXMData(newData);

        // If an index was given, return that element
        if (index >= 0)
            initialResult = initialResult.index(index);

        // Perform the next crawl step if it exists
        if (subsequentCrawl != null)
            return initialResult.crawl(subsequentCrawl);

        return initialResult;
    }

    /**
     * This method retrieves data from the underlying data for this object, assuming
     * that object is an supported data structure (currently supported are {@link List}s
     * and any arrays).
     * <p>
     * This method suppresses index-out-of-bounds errors and simply returns the
     * {@link AIXMNullData} instance if something goes wrong.
     *
     * @param index the index to retrieve
     * @return The new instance for the data at the specified index
     */
    @SuppressWarnings("rawtypes")
    public AIXMData index(int index) {

        // Raw Lists are needed here because that is what XMLBeans produces
        if (data instanceof List && ((List) data).size() > index) {

            return new AIXMData(((List) data).get(index));

        } else if (data instanceof Object[] && ((Object[]) data).length > index) {

            return new AIXMData(((Object[]) data)[index]);

        } else {

            return AIXMNullData.getInstance();
        }
    }
}
