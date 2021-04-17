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
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.fields.Ownership;
import com.github.chrisblutz.jetway.testing.utils.AIXMUtils;
import com.github.chrisblutz.jetway.testing.utils.AssertionUtils;
import com.github.chrisblutz.jetway.testing.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class handles testing of basic AIXM feature-loading
 * functionality from different kinds of files.
 *
 * @author Christopher Lutz
 */
public class FileLoadTests {

    /**
     * Airport with no extensions, based on the information in
     * {@code basic.xml} and {@code aixm.zip}.
     */
    public static final Airport NO_EXTENSION_AIRPORT;
    /**
     * Airport with extension data, based on the information in
     * {@code basic_extension.xml}.
     */
    public static final Airport EXTENSION_AIRPORT;

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

        AIXMSource source = AIXMUtils.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/basic.xml"));
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], NO_EXTENSION_AIRPORT);
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with an AIXM extension.
     */
    @Test
    public void testBasicExtensionLoad() {

        AIXMSource source = AIXMUtils.constructSource(Airport.AIXM_FILE, LoadTests.class.getResourceAsStream("/aixm/basic_extension.xml"));
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], EXTENSION_AIRPORT);
    }

    /**
     * This method tests the loading of a basic {@link Airport}
     * with no AIXM extension from an AIXM ZIP file.
     */
    @Test
    public void testZIPLoad() {

        AIXMSource source = new AIXMFileSource(FileLoadTests.class.getResourceAsStream("/aixm/aixm.zip"));
        TestUtils.initializeJetway(source);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], NO_EXTENSION_AIRPORT);
    }

    /**
     * This method tests that attempting to load
     * a feature from a malformed XML file
     * throws an error.
     */
    @Test(expected = AIXMException.class)
    public void testMalformedXML() {

        AIXMSource source = AIXMUtils.constructSource(Airport.AIXM_FILE, FileLoadTests.class.getResourceAsStream("/aixm/invalid.xml"));
        TestUtils.initializeJetway(source);
    }

    static {

        NO_EXTENSION_AIRPORT = new Airport();
        NO_EXTENSION_AIRPORT.id = "AH_0000001";
        NO_EXTENSION_AIRPORT.name = "TEST AIRPORT";
        NO_EXTENSION_AIRPORT.fieldElevation = 19.5;
        NO_EXTENSION_AIRPORT.latitude = 50.5678;
        NO_EXTENSION_AIRPORT.longitude = -170.1234;
        NO_EXTENSION_AIRPORT.servedCity = "TEST CITY";
        NO_EXTENSION_AIRPORT.privateUseOnly = false;
        NO_EXTENSION_AIRPORT.iataDesignator = "TST";
        NO_EXTENSION_AIRPORT.icao = "KTST";

        EXTENSION_AIRPORT = new Airport();
        EXTENSION_AIRPORT.id = "AH_0000001";
        EXTENSION_AIRPORT.name = "TEST AIRPORT";
        EXTENSION_AIRPORT.fieldElevation = 19.5;
        EXTENSION_AIRPORT.latitude = 50.5678;
        EXTENSION_AIRPORT.longitude = -170.1234;
        EXTENSION_AIRPORT.servedCity = "TEST CITY";
        EXTENSION_AIRPORT.iataDesignator = "TST";
        EXTENSION_AIRPORT.icao = "KTST";
        EXTENSION_AIRPORT.landArea = 234d;
        EXTENSION_AIRPORT.siteNumber = "50000.*A";
        EXTENSION_AIRPORT.county = "TEST COUNTY";
        EXTENSION_AIRPORT.state = "TEST STATE";
        EXTENSION_AIRPORT.privateUseOnly = true;
        EXTENSION_AIRPORT.ownership = Ownership.PUBLIC;
        EXTENSION_AIRPORT.numberOfSingleEngineAircraft = 1;
        EXTENSION_AIRPORT.numberOfMultiEngineAircraft = 2;
        EXTENSION_AIRPORT.numberOfJetEngineAircraft = 3;
        EXTENSION_AIRPORT.numberOfHelicopters = 4;
        EXTENSION_AIRPORT.numberOfGliders = 5;
        EXTENSION_AIRPORT.numberOfMilitaryAircraft = 6;
        EXTENSION_AIRPORT.numberOfUltralightAircraft = 7;
    }
}
