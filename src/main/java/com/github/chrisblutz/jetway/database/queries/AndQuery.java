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
 * This class represents an AND conditional
 * between multiple {@link Query} objects.
 *
 * @author Christopher Lutz
 */
public class AndQuery extends MultiQuery {

    /**
     * This constructor generates a new query with
     * the specified {@link Query} objects as conditions.
     *
     * @param query        the first {@link Query}
     * @param otherQueries the subsequent {@link Query}
     */
    protected AndQuery(Query query, Query... otherQueries) {

        super(query, otherQueries);
    }

    @Override
    public Query and(Query query) {

        queries.add(query);
        return this;
    }

    @Override
    public Query or(Query query) {

        return new OrQuery(this, query);
    }
}
