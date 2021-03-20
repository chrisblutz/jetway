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
import com.github.chrisblutz.jetway.aixm.source.AIXMFileSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMStreamSource;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.features.RunwayDirection;
import com.github.chrisblutz.jetway.features.RunwayEnd;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import com.github.chrisblutz.jetway.testing.utils.ValidationArrays;
import org.junit.Before;
import org.junit.Test;

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

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/basic.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with an AIXM extension.
     */
    @Test
    public void testBasicExtensionLoad() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/basic_extension.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests the loading of multiple {@link Airport}
     * instances with no AIXM extensions.
     */
    @Test
    public void testMultipleLoad() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/basic_multiple.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_MULTIPLE_AIRPORTS, 0, 1);
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * directly from their parent features.
     */
    @Test
    public void testNestedLoad() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);

        Runway[] runways = airports[0].getRunways();
        JetwayAssertions.assertFeaturesIfChild(runways, ValidationArrays.LOAD_NO_EXTENSION_RUNWAYS, airports[0]);

        RunwayEnd[] runwayEnds = airports[0].getRunwayEnds();
        JetwayAssertions.assertFeaturesIfChild(runwayEnds, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_ENDS, runways[0]);

        RunwayDirection[] runwayDirections = runwayEnds[0].getRunwayDirections();
        JetwayAssertions.assertFeaturesIfChild(runwayDirections, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_DIRECTIONS, runwayEnds[0]);
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * using the feature's {@code selectAll()} method.
     */
    @Test
    public void testNestedLoadSelections() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);

        Runway[] runways = Runway.selectAll(null);
        JetwayAssertions.assertFeatures(runways, ValidationArrays.LOAD_NO_EXTENSION_RUNWAYS, 0);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        JetwayAssertions.assertFeatures(runwayEnds, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_ENDS, 0, 1);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        JetwayAssertions.assertFeatures(runwayDirections, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_DIRECTIONS, 0, 1);
    }

    /**
     * This method tests the loading of nested features
     * with no AIXM extensions, retrieving child features
     * using the feature's {@code select()} method.
     */
    @Test
    public void testNestedLoadSingleSelections() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/nested.xml"));
        JetwayTesting.initializeJetway(source);

        Airport airport = Airport.select(null);
        JetwayAssertions.assertFeatureOneOf(airport, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS);

        Runway runway = Runway.select(null);
        JetwayAssertions.assertFeatureOneOf(runway, ValidationArrays.LOAD_NO_EXTENSION_RUNWAYS);

        RunwayEnd runwayEnd = RunwayEnd.select(null);
        JetwayAssertions.assertFeatureOneOf(runwayEnd, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_ENDS);

        RunwayDirection runwayDirection = RunwayDirection.select(null);
        JetwayAssertions.assertFeatureOneOf(runwayDirection, ValidationArrays.LOAD_NO_EXTENSION_RUNWAY_DIRECTIONS);
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with no AIXM extension from an AIXM ZIP file.
     */
    @Test
    public void testZIPLoad() {

        AIXMSource source = new AIXMFileSource(LoadTests.class.getResourceAsStream("/aixm/aixm.zip"));
        JetwayTesting.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
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

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_initial.xml"));
        JetwayTesting.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_INITIAL_AIRPORTS, 0);

        Jetway.reset();

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is still the same as before (no rebuild, so it wasn't overwritten)
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_INITIAL_AIRPORTS, 0);

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

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_initial.xml"));
        JetwayTesting.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_INITIAL_AIRPORTS, 0);

        Jetway.reset();

        // Force Jetway version mismatch for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test2");

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_FINAL_AIRPORTS, 0);

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

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_initial.xml"));
        JetwayTesting.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_INITIAL_AIRPORTS, 0);

        Jetway.reset();

        // Force Jetway version to null for this test
        System.clearProperty("FORCE_JETWAY_VERSION");
        System.setProperty("FORCE_NULL_JETWAY_VERSION", "true");

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_FINAL_AIRPORTS, 0);

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

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_initial.xml"));
        JetwayTesting.initializeJetway(source);

        // Check that airport is correct
        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_INITIAL_AIRPORTS, 0);

        Jetway.reset();

        // Reload Jetway but set the force-rebuild flag
        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, true);

        // Check that the airport is now the new airport, because a rebuild was forced
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_REBUILD_FINAL_AIRPORTS, 0);

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
        JetwayTesting.initializeJetway(source);
    }

    /**
     * This method tests that attempting to load
     * a feature from a malformed XML file
     * throws an error.
     */
    @Test(expected = AIXMException.class)
    public void testMalformedZIP() {

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/invalid.xml"));
        JetwayTesting.initializeJetway(source);
    }
}
