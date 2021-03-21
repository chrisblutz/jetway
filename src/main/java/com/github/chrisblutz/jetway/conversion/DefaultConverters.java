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

import com.github.chrisblutz.jetway.conversion.types.BooleanConverter;
import com.github.chrisblutz.jetway.conversion.types.OwnershipConverter;
import com.github.chrisblutz.jetway.features.fields.Ownership;
import com.github.chrisblutz.jetway.logging.JetwayLog;

/**
 * This class contains default {@link Converter} instances
 * for basic Java types.
 *
 * @author Christopher Lutz
 */
public final class DefaultConverters {

    private DefaultConverters() {}

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Boolean} values.
     */
    public static final Converter<Boolean> BOOLEAN_CONVERTER = new BooleanConverter();

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Byte} values.
     */
    public static final Converter<Byte> BYTE_CONVERTER = new BasicConverter<>(Byte.class, Byte::parseByte, (value) -> Byte.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Character} values.
     */
    public static final Converter<Character> CHARACTER_CONVERTER = new BasicConverter<>(Character.class, string -> string.length() == 1 ? string.charAt(0) : '\0', (value) -> Character.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Double} values.
     */
    public static final Converter<Double> DOUBLE_CONVERTER = new BasicConverter<>(Double.class, Double::parseDouble, (value) -> Double.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Float} values.
     */
    public static final Converter<Float> FLOAT_CONVERTER = new BasicConverter<>(Float.class, Float::parseFloat, (value) -> Float.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Integer} values.
     */
    public static final Converter<Integer> INTEGER_CONVERTER = new BasicConverter<>(Integer.class, Integer::parseInt, (value) -> Integer.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Long} values.
     */
    public static final Converter<Long> LONG_CONVERTER = new BasicConverter<>(Long.class, Long::parseLong, (value) -> Long.toString(value));

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link String} values.
     */
    public static final Converter<String> STRING_CONVERTER = new BasicConverter<>(String.class, string -> string, value -> value);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Short} values.
     */
    public static final Converter<Short> SHORT_CONVERTER = new BasicConverter<>(Short.class, Short::parseShort, (value) -> Short.toString(value));

    /**
     * This converter converts AIXM ownership enum types into {@link Ownership} values.
     */
    public static final Converter<Ownership> OWNERSHIP_CONVERTER = new OwnershipConverter();

    /**
     * This method registers converters for the main Java types, including
     * all primitive types and {@link String} values.
     */
    public static void registerAll() {

        JetwayLog.getJetwayLogger().info("Registering default value converters...");

        // Basic type converters
        DataConversion.registerConverter(BOOLEAN_CONVERTER);
        DataConversion.registerConverter(BYTE_CONVERTER);
        DataConversion.registerConverter(CHARACTER_CONVERTER);
        DataConversion.registerConverter(DOUBLE_CONVERTER);
        DataConversion.registerConverter(FLOAT_CONVERTER);
        DataConversion.registerConverter(INTEGER_CONVERTER);
        DataConversion.registerConverter(LONG_CONVERTER);
        DataConversion.registerConverter(STRING_CONVERTER);
        DataConversion.registerConverter(SHORT_CONVERTER);

        // Enum converters
        DataConversion.registerConverter(OWNERSHIP_CONVERTER);
    }
}
