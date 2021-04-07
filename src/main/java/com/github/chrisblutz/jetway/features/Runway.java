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
 * A {@code Runway} represents a runway or a helipad at
 * and {@link Airport} and contains the size and designation
 * information for that runway/heliport.
 *
 * @author Christopher Lutz
 */
@DatabaseTable("Runways")
@AIXMFeature(name = "Runway", id = "RWY", aixmFile = Airport.AIXM_FILE)
public class Runway implements NestedFeature {

    public static final String ID = "id";
    public static final String AIRPORT_ID = "airportId";
    public static final String DESIGNATOR = "Designator";
    public static final String LENGTH = "Length";
    public static final String WIDTH = "Width";

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

    @DatabaseColumn(name = LENGTH, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/LengthStrip")
    public Double length;

    @DatabaseColumn(name = WIDTH, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/WidthStrip")
    public Double width;

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

        // No dependencies exist
    }

    @Override
    public String toString() {

        return "Runway{" +
                "id='" + id + '\'' +
                ", airportId='" + airportId + '\'' +
                ", designator='" + designator + '\'' +
                ", length=" + length +
                ", width=" + width +
                '}';
    }

    /**
     * This method selects a single runway from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runways.
     * <p>
     * There is no guarantee that calling this method twice
     * will yield the same runway.
     *
     * @param query the {@link Query} to use
     * @return The selected runway
     */
    public static Runway select(Query query) {

        return Database.select(Runway.class, query);
    }

    /**
     * This method selects all runways from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runways.
     *
     * @param query the {@link Query} to use
     * @return The selected runways
     */
    public static Runway[] selectAll(Query query) {

        return Database.selectAll(Runway.class, query);
    }

    /**
     * This method selects all runways from the database
     * based on the {@link Query}, sorting based on the
     * defined {@link Sort} instance.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all runways.
     * <p>
     * Passing a {@code null} {@link Sort} to this method
     * returns results in a database manager-specific
     * order that is not guaranteed to be consistent.
     *
     * @param query the {@link Query} to use
     * @param sort  the {@link Sort} defining the order to use
     * @return The selected runways
     */
    public static Runway[] selectAll(Query query, Sort sort) {

        return Database.selectAll(Runway.class, query, sort);
    }
}
