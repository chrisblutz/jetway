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
package com.github.chrisblutz.jetway.aixm.crawling.conversion;

import org.apache.xmlbeans.XmlAnySimpleType;

/**
 * A {@link BasicConverter} takes AIXM XML types or basic Java objects
 * and converts them to another type.
 *
 * @param <T> the type produced by this converter
 * @author Christopher Lutz
 */
public class BasicConverter<T> implements Converter<T> {

    private static final Class<?>[] DEFAULT_ACCEPTED_TYPES = new Class<?>[]{XmlAnySimpleType.class, Object.class};

    private final T defaultValue;
    private final Class<T> producedClass;
    private final StringConverter<T> converter;

    /**
     * Creates a new basic converter that uses the specified {@link StringConverter} to
     * convert the incoming value's string representation to the converter's type.
     *
     * @param defaultValue  the default value if incoming value is {@code null}
     * @param producedClass the type of value produced by this converter
     * @param converter     the {@link StringConverter} to use when converting
     */
    public BasicConverter(T defaultValue, Class<T> producedClass, StringConverter<T> converter) {

        this.defaultValue = defaultValue;
        this.producedClass = producedClass;
        this.converter = converter;
    }

    @Override
    public T convert(Object value) {

        String string = getDefaultTypeAsString(value);
        if (string == null) {

            return defaultValue;

        } else {

            return converter.convert(string);
        }
    }

    @Override
    public Class<T> getProducedType() {

        return producedClass;
    }

    @Override
    public Class<?>[] getAcceptedTypes() {

        return DEFAULT_ACCEPTED_TYPES;
    }

    private static String getDefaultTypeAsString(Object value) {

        if (value instanceof XmlAnySimpleType) {

            return ((XmlAnySimpleType) value).getStringValue();

        } else {

            return value.toString();
        }
    }
}
