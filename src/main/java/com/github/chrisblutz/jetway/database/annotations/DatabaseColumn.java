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
package com.github.chrisblutz.jetway.database.annotations;

import com.github.chrisblutz.jetway.database.DatabaseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link DatabaseColumn} annotation denotes that a field
 * should be included as a column in the feature's database
 * table.
 *
 * @author Christopher Lutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseColumn {

    /**
     * This attribute denotes the name of the column within
     * the database.
     *
     * @return The column name for this field
     */
    String name();

    /**
     * This attribute denotes the type of value that this column contains.
     * It must be one of the enum values in {@link DatabaseType}.
     *
     * @return The type of value contained in this column
     */
    DatabaseType type();
}
