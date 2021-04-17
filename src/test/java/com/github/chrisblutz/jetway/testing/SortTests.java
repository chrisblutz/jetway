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
import com.github.chrisblutz.jetway.features.*;
import com.github.chrisblutz.jetway.testing.utils.*;
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

        Airport[] validationAirports = ValidationFeatures.SORT_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationAirports);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validationAirports, ValidationFeatures.SORT_AIRPORT_ALL_INDICES);
    }

    /**
     * This method tests sorting by a string column.
     */
    @Test
    public void testSortStringAll() {

        Airport[] validationAirports = ValidationFeatures.SORT_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationAirports);
        TestUtils.initializeJetway(source);

        // Sorting by NAME ascending

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_STRING_ASC_INDICES);

        // Sorting by NAME descending

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_STRING_DESC_INDICES);
    }

    /**
     * This method tests sorting by a numeric column.
     */
    @Test
    public void testSortNumericAll() {

        Airport[] validationAirports = ValidationFeatures.SORT_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationAirports);
        TestUtils.initializeJetway(source);

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_NUMERIC_ASC_INDICES);

        // Sorting by LATITUDE descending

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_NUMERIC_DESC_INDICES);
    }

    /**
     * This method tests sorting by a string column
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortStringWithQuery() {

        Airport[] validationAirports = ValidationFeatures.SORT_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationAirports);
        TestUtils.initializeJetway(source);

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 10);

        // Sorting by NAME ascending where FIELD_ELEVATION > 10

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_QUERY_STRING_ASC_INDICES);

        // Sorting by NAME descending where FIELD_ELEVATION > 10

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_QUERY_STRING_DESC_INDICES);
    }

    /**
     * This method tests sorting by a numeric column
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortNumericWithQuery() {

        Airport[] validationAirports = ValidationFeatures.SORT_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationAirports);
        TestUtils.initializeJetway(source);

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 16);

        // Sorting by LATITUDE ascending where FIELD_ELEVATION <= 16

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_QUERY_NUMERIC_ASC_INDICES);

        // Sorting by LATITUDE descending where FIELD_ELEVATION <= 16

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(airports, validationAirports,
                ValidationFeatures.SORT_AIRPORT_QUERY_NUMERIC_DESC_INDICES);
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

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
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
                ValidationFeatures.SORT_NESTED_AIRPORT_ALL_INDICES);

        Runway[] runways = Runway.selectAll(null);
        AssertionUtils.assertFeatures(runways, validationRunways,
                ValidationFeatures.SORT_NESTED_RUNWAY_ALL_INDICES);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        AssertionUtils.assertFeatures(runwayEnds, validationRunwayEnds,
                ValidationFeatures.SORT_NESTED_RUNWAY_END_ALL_INDICES);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        AssertionUtils.assertFeatures(runwayDirections, validationRunwayDirections,
                ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTION_ALL_INDICES);
    }

    /**
     * This method tests sorting runways.
     */
    @Test
    public void testSortRunways() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Sorting by LENGTH ascending

        Sort sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.ASCENDING);
        Runway[] runways = Runway.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runways, validationRunways,
                ValidationFeatures.SORT_NESTED_RUNWAY_SORTED_ASC_INDICES);

        // Sorting by LENGTH descending

        sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.DESCENDING);
        runways = Runway.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runways, validationRunways,
                ValidationFeatures.SORT_NESTED_RUNWAY_SORTED_DESC_INDICES);
    }

    /**
     * This method tests sorting runways
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwaysWithQuery() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        Query query = Query.whereGreaterThan(Runway.class, Runway.LENGTH, 10000);

        // Sorting by LENGTH ascending where LENGTH > 10000

        Sort sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.ASCENDING);
        Runway[] runways = Runway.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runways, validationRunways,
                ValidationFeatures.SORT_NESTED_RUNWAY_QUERY_SORTED_ASC_INDICES);

        // Sorting by LENGTH descending where LENGTH > 10000

        sort = Sort.by(Runway.class, Runway.LENGTH, Sort.Order.DESCENDING);
        runways = Runway.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runways, validationRunways,
                ValidationFeatures.SORT_NESTED_RUNWAY_QUERY_SORTED_DESC_INDICES);
    }

    /**
     * This method tests sorting runway ends.
     */
    @Test
    public void testSortRunwayEnds() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Sorting by DESIGNATOR ascending

        Sort sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.ASCENDING);
        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runwayEnds, validationRunwayEnds,
                ValidationFeatures.SORT_NESTED_RUNWAY_END_SORTED_ASC_INDICES);

        // Sorting by DESIGNATOR descending

        sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.DESCENDING);
        runwayEnds = RunwayEnd.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runwayEnds, validationRunwayEnds,
                ValidationFeatures.SORT_NESTED_RUNWAY_END_SORTED_DESC_INDICES);
    }

    /**
     * This method tests sorting runway ends
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwayEndsWithQuery() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        Query query = Query.whereLike(RunwayEnd.class, RunwayEnd.DESIGNATOR, "0_");

        // Sorting by DESIGNATOR ascending where DESIGNATOR like 0_

        Sort sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.ASCENDING);
        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runwayEnds, validationRunwayEnds,
                ValidationFeatures.SORT_NESTED_RUNWAY_END_QUERY_SORTED_ASC_INDICES);

        // Sorting by DESIGNATOR descending where DESIGNATOR like 0_

        sort = Sort.by(RunwayEnd.class, RunwayEnd.DESIGNATOR, Sort.Order.DESCENDING);
        runwayEnds = RunwayEnd.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runwayEnds, validationRunwayEnds,
                ValidationFeatures.SORT_NESTED_RUNWAY_END_QUERY_SORTED_DESC_INDICES);
    }

    /**
     * This method tests sorting runway directions.
     */
    @Test
    public void testSortRunwayDirections() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.ASCENDING);
        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runwayDirections, validationRunwayDirections,
                ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTION_SORTED_ASC_INDICES);

        // Sorting by LATITUDE descending

        sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.DESCENDING);
        runwayDirections = RunwayDirection.selectAll(null, sort);
        AssertionUtils.assertFeaturesOrdered(runwayDirections, validationRunwayDirections,
                ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTION_SORTED_DESC_INDICES);
    }

    /**
     * This method tests sorting runway directions
     * while also selecting a specific {@link Query}.
     */
    @Test
    public void testSortRunwayDirectionsWithQuery() {

        Airport[] validationAirports = ValidationFeatures.SORT_NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.SORT_NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.SORT_NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTIONS;
        Feature[] features = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        Query query = Query.whereGreaterThanEquals(RunwayDirection.class, RunwayDirection.LATITUDE, 50);

        // Sorting by LATITUDE ascending where LATITUDE >= 50

        Sort sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.ASCENDING);
        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runwayDirections, validationRunwayDirections,
                ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_ASC_INDICES);

        // Sorting by LATITUDE descending where LATITUDE >= 50

        sort = Sort.by(RunwayDirection.class, RunwayDirection.LATITUDE, Sort.Order.DESCENDING);
        runwayDirections = RunwayDirection.selectAll(query, sort);
        AssertionUtils.assertFeaturesOrdered(runwayDirections, validationRunwayDirections,
                ValidationFeatures.SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_DESC_INDICES);
    }
}
