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
package com.github.chrisblutz.jetway.database.managers.metadata;

/**
 * This class represents a generic piece of metadata.  It provides
 * utility methods for converting to and from Strings, the type
 * used to store metadata in Jetway's database.
 *
 * @param <T> the type of metadata being stored
 * @author Christopher Lutz
 */
public abstract class BasicMetadata<T> {

    private final String name;

    /**
     * Creates a new metadata instance with the specified column name.
     *
     * @param name the name of the column to be used
     */
    public BasicMetadata(String name) {

        this.name = name;
    }

    /**
     * This method retrieves the column name for this metadata.
     *
     * @return The column name for this metadata
     */
    public String getName() {

        return name;
    }

    /**
     * This method converts a stored {@link String} into the
     * correct type for this metadata instance.
     * <p>
     * This method is called when converting metadata from
     * the database-stored values to usable values.
     *
     * @param data the data to convert
     * @return The converted metadata value
     */
    public abstract T fromString(String data);

    /**
     * This method converts a metadata value into a {@link String}
     * for storage.
     * <p>
     * This method is called when converting metadata into values
     * suitable for storage in Jetway's database.
     *
     * @param object the value to convert
     * @return The metadata value as a {@link String}
     */
    public abstract String toString(T object);
}
