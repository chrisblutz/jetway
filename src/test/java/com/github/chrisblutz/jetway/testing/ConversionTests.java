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
import com.github.chrisblutz.jetway.conversion.DataConversion;
import com.github.chrisblutz.jetway.conversion.DefaultConverters;
import com.github.chrisblutz.jetway.features.fields.Ownership;
import gov.faa.aixm51.apt.AirportHeliportExtensionType;
import gov.faa.aixm51.apt.impl.AirportHeliportExtensionTypeImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class handles testing of type
 * conversion functionality.
 *
 * @author Christopher Lutz
 */
public class ConversionTests {

    /**
     * This method resets Jetway before each test
     * and registers data converters.
     */
    @Before
    public void beforeAll() {

        Jetway.reset();
        DefaultConverters.registerAll();
    }

    /**
     * This method tests that data conversion
     * where a converter for the required type does not
     * exist returns null.
     */
    @Test
    public void testNoConverterForType() {

        // Use File as the unrecognized type to attempt conversion
        // In this test, the expected type (File) will have no converter
        // get() should return null
        File invalidType = new File("");
        Object result = DataConversion.get(invalidType, File.class);
        assertNull(result);
    }

    /**
     * This method tests that data conversion
     * where the converter for the required type does not
     * accept the current value returns null.
     */
    @Test
    public void testTypeNotAccepted() {

        // Use File as the unrecognized type to attempt conversion
        // In this test, the expected type (Boolean) will have a converter,
        // but File will not be one of the accepted types
        // get() should return false
        File invalidType = new File("");
        Object result = DataConversion.get(invalidType, Boolean.class);
        assertNull(result);
    }

    /**
     * This method tests that the Ownership enum
     * converter works as expected.
     */
    @Test
    public void testOwnershipEnum() {

        // Test all variations of ownership, plus a "null" conversion
        AirportHeliportExtensionType.OwnershipType.Enum airForceOwnership = AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MA;
        AirportHeliportExtensionType.OwnershipType.Enum navyOwnership = AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MN;
        AirportHeliportExtensionType.OwnershipType.Enum armyOwnership = AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MR;
        AirportHeliportExtensionType.OwnershipType.Enum privateOwnership = AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.PR;
        AirportHeliportExtensionType.OwnershipType.Enum publicOwnership = AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.PU;

        Ownership expectedAirForceOwnership = (Ownership) DataConversion.get(airForceOwnership, Ownership.class);
        Ownership expectedNavyOwnership = (Ownership) DataConversion.get(navyOwnership, Ownership.class);
        Ownership expectedArmyOwnership = (Ownership) DataConversion.get(armyOwnership, Ownership.class);
        Ownership expectedPrivateOwnership = (Ownership) DataConversion.get(privateOwnership, Ownership.class);
        Ownership expectedPublicOwnership = (Ownership) DataConversion.get(publicOwnership, Ownership.class);
        Ownership expectedNull = (Ownership) DataConversion.get(null, Ownership.class);

        assertEquals(expectedAirForceOwnership, Ownership.AIR_FORCE);
        assertEquals(expectedNavyOwnership, Ownership.NAVY);
        assertEquals(expectedArmyOwnership, Ownership.ARMY);
        assertEquals(expectedPrivateOwnership, Ownership.PRIVATE);
        assertEquals(expectedPublicOwnership, Ownership.PUBLIC);
        assertNull(expectedNull);
    }
}
