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
import com.github.chrisblutz.jetway.aixm.source.AIXMFileSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.features.RunwayDirection;
import com.github.chrisblutz.jetway.features.RunwayEnd;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class handles testing of basic AIXM feature-loading
 * functionality.
 *
 * @author Christopher Lutz
 */
public class LoadTests {

    private static Airport[] validationAirportsRebuildInitial, validationAirportsRebuildFinal;
    private static Airport[] validationAirportsNoExtension, validationAirportsExtension;
    private static Airport[] validationAirportsMultiple;
    private static Runway[] validationRunwaysNoExtension;
    private static RunwayEnd[] validationRunwayEndsNoExtension;
    private static RunwayDirection[] validationRunwayDirectionsNoExtension;

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsNoExtension, 0);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsExtension, 0);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsMultiple, 0, 1);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsNoExtension, 0);

        Runway[] runways = airports[0].getRunways();
        JetwayAssertions.assertFeaturesIfChild(runways, validationRunwaysNoExtension, airports[0]);

        RunwayEnd[] runwayEnds = runways[0].getRunwayEnds();
        JetwayAssertions.assertFeaturesIfChild(runwayEnds, validationRunwayEndsNoExtension, runways[0]);

        RunwayDirection[] runwayDirections = runwayEnds[0].getRunwayDirections();
        JetwayAssertions.assertFeaturesIfChild(runwayDirections, validationRunwayDirectionsNoExtension, runwayEnds[0]);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsNoExtension, 0);

        Runway[] runways = Runway.selectAll(null);
        JetwayAssertions.assertFeatures(runways, validationRunwaysNoExtension, 0);

        RunwayEnd[] runwayEnds = RunwayEnd.selectAll(null);
        JetwayAssertions.assertFeatures(runwayEnds, validationRunwayEndsNoExtension, 0, 1);

        RunwayDirection[] runwayDirections = RunwayDirection.selectAll(null);
        JetwayAssertions.assertFeatures(runwayDirections, validationRunwayDirectionsNoExtension, 0, 1);
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
        JetwayAssertions.assertFeatureOneOf(airport, validationAirportsNoExtension);

        Runway runway = Runway.select(null);
        JetwayAssertions.assertFeatureOneOf(runway, validationRunwaysNoExtension);

        RunwayEnd runwayEnd = RunwayEnd.select(null);
        JetwayAssertions.assertFeatureOneOf(runwayEnd, validationRunwayEndsNoExtension);

        RunwayDirection runwayDirection = RunwayDirection.select(null);
        JetwayAssertions.assertFeatureOneOf(runwayDirection, validationRunwayDirectionsNoExtension);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsNoExtension, 0);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildInitial, 0);

        Jetway.reset();

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is still the same as before (no rebuild, so it wasn't overwritten)
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildInitial, 0);

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
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildInitial, 0);

        Jetway.reset();

        // Force Jetway version mismatch for this test
        System.setProperty("FORCE_JETWAY_VERSION", "test2");

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildFinal, 0);

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
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildInitial, 0);

        Jetway.reset();

        // Force Jetway version to null for this test
        System.clearProperty("FORCE_JETWAY_VERSION");

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, false);

        // Check that the airport is now the new airport, because a rebuild was required due to version mismatch
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildFinal, 0);
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
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildInitial, 0);

        Jetway.reset();

        // Reload Jetway but set the force-rebuild flag
        source = JetwayTesting.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/rebuild_final.xml"));
        JetwayTesting.initializeJetway(source, true);

        // Check that the airport is now the new airport, because a rebuild was forced
        airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, validationAirportsRebuildFinal, 0);

        // Clear forced version property to avoid interference in other tests
        System.clearProperty("FORCE_JETWAY_VERSION");
    }

    /**
     * This method sets up the features
     * needed for validation during assertions
     * in {@link JetwayAssertions} methods.
     */
    @BeforeClass
    public static void setupValidationFeatures() {

        Airport airportRebuildInitial = new Airport();
        airportRebuildInitial.id = "AH_0000001";
        airportRebuildInitial.name = "AIRPORT A";
        airportRebuildInitial.fieldElevation = 20d;
        airportRebuildInitial.latitude = 60d;
        airportRebuildInitial.longitude = -170d;
        airportRebuildInitial.servedCity = "TEST CITY";
        airportRebuildInitial.iataDesignator = "TST";
        airportRebuildInitial.icao = "KTST";
        validationAirportsRebuildInitial = new Airport[]{airportRebuildInitial};

        Airport airportRebuildFinal = new Airport();
        airportRebuildFinal.id = "AH_0000001";
        airportRebuildFinal.name = "AIRPORT B";
        airportRebuildFinal.fieldElevation = 10d;
        airportRebuildFinal.latitude = 50d;
        airportRebuildFinal.longitude = -160d;
        airportRebuildFinal.servedCity = "TEST CITY";
        airportRebuildFinal.iataDesignator = "TST";
        airportRebuildFinal.icao = "KTST";
        validationAirportsRebuildFinal = new Airport[]{airportRebuildFinal};

        Airport airportBasicNoExtension = new Airport();
        airportBasicNoExtension.id = "AH_0000001";
        airportBasicNoExtension.name = "TEST AIRPORT";
        airportBasicNoExtension.fieldElevation = 19.5;
        airportBasicNoExtension.latitude = 50.5678;
        airportBasicNoExtension.longitude = -170.1234;
        airportBasicNoExtension.servedCity = "TEST CITY";
        airportBasicNoExtension.iataDesignator = "TST";
        airportBasicNoExtension.icao = "KTST";
        validationAirportsNoExtension = new Airport[]{airportBasicNoExtension};

        Airport airportBasicExtension = new Airport();
        airportBasicExtension.id = "AH_0000001";
        airportBasicExtension.name = "TEST AIRPORT";
        airportBasicExtension.fieldElevation = 19.5;
        airportBasicExtension.latitude = 50.5678;
        airportBasicExtension.longitude = -170.1234;
        airportBasicExtension.servedCity = "TEST CITY";
        airportBasicExtension.iataDesignator = "TST";
        airportBasicExtension.icao = "KTST";
        airportBasicExtension.landArea = 234d;
        airportBasicExtension.siteNumber = "50000.*A";
        airportBasicExtension.county = "TEST COUNTY";
        airportBasicExtension.state = "TEST STATE";
        airportBasicExtension.numberOfSingleEngineAircraft = 1;
        airportBasicExtension.numberOfMultiEngineAircraft = 2;
        airportBasicExtension.numberOfJetEngineAircraft = 3;
        airportBasicExtension.numberOfHelicopters = 4;
        airportBasicExtension.numberOfGliders = 5;
        airportBasicExtension.numberOfMilitaryAircraft = 6;
        airportBasicExtension.numberOfUltralightAircraft = 7;
        validationAirportsExtension = new Airport[]{airportBasicExtension};

        Airport airportMultiple1 = new Airport();
        airportMultiple1.id = "AH_0000001";
        airportMultiple1.name = "TEST AIRPORT 1";
        airportMultiple1.fieldElevation = 19.5;
        airportMultiple1.latitude = 50.5678;
        airportMultiple1.longitude = -170.1234;
        airportMultiple1.servedCity = "TEST CITY 1";
        airportMultiple1.iataDesignator = "TST1";
        airportMultiple1.icao = "KTS1";

        Airport airportMultiple2 = new Airport();
        airportMultiple2.id = "AH_0000002";
        airportMultiple2.name = "TEST AIRPORT 2";
        airportMultiple2.fieldElevation = 10d;
        airportMultiple2.latitude = -20.8765;
        airportMultiple2.longitude = 120.4321;
        airportMultiple2.servedCity = "TEST CITY 2";
        airportMultiple2.iataDesignator = "TST2";
        airportMultiple2.icao = "KTS2";
        validationAirportsMultiple = new Airport[]{airportMultiple1, airportMultiple2};

        Runway runwayNoExtension = new Runway();
        runwayNoExtension.id = "RWY_0000001_1";
        runwayNoExtension.airportId = airportBasicNoExtension.id;
        runwayNoExtension.designator = "05/23";
        runwayNoExtension.length = 8525d;
        runwayNoExtension.width = 200d;
        validationRunwaysNoExtension = new Runway[]{runwayNoExtension};

        RunwayEnd runwayEndNoExtensionBase = new RunwayEnd();
        runwayEndNoExtensionBase.id = "RWY_BASE_END_0000001_1";
        runwayEndNoExtensionBase.runwayId = runwayNoExtension.id;
        runwayEndNoExtensionBase.designator = "05";

        RunwayEnd runwayEndNoExtensionReciprocal = new RunwayEnd();
        runwayEndNoExtensionReciprocal.id = "RWY_RECIPROCAL_END_0000001_1";
        runwayEndNoExtensionReciprocal.runwayId = runwayNoExtension.id;
        runwayEndNoExtensionReciprocal.designator = "23";
        validationRunwayEndsNoExtension = new RunwayEnd[]{runwayEndNoExtensionBase, runwayEndNoExtensionReciprocal};

        RunwayDirection runwayDirectionBaseNoExtension = new RunwayDirection();
        runwayDirectionBaseNoExtension.id = "RWY_DIRECTION_BASE_END_0000001_1";
        runwayDirectionBaseNoExtension.runwayEndId = runwayEndNoExtensionBase.id;
        runwayDirectionBaseNoExtension.latitude = 50.5679;
        runwayDirectionBaseNoExtension.longitude = -170.1233;

        RunwayDirection runwayDirectionReciprocalNoExtension = new RunwayDirection();
        runwayDirectionReciprocalNoExtension.id = "RWY_DIRECTION_RECIPROCAL_END_0000001_1";
        runwayDirectionReciprocalNoExtension.runwayEndId = runwayEndNoExtensionReciprocal.id;
        runwayDirectionReciprocalNoExtension.latitude = 50.9765;
        runwayDirectionReciprocalNoExtension.longitude = -170.3211;
        validationRunwayDirectionsNoExtension = new RunwayDirection[]{runwayDirectionBaseNoExtension, runwayDirectionReciprocalNoExtension};
    }
}
