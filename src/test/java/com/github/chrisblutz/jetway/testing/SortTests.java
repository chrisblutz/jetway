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
package com.github.chrisblutz.jetway.testing;

import com.github.chrisblutz.jetway.Jetway;
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.Sort;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.features.RunwayDirection;
import com.github.chrisblutz.jetway.features.RunwayEnd;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import com.github.chrisblutz.jetway.testing.utils.ValidationArrays;
import org.junit.Before;
import org.junit.Test;

/**
 * This class handles testing of {@link Sort}
 * functionality.
 *
 * @author Christopher Lutz
 */
public class SortTests {

    /**
     * This method resets Jetway before each test
     */
    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    /**
     * This method ensures that all features in the {@code sorting_basic.xml}
     * file are correctly recognized by Jetway.  This allows for easier
     * bug-finding when errors arise.  If this test fails, the error is most
     * likely in the loader itself.  If other tests fail when this one passes,
     * the error is most likely in the sorting functionality.
     */
    @Test
    public void testAll() {

        // Make sure assertions are correct by selecting all

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 0, 1, 2, 3, 4);
    }

    /**
     * This method tests sorting by a string column.
     */
    @Test
    public void testSortStringAll() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway(source);

        // Sorting by NAME ascending

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 1, 0, 2, 4, 3);

        // Sorting by NAME descending

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 3, 4, 2, 0, 1);
    }

    /**
     * This method tests sorting by a numeric column.
     */
    @Test
    public void testSortNumericAll() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway(source);

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 2, 1, 4, 3, 0);

        // Sorting by LATITUDE descending

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 0, 3, 4, 1, 2);
    }

    /**
     * This method tests sorting by a string column
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortStringWithQuery() {


        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 10);

        // Sorting by NAME ascending where FIELD_ELEVATION > 10

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 1, 2, 4, 3);

        // Sorting by NAME descending where FIELD_ELEVATION > 10

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 3, 4, 2, 1);
    }

    /**
     * This method tests sorting by a numeric column
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortNumericWithQuery() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 16);

        // Sorting by LATITUDE ascending where FIELD_ELEVATION <= 16

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 2, 1, 3, 0);

        // Sorting by LATITUDE descending where FIELD_ELEVATION <= 16

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, ValidationArrays.SORTING_BASIC_AIRPORTS, 0, 3, 1, 2);
    }

    /**
     * This method ensures that all features in the {@code sorting_nested.xml}
     * file are correctly recognized by Jetway.  This allows for easier
     * bug-finding when errors arise.  If this test fails, the error is most
     * likely in the loader itself.  If other tests fail when this one passes,
     * the error is most likely in the sorting functionality.
     */
    @Test
    public void testAllNested() {

        // Make sure assertions are correct by selecting all

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.SORTING_NESTED_AIRPORTS, 0, 1, 2);

        Runway[] runways = Runway.selectAll(null);
        JetwayAssertions.assertFeatures(runways, ValidationArrays.SORTING_NESTED_RUNWAYS, 0, 1, 2, 3, 4, 5);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        JetwayAssertions.assertFeatures(runwayEnds, ValidationArrays.SORTING_NESTED_RUNWAY_ENDS,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        JetwayAssertions.assertFeatures(runwayDirections, ValidationArrays.SORTING_NESTED_RUNWAY_DIRECTIONS,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    }

    /**
     * This method tests sorting runways.
     */
    @Test
    public void testSortRunways() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        // Sorting by LENGTH ascending

        Sort sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.ASCENDING);
        Runway[] runways = Runway.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runways, ValidationArrays.SORTING_NESTED_RUNWAYS, 4, 2, 0, 1, 3, 5);

        // Sorting by LENGTH descending

        sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.DESCENDING);
        runways = Runway.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runways, ValidationArrays.SORTING_NESTED_RUNWAYS, 5, 3, 1, 0, 2, 4);
    }

    /**
     * This method tests sorting runways
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwaysWithQuery() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereGreaterThan(Runway.class, Runway.LENGTH, 10000);

        // Sorting by LENGTH ascending where LENGTH > 10000

        Sort sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.ASCENDING);
        Runway[] runways = Runway.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runways, ValidationArrays.SORTING_NESTED_RUNWAYS, 1, 3, 5);

        // Sorting by LENGTH descending where LENGTH > 10000

        sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.DESCENDING);
        runways = Runway.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runways, ValidationArrays.SORTING_NESTED_RUNWAYS, 5, 3, 1);
    }

    /**
     * This method tests sorting runway ends.
     */
    @Test
    public void testSortRunwayEnds() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        // Sorting by DESIGNATOR ascending

        Sort sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.ASCENDING);
        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayEnds, ValidationArrays.SORTING_NESTED_RUNWAY_ENDS,
                0, 4, 8, 2, 6, 10, 1, 5, 9, 3, 7, 11);

        // Sorting by DESIGNATOR descending

        sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.DESCENDING);
        runwayEnds = RunwayEnd.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayEnds, ValidationArrays.SORTING_NESTED_RUNWAY_ENDS,
                11, 7, 3, 9, 5, 1, 10, 6, 2, 8, 4, 0);
    }

    /**
     * This method tests sorting runway ends
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwayEndsWithQuery() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereLike(RunwayEnd.class, RunwayEnd.DESIGNATOR, "0_");

        // Sorting by DESIGNATOR ascending

        Sort sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.ASCENDING);
        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayEnds, ValidationArrays.SORTING_NESTED_RUNWAY_ENDS, 0, 4, 8);

        // Sorting by DESIGNATOR descending

        sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.DESCENDING);
        runwayEnds = RunwayEnd.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayEnds, ValidationArrays.SORTING_NESTED_RUNWAY_ENDS, 8, 4, 0);
    }

    /**
     * This method tests sorting runway directions.
     */
    @Test
    public void testSortRunwayDirections() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.ASCENDING);
        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayDirections, ValidationArrays.SORTING_NESTED_RUNWAY_DIRECTIONS,
                9, 8, 5, 4, 0, 1, 2, 3, 6, 7, 10, 11);

        // Sorting by LATITUDE descending

        sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.DESCENDING);
        runwayDirections = RunwayDirection.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayDirections, ValidationArrays.SORTING_NESTED_RUNWAY_DIRECTIONS,
                11, 10, 7, 6, 3, 2, 1, 0, 4, 5, 8, 9);
    }

    /**
     * This method tests sorting runway directions
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwayDirectionsWithQuery() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/sorting_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereGreaterThan(RunwayDirection.class, RunwayDirection.LATITUDE, 50);

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.ASCENDING);
        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayDirections, ValidationArrays.SORTING_NESTED_RUNWAY_DIRECTIONS,
                0, 1, 2, 3, 6, 7, 10, 11);

        // Sorting by LATITUDE descending

        sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.DESCENDING);
        runwayDirections = RunwayDirection.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(runwayDirections, ValidationArrays.SORTING_NESTED_RUNWAY_DIRECTIONS,
                11, 10, 7, 6, 3, 2, 1, 0);
    }
}
