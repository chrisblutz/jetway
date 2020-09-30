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
package com.github.chrisblutz.jetway.database.queries;

import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.database.mappings.SchemaTable;
import com.github.chrisblutz.jetway.features.Feature;

/**
 * This class represents a sorting order for
 * database selections.
 *
 * @author Christopher Lutz
 */
public class Sort {

    /**
     * These values represent various sorting directions
     * that can be used by {@link Sort} instances.
     */
    @SuppressWarnings("javadoc")
    public enum Order {

        ASCENDING, DESCENDING
    }

    private final SchemaTable table;
    private final String attribute;
    private final Order order;

    private Sort(SchemaTable table, String attribute, Order order) {

        this.table = table;
        this.attribute = attribute;
        this.order = order;
    }

    /**
     * This method retrieves the {@link SchemaTable} that contains the
     * attribute this instance sorts on.
     *
     * @return The {@link SchemaTable} that contains the sorting attribute
     */
    public SchemaTable getTable() {

        return table;
    }

    /**
     * This method retrieves the database attribute that this instance sorts on.
     *
     * @return The attribute this instance sorts on
     */
    public String getAttribute() {

        return attribute;
    }

    /**
     * This method retrieves the sorting {@link Order} for this instance.
     *
     * @return The sorting {@link Order}
     */
    public Order getOrder() {

        return order;
    }

    /**
     * This method creates a new sorting order based on the specified attribute
     * from the specified feature, ordering in the specified direction as defined
     * by the given {@link Order}.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param order     the sorting {@link Order}
     * @return The new sorting instance
     */
    public static Sort by(Class<? extends Feature> feature, String attribute, Order order) {

        SchemaTable table = SchemaManager.get(feature);
        return new Sort(table, attribute, order);
    }
}
