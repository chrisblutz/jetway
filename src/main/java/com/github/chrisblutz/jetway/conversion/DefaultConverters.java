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

import aero.aixm.v5.CodeYesNoType;
import aero.aixm.v5.impl.CodeYesNoTypeImpl;
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
    public static final Converter<Boolean> BOOLEAN_CONVERTER = new Converter<Boolean>() {

        @Override
        public Boolean convert(Object value) {

            if (value instanceof CodeYesNoType) {

                return ((CodeYesNoType) value).getObjectValue() == CodeYesNoTypeImpl.YES;

            } else {

                return false;
            }
        }

        @Override
        public Boolean convert(String value) {

            return Boolean.parseBoolean(value);
        }

        @Override
        public Class<Boolean> getProducedType() {

            return Boolean.class;
        }

        @Override
        public Class<?>[] getAcceptedTypes() {

            return new Class<?>[]{CodeYesNoType.class};
        }
    };

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Byte} values.
     */
    public static final Converter<Byte> BYTE_CONVERTER = new BasicConverter<>((byte) 0, Byte.class, Byte::parseByte);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Character} values.
     */
    public static final Converter<Character> CHARACTER_CONVERTER = new BasicConverter<>('\0', Character.class, string -> string.length() == 1 ? string.charAt(0) : '\0');

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Double} values.
     */
    public static final Converter<Double> DOUBLE_CONVERTER = new BasicConverter<>(0d, Double.class, Double::parseDouble);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Float} values.
     */
    public static final Converter<Float> FLOAT_CONVERTER = new BasicConverter<>(0f, Float.class, Float::parseFloat);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Integer} values.
     */
    public static final Converter<Integer> INTEGER_CONVERTER = new BasicConverter<>(0, Integer.class, Integer::parseInt);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Long} values.
     */
    public static final Converter<Long> LONG_CONVERTER = new BasicConverter<>(0L, Long.class, Long::parseLong);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link String} values.
     */
    public static final Converter<String> STRING_CONVERTER = new BasicConverter<>(null, String.class, string -> string);

    /**
     * This converter converts basic XMLBeans types and basic {@link Object}s into {@link Short} values.
     */
    public static final Converter<Short> SHORT_CONVERTER = new BasicConverter<>((short) 0, Short.class, Short::parseShort);

    /**
     * This method registers converters for the main Java types, including
     * all primitive types and {@link String} values.
     */
    public static void registerAll() {

        JetwayLog.getJetwayLogger().info("Registering default value converters...");

        DataConversion.registerConverter(BOOLEAN_CONVERTER);
        DataConversion.registerConverter(BYTE_CONVERTER);
        DataConversion.registerConverter(CHARACTER_CONVERTER);
        DataConversion.registerConverter(DOUBLE_CONVERTER);
        DataConversion.registerConverter(FLOAT_CONVERTER);
        DataConversion.registerConverter(INTEGER_CONVERTER);
        DataConversion.registerConverter(LONG_CONVERTER);
        DataConversion.registerConverter(STRING_CONVERTER);
        DataConversion.registerConverter(SHORT_CONVERTER);
    }
}
