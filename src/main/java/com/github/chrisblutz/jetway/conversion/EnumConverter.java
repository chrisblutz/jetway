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
 * A {@link EnumConverter} takes AIXM XML types and converts
 * them into Enum values.
 *
 * @param <T> the type produced by this converter
 * @author Christopher Lutz
 */
public abstract class EnumConverter<T extends Enum<T>> implements Converter<T> {

    private final Class<?>[] acceptedTypes;

    private final Class<T> producedClass;

    /**
     * Creates a new enum converter that uses accepts the specified AIXM types.
     *
     * @param producedClass the Enum type of value produced by this converter
     * @param accepted      the AIXM types that can be converted
     */
    public EnumConverter(Class<T> producedClass, Class<?>... accepted) {

        this.producedClass = producedClass;
        this.acceptedTypes = accepted;
    }

    @Override
    public String convertToString(T value) {

        return value.name();
    }

    @Override
    public T convert(String value) {

        return Enum.valueOf(producedClass, value);
    }

    @Override
    public Class<T> getProducedType() {

        return producedClass;
    }

    @Override
    public Class<?>[] getAcceptedTypes() {

        return acceptedTypes;
    }
}
