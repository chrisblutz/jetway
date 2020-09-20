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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a conditional combination
 * of multiple {@link Query} objects.
 *
 * @author Christopher Lutz
 */
public abstract class MultiQuery extends Query {

    /**
     * This {@link List} contains all of this query's
     * conditional {@link Query} objects
     */
    protected final List<Query> queries = new ArrayList<>();

    /**
     * This constructor generates a new query with
     * the specified {@link Query} objects as conditions.
     *
     * @param query        the first {@link Query}
     * @param otherQueries the subsequent {@link Query}
     */
    protected MultiQuery(Query query, Query... otherQueries) {

        queries.add(query);
        queries.addAll(Arrays.asList(otherQueries));
    }

    /**
     * This method retrieves the list of the {@link Query}
     * objects that are the conditions for this query.
     *
     * @return the list of {@link Query} objects
     */
    public List<Query> getQueries() {

        return queries;
    }
}
