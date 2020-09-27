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
import com.github.chrisblutz.jetway.database.queries.Sort;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SortTests {

    private static Airport[] validationAirports;

    @BeforeClass
    public static void setupValidationAirports() {

        Airport airport1 = new Airport();
        airport1.id = "AH_0000001";
        airport1.name = "AIRPORT C";
        airport1.fieldElevation = 10d;
        airport1.latitude = 60d;
        airport1.longitude = -155d;
        airport1.servedCity = "FIRST CITY";
        airport1.iataDesignator = "TST";
        airport1.icao = "KTST";

        Airport airport2 = new Airport();
        airport2.id = "AH_0000002";
        airport2.name = "AIRPORT A";
        airport2.fieldElevation = 12d;
        airport2.latitude = 48d;
        airport2.longitude = -160d;
        airport2.servedCity = "SECOND CITY";
        airport2.iataDesignator = "TST";
        airport2.icao = "KTST";

        Airport airport3 = new Airport();
        airport3.id = "AH_0000003";
        airport3.name = "AIRPORT F";
        airport3.fieldElevation = 14d;
        airport3.latitude = 46d;
        airport3.longitude = -165d;
        airport3.servedCity = "FIRST CITY";
        airport3.iataDesignator = "TST";
        airport3.icao = "KTST";

        Airport airport4 = new Airport();
        airport4.id = "AH_0000004";
        airport4.name = "AIRPORT Z";
        airport4.fieldElevation = 16d;
        airport4.latitude = 52d;
        airport4.longitude = -170d;
        airport4.servedCity = "SECOND CITY";
        airport4.iataDesignator = "TST";
        airport4.icao = "KTST";

        Airport airport5 = new Airport();
        airport5.id = "AH_0000005";
        airport5.name = "AIRPORT H";
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

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway();

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, validationAirports, 0, 1, 2, 3, 4);
    }

    @Test
    public void testSortStringAll() {

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway();

        // Sorting by NAME ascending

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 1, 0, 2, 4, 3);

        // Sorting by NAME descending

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 3, 4, 2, 0, 1);
    }

    @Test
    public void testSortNumericAll() {

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway();

        // Sorting by LATITUDE ascending

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 2, 1, 4, 3, 0);

        // Sorting by LATITUDE descending

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(null, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 0, 3, 4, 1, 2);
    }

    @Test
    public void testSortStringWithQuery() {


        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereGreaterThan(Airport.class, Airport.FIELD_ELEVATION, 10);

        // Sorting by NAME ascending where FIELD_ELEVATION > 10

        Sort sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 1, 2, 4, 3);

        // Sorting by NAME descending where FIELD_ELEVATION > 10

        sort = Sort.by(Airport.class, Airport.NAME, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 3, 4, 2, 1);
    }

    @Test
    public void testSortNumericWithQuery() {

        AIXMFiles.registerCustomInputStream("APT_AIXM", LoadTests.class.getResourceAsStream("/aixm/sorting_basic.xml"));
        JetwayTesting.initializeJetway();

        Query query = Query.whereLessThanEquals(Airport.class, Airport.FIELD_ELEVATION, 16);

        // Sorting by LATITUDE ascending where FIELD_ELEVATION <= 16

        Sort sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.ASCENDING);
        Airport[] airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 2, 1, 3, 0);

        // Sorting by LATITUDE descending where FIELD_ELEVATION <= 16

        sort = Sort.by(Airport.class, Airport.LATITUDE, Sort.Order.DESCENDING);
        airports = Airport.selectAll(query, sort);
        JetwayAssertions.assertFeaturesOrdered(airports, validationAirports, 0, 3, 1, 2);
    }
}
