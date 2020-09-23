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
import com.github.chrisblutz.jetway.aixm.annotations.AIXMId;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMParent;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.database.queries.Query;

/**
 * A {@code Runway} represents a runway or a helipad at
 * and {@link Airport} and contains the size and designation
 * information for that runway/heliport.
 *
 * @author Christopher Lutz
 */
@DatabaseTable("Runways")
@AIXMFeature(name = "Runway", id = "RWY", parent = Airport.class)
public class Runway {

    public static final String ID = "id";
    public static final String AIRPORT_ID = "airportId";
    public static final String DESIGNATOR = "Designator";
    public static final String LENGTH = "Length";
    public static final String WIDTH = "Width";

    private RunwayEnd[] runwayEnds;

    @DatabaseColumn(name = ID, type = DatabaseType.STRING, primary = true)
    @AIXMId
    public String id;

    @DatabaseColumn(name = AIRPORT_ID, type = DatabaseType.STRING, foreign = true, foreignClass = Airport.class)
    @AIXMParent
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
     * This method selects all runway ends for this runway from
     * the database and returns them.
     * <p>
     * This method also caches its result, so calling this method
     * more than once will not result in more database calls.
     *
     * @return The array of runway ends
     */
    public RunwayEnd[] getRunwayEnds() {

        // Check if cached value exists
        if (runwayEnds == null) {

            Query query = Query.whereEquals(RunwayEnd.class, RunwayEnd.RUNWAY_ID, id);
            runwayEnds = RunwayEnd.selectAll(query);
        }

        return runwayEnds;
    }

    /**
     * This method selects all runway ends for this runway that
     * fit the {@link Query} from the database and returns them.
     * <p>
     * Unlike {@link #getRunwayEnds()}, this method does not cache its
     * result, so calling this method will result in further
     * database calls, even if the same {@link Query} is used.
     *
     * @param query the {@link Query} to use
     * @return The array of runway ends
     */
    public RunwayEnd[] getRunwayEnds(Query query) {

        Query fullQuery = query.and(Query.whereEquals(RunwayEnd.class, RunwayEnd.RUNWAY_ID, id));
        return RunwayEnd.selectAll(fullQuery);
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
}
