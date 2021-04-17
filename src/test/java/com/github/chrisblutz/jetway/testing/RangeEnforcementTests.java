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
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.database.enforcement.Enforcement;
import com.github.chrisblutz.jetway.exceptions.JetwayException;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.testing.utils.AIXMUtils;
import com.github.chrisblutz.jetway.testing.utils.AssertionUtils;
import com.github.chrisblutz.jetway.testing.utils.TestUtils;
import com.github.chrisblutz.jetway.testing.utils.ValidationFeatures;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

/**
 * This class handles testing of basic AIXM feature-loading
 * functionality.
 *
 * @author Christopher Lutz
 */
public class RangeEnforcementTests {

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        Jetway.reset();
    }

    /**
     * This method tests using a currently-valid date range using strict enforcement.
     */
    @Test
    public void testCurrentLoad() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);
        TestUtils.initializeJetway(source, false, Enforcement.STRICT, true);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests using an out-of-date date range from the past using strict enforcement.
     */
    @Test(expected = JetwayException.class)
    public void testPastLoad() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.STRICT, true);
    }

    /**
     * This method tests using a range from the future using strict enforcement.
     */
    @Test(expected = JetwayException.class)
    public void testFutureLoad() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getFutureTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.STRICT, true);
    }

    /**
     * This method tests using an out-of-date range from the past using lenient enforcement.
     */
    @Test
    public void testPastLoadLenient() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.LENIENT, true);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests using a range from the future using lenient enforcement.
     */
    @Test
    public void testFutureLoadLenient() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getFutureTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.LENIENT, true);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests using a range from the past when the database has prior data.
     */
    @Test(expected = JetwayException.class)
    public void testPastLoadWithPrior() {

        // Load old data into database intentionally
        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.IGNORE, true);

        // Reset Jetway so it is ready to load the next attempt
        Jetway.reset();

        // Now attempt to load old data, and it should fail with strict enforcement
        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validationNew);

        TestUtils.initializeJetway(source, false, Enforcement.STRICT, false);
    }

    /**
     * This method tests using a range from the future when the database has prior data.
     */
    @Test(expected = JetwayException.class)
    public void testFutureLoadWithPrior() {

        // Load old data into database intentionally
        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validation);

        TestUtils.initializeJetway(source, false, Enforcement.IGNORE, true);

        // Reset Jetway so it is ready to load the next attempt
        Jetway.reset();

        // Now attempt to load future data, and it should fail with strict enforcement
        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getFutureTime(), validationNew);

        TestUtils.initializeJetway(source, false, Enforcement.STRICT, false);
    }

    /**
     * This method tests trying to load new data when the current data is still valid.
     */
    @Test
    public void testExisting() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validation);

        // Tell Jetway to clear any metadata before loading
        TestUtils.initializeJetway(source, true, Enforcement.STRICT, true);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        // Reset Jetway so it is ready to load the next attempt
        Jetway.reset();

        // Attempt to load new data (shouldn't load since current data is valid)
        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);

        // Tell Jetway to clear any metadata before loading
        TestUtils.initializeJetway(source, false, Enforcement.STRICT, false);

        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);
    }

    /**
     * This method tests trying to load new data when the current data is out-of-date.
     */
    @Test
    public void testExistingOld() {

        Airport validation = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT;
        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, getPastTime(), validation);

        // Tell Jetway to clear any metadata before loading
        TestUtils.initializeJetway(source, true, Enforcement.IGNORE, true);

        Airport[] airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validation);

        // Reset Jetway so it is ready to load the next attempt
        Jetway.reset();

        // Attempt to load new data (should load since current data is invalid)
        Airport validationNew = ValidationFeatures.BASIC_NO_EXTENSION_AIRPORT_OTHER;
        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, validationNew);

        // Tell Jetway to clear any metadata before loading
        TestUtils.initializeJetway(source, false, Enforcement.STRICT, false);

        airports = Airport.selectAll(null);
        assertEquals(1, airports.length);
        AssertionUtils.assertFeature(airports[0], validationNew);
    }

    private ZonedDateTime getPastTime() {

        // Current time minus 29 days (outside validity window)
        return ZonedDateTime.now().minus(29, ChronoUnit.DAYS);
    }

    private ZonedDateTime getFutureTime() {

        // Current time plus 29 days (outside validity window)
        return ZonedDateTime.now().plus(29, ChronoUnit.DAYS);
    }
}
