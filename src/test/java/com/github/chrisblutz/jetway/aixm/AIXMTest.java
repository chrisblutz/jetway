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
package com.github.chrisblutz.jetway.aixm;

import com.github.chrisblutz.jetway.Jetway;
import com.github.chrisblutz.jetway.aixm.crawling.AIXMData;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.aixm.utils.TestObject;
import com.github.chrisblutz.jetway.features.Airport;
import org.junit.Test;

import static org.junit.Assert.*;

public class AIXMTest {

    @Test
    public void testBasicLoad() {

        Jetway.reset();
        AIXMFiles.registerCustomInputStream("APT_AIXM", AIXMTest.class.getResourceAsStream("/aixm/basic.xml"));
        intializeJetway("/");

        Airport[] airports = Airport.selectAll(null);
        assertNotNull(airports);
        assertEquals(1, airports.length);

        Airport airport = airports[0];
        assertEquals("AH_0000001", airport.id);
        assertEquals("TEST AIRPORT", airport.name);
        assertEquals("TST", airport.iataDesignator);
        assertEquals("KTST", airport.icao);
        assertNull(airport.siteNumber);
        assertEquals(19.5, airport.fieldElevation, 0);
        assertNull(airport.landArea);
        assertEquals(50.5678, airport.latitude, 0);
        assertEquals(-170.1234, airport.longitude, 0);
        assertNull(airport.county);
        assertNull(airport.state);
        assertEquals("TEST CITY", airport.servedCity);
        assertNull(airport.numberOfSingleEngineAircraft);
        assertNull(airport.numberOfMultiEngineAircraft);
        assertNull(airport.numberOfJetEngineAircraft);
        assertNull(airport.numberOfHelicopters);
        assertNull(airport.numberOfGliders);
        assertNull(airport.numberOfMilitaryAircraft);
        assertNull(airport.numberOfUltralightAircraft);
    }

    @Test
    public void testBasicExtensionLoad() {

        Jetway.reset();
        AIXMFiles.registerCustomInputStream("APT_AIXM", AIXMTest.class.getResourceAsStream("/aixm/basic_extension.xml"));
        intializeJetway("/");

        Airport[] airports = Airport.selectAll(null);
        assertNotNull(airports);
        assertEquals(1, airports.length);

        Airport airport = airports[0];
        assertEquals("AH_0000001", airport.id);
        assertEquals("TEST AIRPORT", airport.name);
        assertEquals("TST", airport.iataDesignator);
        assertEquals("KTST", airport.icao);
        assertEquals("50000.*A", airport.siteNumber);
        assertEquals(19.5, airport.fieldElevation, 0);
        assertEquals(234, airport.landArea, 0);
        assertEquals(50.5678, airport.latitude, 0);
        assertEquals(-170.1234, airport.longitude, 0);
        assertEquals("TEST COUNTY", airport.county);
        assertEquals("TEST STATE", airport.state);
        assertEquals("TEST CITY", airport.servedCity);
        assertEquals(1, (int) airport.numberOfSingleEngineAircraft);
        assertEquals(2, (int) airport.numberOfMultiEngineAircraft);
        assertEquals(3, (int) airport.numberOfJetEngineAircraft);
        assertEquals(4, (int) airport.numberOfHelicopters);
        assertEquals(5, (int) airport.numberOfGliders);
        assertEquals(6, (int) airport.numberOfMilitaryAircraft);
        assertEquals(7, (int) airport.numberOfUltralightAircraft);
    }

