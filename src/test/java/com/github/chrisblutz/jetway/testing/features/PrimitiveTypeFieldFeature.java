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
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;

@DatabaseTable("Table")
public class PrimitiveTypeFieldFeature {

    @DatabaseColumn(name = "id", type = DatabaseType.STRING, primary = true)
    public String id;

    @DatabaseColumn(name = "Test", type = DatabaseType.DOUBLE)
    public double test;
}
