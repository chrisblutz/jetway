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
import com.github.chrisblutz.jetway.testing.utils.AirportAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTests {

    private static Airport[] validationAirports;

    @BeforeClass
    public static void setupValidationAirports() {

        Airport airport1 = new Airport();
        airport1.id = "AH_0000001";
        airport1.name = "FIRST AIRPORT INTL";
        airport1.fieldElevation = 10d;
        airport1.latitude = 42d;
        airport1.longitude = -155d;
        airport1.servedCity = "FIRST CITY";
        airport1.iataDesignator = "TST";
        airport1.icao = "KTST";

        Airport airport2 = new Airport();
        airport2.id = "AH_0000002";
        airport2.name = "SECOND AIRPORT";
        airport2.fieldElevation = 12d;
        airport2.latitude = 44d;
        airport2.longitude = -160d;
        airport2.servedCity = "SECOND CITY";
        airport2.iataDesignator = "TST";
        airport2.icao = "KTST";

        Airport airport3 = new Airport();
        airport3.id = "AH_0000003";
        airport3.name = "THIRD AIRPORT";
        airport3.fieldElevation = 14d;
        airport3.latitude = 46d;
        airport3.longitude = -165d;
        airport3.servedCity = "FIRST CITY";
        airport3.iataDesignator = "TST";
        airport3.icao = "KTST";

        Airport airport4 = new Airport();
        airport4.id = "AH_0000004";
        airport4.name = "FOURTH AIRPORT INTL";
        airport4.fieldElevation = 16d;
        airport4.latitude = 48d;
        airport4.longitude = -170d;
        airport4.servedCity = "SECOND CITY";
        airport4.iataDesignator = "TST";
        airport4.icao = "KTST";

        Airport airport5 = new Airport();
        airport5.id = "AH_0000005";
        airport5.name = "FIFTH AIRPORT";
        airport5.fieldElevation = 18d;
        airport5.latitude = 50d;
        airport5.longitude = -175d;
        airport5.servedCity = "THIRD CITY";
        airport5.iataDesignator = "TST";
        airport5.icao = "KTST";

        validationAirports = new Airport[]{airport1, airport2, airport3, airport4, airport5};
    }

    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    @Test
    public void testAll() {

        // Make sure assertions are correct by selecting all

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Airport[] airports = Airport.selectAll(null);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 1, 2, 3, 4);
    }

    @Test
    public void testEquals() {

        // Checking SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 2);
    }

    @Test
    public void testNotEquals() {

        // Checking SERVED_CITY != "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereNotEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 1, 3, 4);
    }

    @Test
    public void testGreaterThan() {

        // Checking FIELD_ELEVATION > 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 3, 4);
    }

    @Test
    public void testGreaterThanEquals() {

        // Checking FIELD_ELEVATION >= 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 2, 3, 4);
    }

    @Test
    public void testLessThan() {

        // Checking FIELD_ELEVATION < 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 1);
    }

    @Test
    public void testLessThanEquals() {

        // Checking FIELD_ELEVATION <= 14

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 14);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 1, 2);
    }

    @Test
    public void testLike() {

        // Checking NAME LIKE "%INTL"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereLike(Airport.class, Airport.NAME, "%INTL");
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 3);
    }

    @Test
    public void testAnd() {

        // Check FIELD_ELEVATION > 12 AND LATITUDE < 50

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);

        // Check "and" on first
        Query query = firstQuery.and(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 2, 3);

        // Check "and" on second (other way round)
        query = secondQuery.and(firstQuery);
        airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 2, 3);
    }

    @Test
    public void testOr() {

        // Check FIELD_ELEVATION < 12 OR LATITUDE > 46

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 46);

        // Check "or" on first
        Query query = firstQuery.or(secondQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 3, 4);

        // Check "or" on second (other way round)
        query = secondQuery.or(firstQuery);
        airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 3, 4);
    }

    @Test
    public void testAndAnd() {

        // Check (FIELD_ELEVATION >= 12 AND LATITUDE < 50) AND LONGITUDE > -170

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereGreaterThanEquals(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -170);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 1, 2);
    }

    @Test
    public void testAndOr() {

        // Check (FIELD_ELEVATION > 12 AND LATITUDE < 50) OR LONGITUDE > -160

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereLessThan(Airport.class, Airport.LATITUDE, 50);
        Query thirdQuery = Query.whereGreaterThan(Airport.class, Airport.LONGITUDE, -160);

        Query andQuery = firstQuery.and(secondQuery);
        Query query = andQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 2, 3);
    }

    @Test
    public void testOrAnd() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 44) AND SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 44);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.and(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 2);
    }

    @Test
    public void testOrOr() {

        // Check (FIELD_ELEVATION < 12 OR LATITUDE > 48) OR SERVED_CITY = "FIRST CITY"

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/database_basic.xml"));
        JetwayTesting.initializeJetway();

        Query firstQuery = Query.whereLessThan(Airport.class, Airport.FIELD_ELEVATION, 12);
        Query secondQuery = Query.whereGreaterThan(Airport.class, Airport.LATITUDE, 48);
        Query thirdQuery = Query.whereEquals(Airport.class, Airport.SERVED_CITY, "FIRST CITY");

        Query orQuery = firstQuery.or(secondQuery);
        Query query = orQuery.or(thirdQuery);
        Airport[] airports = Airport.selectAll(query);
        AirportAssertions.assertAirports(airports, validationAirports, 0, 2, 4);
    }
}
