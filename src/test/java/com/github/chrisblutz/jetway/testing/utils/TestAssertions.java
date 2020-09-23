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
package com.github.chrisblutz.jetway.testing.utils;

import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.features.RunwayDirection;
import com.github.chrisblutz.jetway.features.RunwayEnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestAssertions {

    public static void basicAirportNoExtension(Airport airport) {

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

    public static void basicRunway(Runway runway, String airportId) {

        assertEquals("RWY_0000001_1", runway.id);
        assertEquals(airportId, runway.airportId);
        assertEquals("05/23", runway.designator);
        assertEquals((long) 8525, runway.length, 0);
        assertEquals(200, runway.width, 0);
    }

    public static void basicRunwayEnd(RunwayEnd runwayEnd, String runwayId) {

        assertEquals("RWY_BASE_END_0000001_1", runwayEnd.id);
        assertEquals(runwayId, runwayEnd.runwayId);
        assertEquals("05", runwayEnd.designator);
    }

    public static void basicRunwayDirection(RunwayDirection runwayDirection, String runwayEndId) {

        assertEquals("RWY_DIRECTION_BASE_END_0000001_1", runwayDirection.id);
        assertEquals(runwayEndId, runwayDirection.runwayEndId);
        assertEquals(50.5679, runwayDirection.latitude, 0);
        assertEquals(-170.1233, runwayDirection.longitude, 0);
    }
}
