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
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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
}
