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
package com.github.chrisblutz.jetway.testing.features;

import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabasePrimaryKey;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.features.Feature;

/**
 * This class is used when testing for attributes
 * declared as primitive types.
 *
 * @author Christopher lutz
 */
@DatabaseTable("Table")
public class PrimitiveTypeFieldFeature implements Feature {

    /**
     * This attribute should not affect the test.
     */
    @DatabaseColumn(name = "id", type = DatabaseType.STRING)
    @DatabasePrimaryKey
    public String id;

    /**
     * This attribute should cause an error
     * because is is a primitive type.
     */
    @DatabaseColumn(name = "Test", type = DatabaseType.DOUBLE)
    public double test;

    @Override
    public String getId() {

        return id;
    }
}