    @Test
    public void testMultipleLoad() {

        Jetway.reset();
        AIXMFiles.registerCustomInputStream("APT_AIXM", AIXMTest.class.getResourceAsStream("/aixm/basic_multiple.xml"));
        intializeJetway("/");

        Airport[] airports = Airport.selectAll(null);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        Airport airport1 = airports[0];
        assertEquals("AH_0000001", airport1.id);
        assertEquals("TEST AIRPORT 1", airport1.name);
        assertEquals("TST1", airport1.iataDesignator);
        assertEquals("KTS1", airport1.icao);
        assertNull(airport1.siteNumber);
        assertEquals(19.5, airport1.fieldElevation, 0);
        assertNull(airport1.landArea);
        assertEquals(50.5678, airport1.latitude, 0);
        assertEquals(-170.1234, airport1.longitude, 0);
        assertNull(airport1.county);
        assertNull(airport1.state);
        assertEquals("TEST CITY 1", airport1.servedCity);
        assertNull(airport1.numberOfSingleEngineAircraft);
        assertNull(airport1.numberOfMultiEngineAircraft);
        assertNull(airport1.numberOfJetEngineAircraft);
        assertNull(airport1.numberOfHelicopters);
        assertNull(airport1.numberOfGliders);
        assertNull(airport1.numberOfMilitaryAircraft);
        assertNull(airport1.numberOfUltralightAircraft);

        Airport airport2 = airports[1];
        assertEquals("AH_0000002", airport2.id);
        assertEquals("TEST AIRPORT 2", airport2.name);
        assertEquals("TST2", airport2.iataDesignator);
        assertEquals("KTS2", airport2.icao);
        assertNull(airport2.siteNumber);
        assertEquals(10.0, airport2.fieldElevation, 0);
        assertNull(airport2.landArea);
        assertEquals(-20.8765, airport2.latitude, 0);
        assertEquals(120.4321, airport2.longitude, 0);
        assertNull(airport2.county);
        assertNull(airport2.state);
        assertEquals("TEST CITY 2", airport2.servedCity);
        assertNull(airport2.numberOfSingleEngineAircraft);
        assertNull(airport2.numberOfMultiEngineAircraft);
        assertNull(airport2.numberOfJetEngineAircraft);
        assertNull(airport2.numberOfHelicopters);
        assertNull(airport2.numberOfGliders);
        assertNull(airport2.numberOfMilitaryAircraft);
        assertNull(airport2.numberOfUltralightAircraft);

        Airport airport = Airport.select(null);
        assertNotNull(airports);

        assertEquals("AH_0000001", airport.id);
        assertEquals("TEST AIRPORT 1", airport.name);
        assertEquals("TST1", airport.iataDesignator);
        assertEquals("KTS1", airport.icao);
        assertNull(airport.siteNumber);
        assertEquals(19.5, airport.fieldElevation, 0);
        assertNull(airport.landArea);
        assertEquals(50.5678, airport.latitude, 0);
        assertEquals(-170.1234, airport.longitude, 0);
        assertNull(airport.county);
        assertNull(airport.state);
        assertEquals("TEST CITY 1", airport.servedCity);
        assertNull(airport.numberOfSingleEngineAircraft);
        assertNull(airport.numberOfMultiEngineAircraft);
        assertNull(airport.numberOfJetEngineAircraft);
        assertNull(airport.numberOfHelicopters);
        assertNull(airport.numberOfGliders);
        assertNull(airport.numberOfMilitaryAircraft);
        assertNull(airport.numberOfUltralightAircraft);
    }

    @Test
    public void testZIPLoad() {

        Jetway.reset();
        intializeJetway("test/nasr.zip");

        Airport[] airports = Airport.selectAll(null);
        assertNotNull(airports);
        assertEquals(1, airports.length);

        Airport airport = airports[0];
        assertEquals("AH_0000001", airport.id);
        assertEquals("TEST AIRPORT", airport.name);
        assertEquals("TST", airport.iataDesignator);
        assertEquals("KTST", airport.icao);
        assertNull(airport.siteNumber);
        assertEquals(19.5, airport.fieldElevation, 0);
        assertNull(airport.landArea);
        assertEquals(50.5678, airport.latitude, 0);
        assertEquals(-170.1234, airport.longitude, 0);
        assertNull(airport.county);
        assertNull(airport.state);
        assertEquals("TEST CITY", airport.servedCity);
        assertNull(airport.numberOfSingleEngineAircraft);
        assertNull(airport.numberOfMultiEngineAircraft);
        assertNull(airport.numberOfJetEngineAircraft);
        assertNull(airport.numberOfHelicopters);
        assertNull(airport.numberOfGliders);
        assertNull(airport.numberOfMilitaryAircraft);
        assertNull(airport.numberOfUltralightAircraft);
    }

    @Test
    public void testDataCrawl() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);

        assertEquals(true, data.crawl("Boolean").get(Boolean.class));
        assertEquals((byte) 5, data.crawl("Byte").get(Byte.class));
        assertEquals('_', data.crawl("Character").get(Character.class));
        assertEquals(1.234d, data.crawl("Double").get(Double.class));
        assertEquals(5.678f, data.crawl("Float").get(Float.class));
        assertEquals(1234567, data.crawl("Integer").get(Integer.class));
        assertEquals(123456789L, data.crawl("Long").get(Long.class));
        assertEquals("Value", data.crawl("String").get(String.class));
        assertEquals((short) 12345, data.crawl("Short").get(Short.class));
    }

    @Test(expected = AIXMException.class)
    public void testInvalidDataPath() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);
        data.crawl("InvalidPath");
    }

    private void intializeJetway(String aixmPath) {

        String user = System.getenv("TEST_USER");
        String password = System.getenv("TEST_PASSWORD");
        String server = System.getenv("TEST_SERVER");
        Jetway.initialize(("mysql -a " + aixmPath +
                (user != null && !user.isEmpty() ? " -u " + user : "") +
                (password != null && !password.isEmpty() ? " -p " + password : "") +
                (server != null && !server.isEmpty() ? " -s " + server : "")
                + " --drop").split(" "));
    }
}
