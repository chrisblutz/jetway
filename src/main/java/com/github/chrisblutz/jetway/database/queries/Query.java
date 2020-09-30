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

import com.github.chrisblutz.jetway.features.Feature;

/**
 * This class represents a SQL-style query to
 * narrow down information selected from the Jetway
 * database.
 *
 * @author Christopher Lutz
 */
public abstract class Query {

    /**
     * These values represent multiple comparison operations
     * used by {@link Query} instances.
     */
    @SuppressWarnings("javadoc")
    public enum QueryOperation {

        EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS, LIKE
    }

    /**
     * This method constructs a query to join the current
     * query to the specified query using an AND
     * conditional.
     *
     * @param query the query to join with
     * @return The two queries combined with an AND comparison
     */
    public abstract Query and(Query query);

    /**
     * This method constructs a query to join the current
     * query to the specified query using an OR
     * conditional.
     *
     * @param query the query to join with
     * @return The two queries combined with an OR comparison
     */
    public abstract Query or(Query query);

    /**
     * This method constructs a query to check if the specified
     * feature attribute is equal to the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is equal to the expected value
     */
    public static Query whereEquals(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.EQUALS);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute is not equal to the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is not equal to the expected value
     */
    public static Query whereNotEquals(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.NOT_EQUALS);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute is greater than the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is greater than the expected value
     */
    public static Query whereGreaterThan(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.GREATER_THAN);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute is greater than or equal to the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is greater than or equal to the expected value
     */
    public static Query whereGreaterThanEquals(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.GREATER_THAN_EQUALS);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute is less than the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is less than the expected value
     */
    public static Query whereLessThan(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.LESS_THAN);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute is less than or equal to the specified expected value.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute is less than or equal to the expected value
     */
    public static Query whereLessThanEquals(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.LESS_THAN_EQUALS);
    }

    /**
     * This method constructs a query to check if the specified
     * feature attribute matches the expected pattern.
     *
     * @param feature   the feature for the attribute
     * @param attribute the attribute name
     * @param expected  the expected value
     * @return The query to check if the attribute matches the expected pattern.
     */
    public static Query whereLike(Class<? extends Feature> feature, String attribute, Object expected) {

        return buildQuery(feature, attribute, expected, Query.QueryOperation.LIKE);
    }

    private static Query buildQuery(Class<? extends Feature> feature, String attribute, Object expected, Query.QueryOperation operation) {

        return new SingleQuery(feature, attribute, expected, operation);
    }
}
