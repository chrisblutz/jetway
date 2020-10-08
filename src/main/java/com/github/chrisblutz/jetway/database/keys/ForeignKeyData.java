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
package com.github.chrisblutz.jetway.database.keys;

import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;

/**
 * This class handles data pertaining to foreign
 * keys in database tables.
 *
 * @author Christopher Lutz
 */
public class ForeignKeyData {

    private final Class<? extends Feature> feature;
    private final Relationship relationship;

    /**
     * This constructor creates a new data instance
     * with the specified feature class and relationship.
     *
     * @param feature      the referenced table for this foreign key
     * @param relationship the relationship for this foreign key
     */
    public ForeignKeyData(Class<? extends Feature> feature, Relationship relationship) {

        this.feature = feature;
        this.relationship = relationship;
    }

    /**
     * This method gets the feature class referenced by
     * this foreign key.
     *
     * @return The referenced feature class for this foreign key
     */
    public Class<? extends Feature> getFeatureClass() {

        return feature;
    }

    /**
     * This method gets the database table referenced by
     * this foreign key.
     *
     * @return The referenced {@link SchemaTable} for this foreign key
     */
    public SchemaTable getFeatureTable() {

        return SchemaManager.get(getFeatureClass());
    }

    /**
     * This method gets the relationship for this
     * foreign key.
     *
     * @return The relationship for this foreign key
     */
    public Relationship getRelationship() {

        return relationship;
    }
}
