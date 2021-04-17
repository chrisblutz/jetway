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
import com.github.chrisblutz.jetway.features.*;
import com.github.chrisblutz.jetway.testing.utils.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

        Jetway.reset();
    }

    /**
     * This method ensures that all query features are correctly recognized by Jetway.
     * This allows for easier bug-finding when errors arise.  If this test fails,
     * the error is most likely in the loader itself.  If other tests fail when this
     * one passes, the error is most likely in the query functionality.
     */
    @Test
    public void testAll() {

        // Make sure assertions are correct by selecting all

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_ALL_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#EQUALS}
     * operation.
     */
    @Test
    public void testEquals() {

        // Checking SERVED_CITY = "CITY 1"

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "CITY 1");
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_EQUALS_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#NOT_EQUALS}
     * operation.
     */
    @Test
    public void testNotEquals() {

        // Checking SERVED_CITY != "CITY 1"

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereNotEquals(Airport.class, Airport.SERVED_CITY, "CITY 1");
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_NOT_EQUALS_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#GREATER_THAN}
     * operation.
     */
    @Test
    public void testGreaterThan() {

        // Checking FIELD_ELEVATION > 14

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_GREATER_THAN_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#GREATER_THAN_EQUALS}
     * operation.
     */
    @Test
    public void testGreaterThanEquals() {

        // Checking FIELD_ELEVATION >= 14

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation,
                ValidationFeatures.QUERY_AIRPORT_GREATER_THAN_EQUALS_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LESS_THAN}
     * operation.
     */
    @Test
    public void testLessThan() {

        // Checking FIELD_ELEVATION < 14

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_LESS_THAN_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LESS_THAN_EQUALS}
     * operation.
     */
    @Test
    public void testLessThanEquals() {

        // Checking FIELD_ELEVATION <= 14

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation,
                ValidationFeatures.QUERY_AIRPORT_LESS_THAN_EQUALS_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.Query.QueryOperation#LIKE}
     * operation.
     */
    @Test
    public void testLike() {

        // Checking NAME LIKE "%INTL"

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query query = Query.whereLike(Airport.class, Airport.NAME, "%INTL");
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_LIKE_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.AndQuery}
     * query type.
     */
    @Test
    public void testAnd() {

        // Check FIELD_ELEVATION > 12 AND LATITUDE < 50

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);

        // Check "and" on first
        Query query = firstQuery.and(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_AND_INDICES);

        // Check "and" on second (other way round)
        query = secondQuery.and(firstQuery);
        airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_AND_INDICES);
    }

    /**
     * This method tests the
     * {@link com.github.chrisblutz.jetway.database.queries.OrQuery}
     * query type.
     */
    @Test
    public void testOr() {

        // Check FIELD_ELEVATION < 12 OR LATITUDE > 46

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 46);

        // Check "or" on first
        Query query = firstQuery.or(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_OR_INDICES);

        // Check "or" on second (other way round)
        query = secondQuery.or(firstQuery);
        airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_OR_INDICES);
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

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -170);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_AND_AND_INDICES);
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

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -160);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_AND_OR_INDICES);
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

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 44) AND SERVED_CITY = "CITY 1"

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 44);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "CITY 1");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_OR_AND_INDICES);
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

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 48) OR SERVED_CITY = "CITY 1"

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 48);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "CITY 1");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_OR_OR_INDICES);
    }

    /**
     * This method tests the escaping of query
     * string values interacting with the database.
     */
    @Test
    public void testStringEscaping() {

        Airport[] validation = ValidationFeatures.QUERY_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        // Check that all airports are present
        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.QUERY_AIRPORT_ALL_INDICES);

        // Test query containing a reserved character - "
        // and check that the result is empty and does not throw an error
        Query query = Query.whereEquals(Airport.class, Airport.NAME, "\" SOME RANDOM VALUE");
        airports = Airport.selectAll(query);
        assertEquals(0, airports.length);
    }

    /**
     * This method ensures that all nested query features are correctly recognized
     * by Jetway.  This allows for easier bug-finding when errors arise.  If this
     * test fails, the error is most likely in the loader itself.  If other tests
     * fail when this one passes, the error is most likely in the query functionality.
     */
    @Test
    public void testAllNested() {

        // Make sure assertions are correct by selecting all

        Airport[] validationAirports = ValidationFeatures.QUERY_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.QUERY_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.QUERY_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.QUERY_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validationAirports,
                ValidationFeatures.QUERY_NESTED_AIRPORT_ALL_INDICES);

        Runway[] runways = Runway.selectAll(null);
        AssertionUtils.assertFeatures(runways, validationRunways,
                ValidationFeatures.QUERY_NESTED_RUNWAY_ALL_INDICES);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        AssertionUtils.assertFeatures(runwayEnds, validationRunwayEnds,
                ValidationFeatures.QUERY_NESTED_RUNWAY_END_ALL_INDICES);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        AssertionUtils.assertFeatures(runwayDirections, validationRunwayDirections,
                ValidationFeatures.QUERY_NESTED_RUNWAY_DIRECTION_ALL_INDICES);
    }

    /**
     * This method tests the querying
     * of features from within another feature
     * using {@link Query} instances.
     */
    @Test
    public void testNestedFeatures() {

        Airport[] validationAirports = ValidationFeatures.QUERY_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.QUERY_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.QUERY_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.QUERY_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validationAirports,
                ValidationFeatures.QUERY_NESTED_AIRPORT_ALL_INDICES);

        // Select runways where LENGTH >= 10000
        Airport airport = airports[0];

        Query query = Query.whereGreaterThanEquals(Runway.class, Runway.LENGTH, 10000);
        Runway[] runways = airport.getRunways(query);
        AssertionUtils.assertFeatures(runways, validationRunways,
                ValidationFeatures.QUERY_NESTED_RUNWAY_GREATER_THAN_EQUALS_INDICES);

        // Select runway ends where DESIGNATOR = 05
        query = Query.whereEquals(RunwayEnd.class, RunwayEnd.DESIGNATOR, "05");
        RunwayEnd[] runwayEnds = airport.getRunwayEnds(query);
        AssertionUtils.assertFeatures(runwayEnds, validationRunwayEnds,
                ValidationFeatures.QUERY_NESTED_RUNWAY_END_EQUALS_INDICES);
    }
}
