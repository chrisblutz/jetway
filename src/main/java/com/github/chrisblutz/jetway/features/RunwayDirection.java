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
 * A {@code RunwayDirection} represents the geographic
 * information for a {@link RunwayEnd}.
 *
 * @author Christopher Lutz
 */
@DatabaseTable("RunwayDirections")
@AIXMFeature(name = "RunwayDirection", id = "(RWY_DIRECTION_BASE_END|RWY_DIRECTION_RECIPROCAL_END)", aixmFile = Airport.AIXM_FILE)
public class RunwayDirection implements NestedFeature {

    public static final String ID = "id";
    public static final String RUNWAY_END_ID = "runwayEndId";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";

    @DatabaseColumn(name = ID, type = DatabaseType.STRING)
    @DatabasePrimaryKey
    @AIXMId
    public String id;

    @DatabaseColumn(name = RUNWAY_END_ID, type = DatabaseType.STRING)
    @DatabaseForeignKey(RunwayEnd.class)
    @AIXMForeign(feature = RunwayEnd.class, path = "UsedRunway")
    public String runwayEndId;

    @DatabaseColumn(name = LATITUDE, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/ElevatedPoint/Position/ListValue[1]")
    public Double latitude;

    @DatabaseColumn(name = LONGITUDE, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/ElevatedPoint/Position/ListValue[0]")
    public Double longitude;

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getParentId() {

        return runwayEndId;
    }

    @Override
    public void cacheDependencies() {

        // No dependencies exist
    }

    @Override
    public String toString() {

        return "RunwayDirection{" +
                "id='" + id + '\'' +
                ", runwayEndId='" + runwayEndId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * This method selects a single runway direction from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway directions.
     * <p>
     * There is no guarantee that calling this method twice
     * will yield the same runway direction.
     *
     * @param query the {@link Query} to use
     * @return The selected runway directions
     */
    public static RunwayDirection select(Query query) {

        return Database.select(RunwayDirection.class, query);
    }

    /**
     * This method selects all runway directions from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway directions.
     *
     * @param query the {@link Query} to use
     * @return The selected runway directions
     */
    public static RunwayDirection[] selectAll(Query query) {

        return Database.selectAll(RunwayDirection.class, query);
    }

    /**
     * This method selects all runway directions from the database
     * based on the {@link Query}, sorting based on the
     * defined {@link Sort} instance.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runway directions.
     * <p>
     * Passing a {@code null} {@link Sort} to this method
     * returns results in a database manager-specific
     * order that is not guaranteed to be consistent.
     *
     * @param query the {@link Query} to use
     * @param sort  the {@link Sort} defining the order to use
     * @return The selected runway directions
     */
    public static RunwayDirection[] selectAll(Query query, Sort sort) {

        return Database.selectAll(RunwayDirection.class, query, sort);
    }
}
