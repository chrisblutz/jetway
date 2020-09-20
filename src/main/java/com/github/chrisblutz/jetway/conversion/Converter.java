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
package com.github.chrisblutz.jetway.conversion;

/**
 * This interface defines the basic structure of a class
 * to convert an object of one type into an object of
 * another type.
 *
 * @param <T> the type of object produced by this class
 * @author Christopher Lutz
 */
public interface Converter<T> {

    /**
     * This method converts an {@link Object} (guaranteed to be
     * of one of the types listed in {@link #getAcceptedTypes()})
     * into the conversion type.
     * <p>
     * If conversion is not possible, {@code null} should be returned.
     *
     * @param value the value to convert
     * @return The converted value, or {@code null} if conversion is
     * not possible
     */
    T convert(Object value);

    /**
     * This method converts {@link String} into the conversion type.
     * <p>
     * If conversion is not possible, {@code null} should be returned.
     *
     * @param value the {@link String} to convert
     * @return The converted value, or {@code null} if conversion is
     * not possible
     */
    T convert(String value);

    /**
     * This method returns the {@link Class} of the objects
     * returned by this converter.
     *
     * @return The type created by this converter
     */
    Class<T> getProducedType();

    /**
     * This method returns all acceptable values for the input
     * to this converter.  {@link #convert(Object)} should be
     * able to handle all types in this array.
     *
     * @return An array of acceptable input types
     */
    Class<?>[] getAcceptedTypes();
}
