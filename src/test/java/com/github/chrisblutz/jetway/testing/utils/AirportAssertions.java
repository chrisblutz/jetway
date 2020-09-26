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

import static org.junit.Assert.*;

public class AirportAssertions {

    public static void assertAirportsOrdered(Airport[] airports, Airport[] validationAirports, int... airportIndices) {

        assertNotNull(airports);
        assertEquals(airportIndices.length, airports.length);

        for (int i = 0; i < airportIndices.length; i++) {

            assertAirport(airports[i], validationAirports[airportIndices[i]]);
        }
    }

    public static void assertAirports(Airport[] airports, Airport[] validationAirports, int... airportIndices) {

        assertNotNull(airports);
        assertEquals(airportIndices.length, airports.length);

        // Go through every airport to compare with expected airports
        boolean[] found = new boolean[airportIndices.length];
        for (Airport airport : airports) {

            for (int i = 0; i < airportIndices.length; i++) {

                if (found[i])
                    continue;

                if (assertAirportChecked(airport, validationAirports[airportIndices[i]])) {
                    found[i] = true;
                    break;
                }
            }
        }

        // Check that all expected airports were found
        for (boolean airportFound : found)
            assertTrue(airportFound);
    }

    public static boolean assertAirportChecked(Airport airport, Airport validationAirport) {

        // Check that the current airport is the airport being checked for
        if (!airport.name.equals(validationAirport.name))
            return false;

        assertAirport(airport, validationAirport);
        return true;
    }

    public static void assertAirport(Airport airport, Airport validationAirport) {

        assertEquals(validationAirport.id, airport.id);
        assertEquals(validationAirport.name, airport.name);
        assertEquals(validationAirport.fieldElevation, airport.fieldElevation);
        assertEquals(validationAirport.latitude, airport.latitude);
        assertEquals(validationAirport.longitude, airport.longitude);
        assertEquals(validationAirport.servedCity, airport.servedCity);
        assertEquals(validationAirport.iataDesignator, airport.iataDesignator);
        assertEquals(validationAirport.icao, airport.icao);
        assertEquals(validationAirport.siteNumber, airport.siteNumber);
        assertEquals(validationAirport.landArea, airport.landArea);
        assertEquals(validationAirport.county, airport.county);
        assertEquals(validationAirport.state, airport.state);
        assertEquals(validationAirport.numberOfSingleEngineAircraft, airport.numberOfSingleEngineAircraft);
        assertEquals(validationAirport.numberOfMultiEngineAircraft, airport.numberOfMultiEngineAircraft);
        assertEquals(validationAirport.numberOfJetEngineAircraft, airport.numberOfJetEngineAircraft);
        assertEquals(validationAirport.numberOfHelicopters, airport.numberOfHelicopters);
        assertEquals(validationAirport.numberOfGliders, airport.numberOfGliders);
        assertEquals(validationAirport.numberOfMilitaryAircraft, airport.numberOfMilitaryAircraft);
        assertEquals(validationAirport.numberOfUltralightAircraft, airport.numberOfUltralightAircraft);
    }
}
