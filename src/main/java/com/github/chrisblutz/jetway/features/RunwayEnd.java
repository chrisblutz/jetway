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
package com.github.chrisblutz.jetway.features;

import com.github.chrisblutz.jetway.aixm.annotations.AIXMAttribute;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMForeign;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMId;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseForeignKey;
import com.github.chrisblutz.jetway.database.annotations.DatabasePrimaryKey;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.Sort;

/**
 * A {@code RunwayEnd} represents an end of a {@link Runway}
 * and contains the designation information for that end.
 *
 * @author Christopher Lutz
 */
@DatabaseTable("RunwayEnds")
@AIXMFeature(name = "Runway", id = "(RWY_BASE_END|RWY_RECIPROCAL_END)", aixmFile = Airport.AIXM_FILE)
public class RunwayEnd implements NestedFeature {

    public static final String ID = "id";
    public static final String AIRPORT_ID = "airportId";
    public static final String DESIGNATOR = "Designator";

    private RunwayDirection runwayDirection;

    @DatabaseColumn(name = ID, type = DatabaseType.STRING)
    @DatabasePrimaryKey
    @AIXMId
    public String id;

    @DatabaseColumn(name = AIRPORT_ID, type = DatabaseType.STRING)
    @DatabaseForeignKey(Airport.class)
    @AIXMForeign(feature = Airport.class, path = "AssociatedAirportHeliport")
    public String airportId;

    @DatabaseColumn(name = DESIGNATOR, type = DatabaseType.STRING)
    @AIXMAttribute("Feature/Designator")
    public String designator;

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getParentId() {

        return airportId;
    }

    @Override
    public void cacheDependencies() {

        // Load and cache runway direction
        Query query = Query.whereEquals(RunwayDirection.class, RunwayDirection.RUNWAY_END_ID, id);
        runwayDirection = RunwayDirection.select(query);
    }

    @Override
    public String toString() {

        return "RunwayEnd{" +
                "id='" + id + '\'' +
                ", airportId='" + airportId + '\'' +
                ", designator='" + designator + '\'' +
                '}';
    }

    /**
     * This method selects the runway direction for this
     * runway end from the database.
     * <p>
     * This method also caches its result, so calling this method
     * more than once will not result in more database calls.
     *
     * @return The {@link RunwayDirection} for this end
     */
    public RunwayDirection getRunwayDirection() {

        // Check if cached value exists
        if (runwayDirection == null) {

            Query query = Query.whereEquals(RunwayDirection.class, RunwayDirection.RUNWAY_END_ID, id);
            runwayDirection = RunwayDirection.select(query);
        }

        return runwayDirection;
    }

    /**
     * This method selects a single runway end from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway ends.
     * <p>
     * There is no guarantee that calling this method twice
     * will yield the same runway end.
     *
     * @param query the {@link Query} to use
     * @return The selected runway end
     */
    public static RunwayEnd select(Query query) {

        return Database.select(RunwayEnd.class, query);
    }

    /**
     * This method selects all runway ends from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway ends.
     *
     * @param query the {@link Query} to use
     * @return The selected runway ends
     */
    public static RunwayEnd[] selectAll(Query query) {

        return Database.selectAll(RunwayEnd.class, query);
    }

    /**
     * This method selects all runway ends from the database
     * based on the {@link Query}, sorting based on the
     * defined {@link Sort} instance.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway ends.
     * <p>
     * Passing a {@code null} {@link Sort} to this method
     * returns results in a database manager-specific
     * order that is not guaranteed to be consistent.
     *
     * @param query the {@link Query} to use
     * @param sort  the {@link Sort} defining the order to use
     * @return The selected runway ends
     */
    public static RunwayEnd[] selectAll(Query query, Sort sort) {

        return Database.selectAll(RunwayEnd.class, query, sort);
    }
}
