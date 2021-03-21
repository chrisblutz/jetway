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

    private final Class<T> producedClass;
    private final StringConverter<T> stringConverter;
    private final ValueConverter<T> valueConverter;

    /**
     * Creates a new basic converter that uses the specified {@link StringConverter} to
     * convert the incoming value's string representation to the converter's type.
     *
     * @param producedClass   the type of value produced by this converter
     * @param stringConverter the {@link StringConverter} to use when converting
     * @param valueConverter  the {@link ValueConverter} to use when converting
     */
    public BasicConverter(Class<T> producedClass, StringConverter<T> stringConverter, ValueConverter<T> valueConverter) {

        this.producedClass = producedClass;
        this.stringConverter = stringConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public T convert(Object value) {

        String string = getDefaultTypeAsString(value);
        return convert(string);
    }

    @Override
    public String convertToString(T value) {

        return valueConverter.convert(value);
    }

    @Override
    public T convert(String value) {

        return stringConverter.convert(value);
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
