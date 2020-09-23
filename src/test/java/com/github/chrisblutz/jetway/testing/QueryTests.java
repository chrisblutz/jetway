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
import com.github.chrisblutz.jetway.aixm.AIXMFiles;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryTests {

    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    @Test
    public void testAll() {

        // Make sure assertions are correct by selecting all

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Airport[] airports = Airport.selectAll(null);
        assertNotNull(airports);
        assertEquals(5, airports.length);

        assertAirports(airports, 0, 1, 2, 3, 4);
    }

    @Test
    public void testEquals() {

        // Checking SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 0, 2);
    }

    @Test
    public void testNotEquals() {

        // Checking SERVED_CITY != "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereNotEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 1, 3, 4);
    }

    @Test
    public void testGreaterThan() {

        // Checking FIELD_ELEVATION > 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 3, 4);
    }

    @Test
    public void testGreaterThanEquals() {

        // Checking FIELD_ELEVATION >= 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 2, 3, 4);
    }

    @Test
    public void testLessThan() {

        // Checking FIELD_ELEVATION < 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 0, 1);
    }

    @Test
    public void testLessThanEquals() {

        // Checking FIELD_ELEVATION <= 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 0, 1, 2);
    }

    @Test
    public void testLike() {

        // Checking NAME LIKE "%INTL"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query query = Query.whereLike(Airport.class, Airport.NAME, "%INTL");
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 0, 3);
    }

    @Test
    public void testAnd() {

        // Check FIELD_ELEVATION > 12 AND LATITUDE < 50

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);

        // Check "and" on first
        Query query = firstQuery.and(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 2, 3);

        // Check "and" on second (other way round)
        query = secondQuery.and(firstQuery);
        airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 2, 3);
    }

    @Test
    public void testOr() {

        // Check FIELD_ELEVATION < 12 OR LATITUDE > 46

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 46);

        // Check "or" on first
        Query query = firstQuery.or(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 0, 3, 4);

        // Check "or" on second (other way round)
        query = secondQuery.or(firstQuery);
        airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 0, 3, 4);
    }

    @Test
    public void testAndAnd() {

        // Check (FIELD_ELEVATION >= 12 AND LATITUDE < 50) AND LONGITUDE > -170

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -170);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 1, 2);
    }

    @Test
    public void testAndOr() {

        // Check (FIELD_ELEVATION > 12 AND LATITUDE < 50) OR LONGITUDE > -160

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -160);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 0, 2, 3);
    }

    @Test
    public void testOrAnd() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 44) AND SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 44);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(2, airports.length);

        assertAirports(airports, 0, 2);
    }

    @Test
    public void testOrOr() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 48) OR SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 48);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        assertNotNull(airports);
        assertEquals(3, airports.length);

        assertAirports(airports, 0, 2, 4);
    }

    private void assertAirports(Airport[] airports, int... airportIndices) {

        // Go through every airport to compare with expected airports
        boolean[] found = new boolean[airportIndices.length];
        for (Airport airport : airports) {

            for (int i = 0; i < airportIndices.length; i++) {

                if (found[i])
                    continue;

                if (checkAirport(airport, airportIndices[i])) {
                    found[i] = true;
                    break;
                }
            }
        }

        // Check that all expected airports were found
        for (boolean airportFound : found)
            assertTrue(airportFound);
    }

    private boolean checkAirport(Airport airport, int airportIndex) {

        // Check that the airport we're checking corresponds with the specified index.
        // Assertions should be used outside of this method to check that
        // all necessary indices were found
        if (airportIndex == 0 && !airport.name.equals("FIRST AIRPORT INTL"))
            return false;
        if (airportIndex == 1 && !airport.name.equals("SECOND AIRPORT"))
            return false;
        if (airportIndex == 2 && !airport.name.equals("THIRD AIRPORT"))
            return false;
        if (airportIndex == 3 && !airport.name.equals("FOURTH AIRPORT INTL"))
            return false;
        if (airportIndex == 4 && !airport.name.equals("FIFTH AIRPORT"))
            return false;

        // Check airport-specific elements
        switch (airportIndex) {

            case 0:

                assertEquals("AH_0000001", airport.id);
                assertEquals("FIRST AIRPORT INTL", airport.name);
                assertEquals(10, airport.fieldElevation, 0);
                assertEquals(42, airport.latitude, 0);
                assertEquals(-155, airport.longitude, 0);
                assertEquals("FIRST CITY", airport.servedCity);
                break;

            case 1:

                assertEquals("AH_0000002", airport.id);
                assertEquals("SECOND AIRPORT", airport.name);
                assertEquals(12, airport.fieldElevation, 0);
                assertEquals(44, airport.latitude, 0);
                assertEquals(-160, airport.longitude, 0);
                assertEquals("SECOND CITY", airport.servedCity);
                break;

            case 2:

                assertEquals("AH_0000003", airport.id);
                assertEquals("THIRD AIRPORT", airport.name);
                assertEquals(14, airport.fieldElevation, 0);
                assertEquals(46, airport.latitude, 0);
                assertEquals(-165, airport.longitude, 0);
                assertEquals("FIRST CITY", airport.servedCity);
                break;

            case 3:

                assertEquals("AH_0000004", airport.id);
                assertEquals("FOURTH AIRPORT INTL", airport.name);
                assertEquals(16, airport.fieldElevation, 0);
                assertEquals(48, airport.latitude, 0);
                assertEquals(-170, airport.longitude, 0);
                assertEquals("SECOND CITY", airport.servedCity);
                break;

            case 4:

                assertEquals("AH_0000005", airport.id);
                assertEquals("FIFTH AIRPORT", airport.name);
                assertEquals(18, airport.fieldElevation, 0);
                assertEquals(50, airport.latitude, 0);
                assertEquals(-175, airport.longitude, 0);
                assertEquals("THIRD CITY", airport.servedCity);
                break;
        }

        // Assert all common elements
        assertEquals("TST", airport.iataDesignator);
        assertEquals("KTST", airport.icao);
        assertNull(airport.siteNumber);
        assertNull(airport.landArea);
        assertNull(airport.county);
        assertNull(airport.state);
        assertNull(airport.numberOfSingleEngineAircraft);
        assertNull(airport.numberOfMultiEngineAircraft);
        assertNull(airport.numberOfJetEngineAircraft);
        assertNull(airport.numberOfHelicopters);
        assertNull(airport.numberOfGliders);
        assertNull(airport.numberOfMilitaryAircraft);
        assertNull(airport.numberOfUltralightAircraft);

        return true;
    }

    private void initializeJetway() {

        String user = System.getenv("TEST_USER");
        String password = System.getenv("TEST_PASSWORD");
        String server = System.getenv("TEST_SERVER");
        Jetway.initialize(("mysql -a /" +
                (user != null && !user.isEmpty() ? " -u " + user : "") +
                (password != null && !password.isEmpty() ? " -p " + password : "") +
                (server != null && !server.isEmpty() ? " -s " + server : "")
                + " --drop").split(" "));
    }
}
