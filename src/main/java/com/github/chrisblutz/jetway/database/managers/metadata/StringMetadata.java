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
 * This class represents a piece of metadata in the form
 * of a {@link String}.
 *
 * @author Christopher Lutz
 * @see BasicMetadata
 */
public class StringMetadata extends BasicMetadata<String> {

    /**
     * Creates a new metadata instance with the specified column name.
     *
     * @param name the name of the column to be used
     */
    public StringMetadata(String name) {

        super(name);
    }

    @Override
    public String fromString(String data) {

        return data;
    }

    @Override
    public String toString(String object) {

        return object;
    }
}
