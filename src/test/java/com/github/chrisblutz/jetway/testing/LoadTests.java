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
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMStreamSource;
import com.github.chrisblutz.jetway.database.enforcement.Enforcement;
import com.github.chrisblutz.jetway.features.*;
import com.github.chrisblutz.jetway.testing.utils.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class handles testing of basic AIXM feature-loading
 * functionality.
 *
 * @author Christopher Lutz
 */
public class LoadTests {

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        Jetway.reset();
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with no AIXM extension.
     */
    @Test
    public void testBasicLoad() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with an AIXM extension.
     */
    @Test
    public void testBasicExtensionLoad() {

        Airport validation = ValidationFeatures.BASIC_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests the loading of multiple {@link Airport}
     * instances with no AIXM extensions.
     */
    @Test
    public void testMultipleLoad() {

        Airport[] validation = ValidationFeatures.BASIC_MULTIPLE_AIRPORTS;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validation, ValidationFeatures.BASIC_MULTIPLE_AIRPORT_INDICES);
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * directly from their parent features.
     */
    @Test
    public void testNestedLoad() {

        Airport[] validationAirports = ValidationFeatures.NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.NESTED_RUNWAY_DIRECTIONS;
        Feature[] validation = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validationAirports, ValidationFeatures.NESTED_AIRPORT_INDICES);

        for (Airport airport : airports) {

            Runway[] runways = airport.getRunways();
            AssertionUtils.assertFeaturesIfChild(runways, validationRunways, airport);

            RunwayEnd[] runwayEnds = airport.getRunwayEnds();
            AssertionUtils.assertFeaturesIfChild(runwayEnds, validationRunwayEnds, airport);

            for (RunwayEnd runwayEnd : runwayEnds) {

                RunwayDirection runwayDirection = runwayEnd.getRunwayDirection();
                AssertionUtils.assertFeaturesIfChild(new RunwayDirection[]{runwayDirection}, validationRunwayDirections, runwayEnd);
            }
        }
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * using the feature's {@code selectAll()} method.
     */
    @Test
    public void testNestedLoadSelections() {

        Airport[] validationAirports = ValidationFeatures.NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.NESTED_RUNWAY_DIRECTIONS;
        Feature[] validation = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        AssertionUtils.assertFeatures(airports, validationAirports,
                ValidationFeatures.NESTED_AIRPORT_INDICES);

        Runway[] runways = Runway.selectAll(null);
        AssertionUtils.assertFeatures(runways, validationRunways,
                ValidationFeatures.NESTED_RUNWAY_INDICES);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        AssertionUtils.assertFeatures(runwayEnds, validationRunwayEnds,
                ValidationFeatures.NESTED_RUNWAY_END_INDICES);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        AssertionUtils.assertFeatures(runwayDirections, validationRunwayDirections,
                ValidationFeatures.NESTED_RUNWAY_DIRECTION_INDICES);
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * using the feature's {@code select()} method.
     */
    @Test
    public void testNestedLoadSingleSelections() {

        Airport[] validationAirports = ValidationFeatures.NESTED_AIRPORTS;
        Runway[] validationRunways = ValidationFeatures.NESTED_RUNWAYS;
        RunwayEnd[] validationRunwayEnds = ValidationFeatures.NESTED_RUNWAY_ENDS;
        RunwayDirection[] validationRunwayDirections = ValidationFeatures.NESTED_RUNWAY_DIRECTIONS;
        Feature[] validation = FeatureUtils.merge(
                validationAirports,
                validationRunways,
                validationRunwayEnds,
                validationRunwayDirections
        );

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        Airport airport = Airport.select(null);
        AssertionUtils.assertFeatureOneOf(airport, validationAirports);

        Runway runway = Runway.select(null);
        AssertionUtils.assertFeatureOneOf(runway, validationRunways);

        RunwayEnd runwayEnd = RunwayEnd.select(null);
        AssertionUtils.assertFeatureOneOf(runwayEnd, validationRunwayEnds);

        RunwayDirection runwayDirection = RunwayDirection.select(null);
        AssertionUtils.assertFeatureOneOf(runwayDirection, validationRunwayDirections);
    }

    /**
     * This method tests the database preservation
     * feature when the current and database Jetway
     * versions match.
     */
    @Test
    public void testNoRebuildRequired() {

        // Force Jetway version for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test");

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        Jetway.reset();

        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);
        TestUtils.initializeJetway(source, false, Enforcement.IGNORE, false);

        // Check that the airport is still the same as before (no rebuild, so it wasn't overwritten)
        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        // Clear forced version property to avoid interference in other tests
        System.clearProperty("FORCE_JETWAY_VERSION");
    }

    /**
     * This method tests the database preservation
     * feature when the current and database Jetway
     * versions do not match.
     */
    @Test
    public void testRebuildRequired() {

        // Force Jetway version for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test");

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        Jetway.reset();

        // Force Jetway version mismatch for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test2");

        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);
        TestUtils.initializeJetway(source, false, Enforcement.IGNORE, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validationNew);

        // Clear forced version property to avoid interference in other tests
        System.clearProperty("FORCE_JETWAY_VERSION");
    }

    /**
     * This method tests the database preservation
     * feature when the current Jetway version is
     * null.
     */
    @Test
    public void testRebuildRequiredNullVersion() {

        // Force Jetway version for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test");

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        Jetway.reset();

        // Force Jetway version to null for this test
        System.clearProperty("FORCE_JETWAY_VERSION");
        System.setProperty("FORCE_NULL_JETWAY_VERSION", "true");

        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);
        TestUtils.initializeJetway(source, false, Enforcement.IGNORE, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validationNew);

        System.clearProperty("FORCE_NULL_JETWAY_VERSION");
    }

    /**
     * This method tests the database preservation
     * feature when the {@link Jetway#forceDatabaseRebuild()}
     * flag is set.
     */
    @Test
    public void testRebuildRequiredFlag() {

        // Force Jetway version for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test");

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        Jetway.reset();

        // Reload Jetway but set the force-rebuild flag
        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);
        TestUtils.initializeJetway(source, true, Enforcement.IGNORE, false);

        // Check that the airport is now the new airport, because a rebuild was forced
        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validationNew);

        // Clear forced version property to avoid interference in other tests
        System.clearProperty("FORCE_JETWAY_VERSION");
    }

    /**
     * This method tests that attempting to load
     * a feature from a null {@link java.io.InputStream InputStream}
     * throws an error.
     */
    @Test(expected = AIXMException.class)
    public void testNullInputStream() {

        AIXMSource source = new AIXMStreamSource();
        TestUtils.initializeJetway(source);
    }
}
