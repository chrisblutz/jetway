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

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles {@link Converter} instances, used for converting
 * data to other types.
 *
 * @author Christopher Lutz
 */
public final class DataConversion {

    private DataConversion() {}

    private static final Map<Class<?>, Converter<?>> converterMap = new HashMap<>();

    /**
     * This method converts the specified object to the requested type
     * using one of the registered {@link Converter} instances.
     * <p>
     * If no converter is found, this method returns {@code null}.
     *
     * @param data      the data to convert
     * @param typeClass the type to convert to
     * @return The data converted to the specified type, or {@code null}
     */
    public static Object get(Object data, Class<?> typeClass) {

        // Find the correct converter for the type and convert
        if (converterMap.containsKey(typeClass)) {
            Converter<?> converter = converterMap.get(typeClass);

            return getFromConverter(converter, data);
        }

        // If no converter was found, return null
        return null;
    }

    private static Object getFromConverter(Converter<?> converter, Object data) {

        // Make sure that the object is of an accepted type
        for (Class<?> accepted : converter.getAcceptedTypes()) {
            if (accepted.isInstance(data)) {

                // Convert the data
                return converter.convert(data);
            }
        }

        // If no converter was found, return null
        return null;
    }

    /**
     * This method converts the specified {@link String} to the requested type
     * using one of the registered {@link Converter} instances.
     * <p>
     * If no converter is found, this method returns {@code null}.
     *
     * @param data      the {@link String} to convert
     * @param typeClass the type to convert to
     * @return The data converted to the specified type, or {@code null}
     */
    public static Object getFromString(String data, Class<?> typeClass) {

        // Find the correct converter for the type and convert
        if (converterMap.containsKey(typeClass)) {
            Converter<?> converter = converterMap.get(typeClass);
            return converter.convert(data);
        }

        // If no converter was found, return null
        return null;
    }

    /**
     * This method registers a new {@link Converter}.
     *
     * @param converter the {@link Converter} to register.
     */
    public static void registerConverter(Converter<?> converter) {

        converterMap.put(converter.getProducedType(), converter);
    }

    /**
     * This method resets the converter registry.
     */
    public static void reset() {

        converterMap.clear();
    }
}
