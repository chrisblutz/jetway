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

/**
 * This class represents a single condition in a
 * database query.
 *
 * @author Christopher Lutz
 */
public class SingleQuery extends Query {

    private final Class<?> feature;
    private final String attribute;
    private final Object expected;
    private final Query.QueryOperation operation;

    /**
     * This constructor builds a new query that checks the specified
     * feature's attribute against the expected value using the specified
     * comparison operation.
     *
     * @param feature   the feature to pull the attribute from
     * @param attribute the attribute name to check
     * @param expected  the expected value
     * @param operation the comparison operation to use
     */
    public SingleQuery(Class<?> feature, String attribute, Object expected, Query.QueryOperation operation) {

        this.feature = feature;
        this.attribute = attribute;
        this.expected = expected;
        this.operation = operation;
    }

    /**
     * This method retrieves the feature that this query
     * condition operates on.
     *
     * @return The feature that this query operates on
     */
    public Class<?> getFeature() {

        return feature;
    }

    /**
     * This method retrieves the name of the attribute
     * that this query operates on.
     *
     * @return The name of the attribute this query operates on
     */
    public String getAttribute() {

        return attribute;
    }

    /**
     * This method retrieves the expected value of the
     * attribute this query operates on.  For
     * less-than/greater-than comparisons, this
     * is the value to compare to.
     *
     * @return The expected value of the attribute
     */
    public Object getExpectedValue() {

        return expected;
    }

    /**
     * This method retrieves the comparison operator
     * this query is using to compare the attribute
     * to the expected value.
     *
     * @return The comparison operator this query is using
     */
    public Query.QueryOperation getOperation() {

        return operation;
    }

    @Override
    public Query and(Query query) {

        return new AndQuery(this, query);
    }

    @Override
    public Query or(Query query) {

        return new OrQuery(this, query);
    }
}
