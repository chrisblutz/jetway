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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class handles testing of {@link Query}
 * functionality.
 *
 * @author Christopher Lutz
 */
public class QueryTests {

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    /**
     * This method ensures that all features in the {@code query_basic.xml}
     * file are correctly recognized by Jetway.  This allows for easier
     * bug-finding when errors arise.  If this test fails, the error is most
     * likely in the loader itself.  If other tests fail when this one passes,
     * the error is most likely in the query functionality.
     */
    @Test
    public void testAll() {

        // Make sure assertions are correct by selecting all

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 1, 2, 3, 4);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#EQUALS}
     * operation.
     */
    @Test
    public void testEquals() {

        // Checking SERVED_CITY = "FIRST CITY"

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 2);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#NOT_EQUALS}
     * operation.
     */
    @Test
    public void testNotEquals() {

        // Checking SERVED_CITY != "FIRST CITY"

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereNotEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 1, 3, 4);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#GREATER_THAN}
     * operation.
     */
    @Test
    public void testGreaterThan() {

        // Checking FIELD_ELEVATION > 14

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 3, 4);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#GREATER_THAN_EQUALS}
     * operation.
     */
    @Test
    public void testGreaterThanEquals() {

        // Checking FIELD_ELEVATION >= 14

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 2, 3, 4);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LESS_THAN}
     * operation.
     */
    @Test
    public void testLessThan() {

        // Checking FIELD_ELEVATION < 14

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 1);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LESS_THAN_EQUALS}
     * operation.
     */
    @Test
    public void testLessThanEquals() {

        // Checking FIELD_ELEVATION <= 14

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 1, 2);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LIKE}
     * operation.
     */
    @Test
    public void testLike() {

        // Checking NAME LIKE "%INTL"

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query query = Query.whereLike(Airport.class, Airport.NAME, "%INTL");
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 3);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * query type.
     */
    @Test
    public void testAnd() {

        // Check FIELD_ELEVATION > 12 AND LATITUDE < 50

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);

        // Check "and" on first
        Query query = firstQuery.and(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 2, 3);

        // Check "and" on second (other way round)
        query = secondQuery.and(firstQuery);
        airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 2, 3);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * query type.
     */
    @Test
    public void testOr() {

        // Check FIELD_ELEVATION < 12 OR LATITUDE > 46

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 46);

        // Check "or" on first
        Query query = firstQuery.or(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 3, 4);

        // Check "or" on second (other way round)
        query = secondQuery.or(firstQuery);
        airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 3, 4);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * query type followed by another
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * operation.
     */
    @Test
    public void testAndAnd() {

        // Check (FIELD_ELEVATION >= 12 AND LATITUDE < 50) AND LONGITUDE > -170

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -170);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 1, 2);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * query type followed by the
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * operation.
     */
    @Test
    public void testAndOr() {

        // Check (FIELD_ELEVATION > 12 AND LATITUDE < 50) OR LONGITUDE > -160

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -160);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 2, 3);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * query type followed by the
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * operation.
     */
    @Test
    public void testOrAnd() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 44) AND SERVED_CITY = "FIRST CITY"

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 44);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 2);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * query type followed by another
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * operation.
     */
    @Test
    public void testOrOr() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 48) OR SERVED_CITY = "FIRST CITY"

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 48);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 2, 4);
    }

    /**
     * This method tests the escaping of query
     * string values interacting with the database.
     */
    @Test
    public void testStringEscaping() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_basic.xml"));
        JetwayTesting.initializeJetway(source);

        // Check that all airports are present
        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_BASIC_AIRPORTS, 0, 1, 2, 3, 4);

        // Test query containing a reserved character - "
        // and check that the result is empty and does not throw an error
        Query query = Query.whereEquals(Airport.class, Airport.NAME, "\" SOME RANDOM VALUE");
        airports = Airport.selectAll(query);
        assertEquals(0, airports.length);
    }

    /**
     * This method ensures that all features in the {@code query_nested.xml}
     * file are correctly recognized by Jetway.  This allows for easier
     * bug-finding when errors arise.  If this test fails, the error is most
     * likely in the loader itself.  If other tests fail when this one passes,
     * the error is most likely in the query functionality.
     */
    @Test
    public void testAllNested() {

        // Make sure assertions are correct by selecting all

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_NESTED_AIRPORTS, 0);

        Runway[] runways = Runway.selectAll(null);
        JetwayAssertions.assertFeatures(runways, ValidationArrays.QUERY_NESTED_RUNWAYS, 0, 1, 2);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        JetwayAssertions.assertFeatures(runwayEnds, ValidationArrays.QUERY_NESTED_RUNWAY_ENDS, 0, 1);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        JetwayAssertions.assertFeatures(runwayDirections, ValidationArrays.QUERY_NESTED_RUNWAY_DIRECTIONS, 0);
    }

    /**
     * This method tests the querying
     * of features from within another feature
     * using {@link Query} instances.
     */
    @Test
    public void testNestedFeatures() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/query_nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.QUERY_NESTED_AIRPORTS, 0);

        // Select runways where LENGTH >= 10000
        Airport airport = Airport.select(Query.whereEquals(Airport.class, Airport.ID, "AH_0000001"));

        Query query = Query.whereGreaterThanEquals(Runway.class, Runway.LENGTH, 10000);
        Runway[] runways = airport.getRunways(query);
        JetwayAssertions.assertFeatures(runways, ValidationArrays.QUERY_NESTED_RUNWAYS, 1, 2);

        // Select runway ends where DESIGNATOR = 05
        Runway runway = Runway.select(Query.whereEquals(Runway.class, Runway.ID, "RWY_0000001_1"));

        query = Query.whereEquals(RunwayEnd.class, RunwayEnd.DESIGNATOR, "05");
        RunwayEnd[] runwayEnds = runway.getRunwayEnds(query);
        JetwayAssertions.assertFeatures(runwayEnds, ValidationArrays.QUERY_NESTED_RUNWAY_ENDS, 0);

        // Select runway directions where LATITUDE > 60 (should be empty)
        RunwayEnd runwayEnd = RunwayEnd.select(Query.whereEquals(RunwayEnd.class, RunwayEnd.ID, "RWY_BASE_END_0000001_1"));

        query = Query.whereGreaterThan(RunwayDirection.class, RunwayDirection.LATITUDE, 60);
        RunwayDirection[] runwayDirections = runwayEnd.getRunwayDirections(query);
        assertNotNull(runwayDirections);
        assertEquals(0, runwayDirections.length);

        // Select runway directions where LATITUDE < 60
        query = Query.whereLessThan(RunwayDirection.class, RunwayDirection.LATITUDE, 60);
        runwayDirections = runwayEnd.getRunwayDirections(query);
        JetwayAssertions.assertFeatures(runwayDirections, ValidationArrays.QUERY_NESTED_RUNWAY_DIRECTIONS, 0);

        // Select runway direction from runway end using getRunwayDirection()
        RunwayDirection runwayDirection = runwayEnd.getRunwayDirection();
        JetwayAssertions.assertFeature(runwayDirection, ValidationArrays.QUERY_NESTED_RUNWAY_DIRECTIONS[0]);
    }
}
