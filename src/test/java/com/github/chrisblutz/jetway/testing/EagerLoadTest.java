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
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Feature;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.testing.utils.AIXMUtils;
import com.github.chrisblutz.jetway.testing.utils.AssertionUtils;
import com.github.chrisblutz.jetway.testing.utils.TestUtils;
import com.github.chrisblutz.jetway.testing.utils.ValidationFeatures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class handles testing of Jetway's eager vs. lazy
 * loading techniques.
 *
 * @author Christopher Lutz
 */
public class EagerLoadTest {

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        Jetway.reset();
    }

    /**
     * This method tests loading an airport using eager loading.
     */
    @Test
    public void testLoadEagerAirport() {

        Airport validationAirport = ValidationFeatures.EAGER_AIRPORT;
        Runway validationRunway = ValidationFeatures.EAGER_RUNWAY_1;
        Feature[] features = new Feature[]{validationAirport, validationRunway};

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        Database.enableEagerLoading(true);
        TestUtils.initializeJetway(source);

        // Eager-load airport
        Airport airport = Airport.select(null);
        AssertionUtils.assertFeature(airport, ValidationFeatures.EAGER_AIRPORT);

        // Reset Jetway so we can load the new data
        Jetway.reset();

        Airport validationAirportNew = ValidationFeatures.EAGER_AIRPORT;
        Runway validationRunwayNew = ValidationFeatures.EAGER_RUNWAY_2;
        features = new Feature[]{validationAirportNew, validationRunwayNew};

        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Get runway (since we used eager loading, it should still be the first one)
        Runway[] runways = airport.getRunways();
        Assert.assertEquals(1, runways.length);
        Assert.assertEquals(1, runways.length);
        AssertionUtils.assertFeature(runways[0], ValidationFeatures.EAGER_RUNWAY_1);
    }

    /**
     * This method tests loading an airport using lazy loading.
     */
    @Test
    public void testLoadLazyAirport() {

        Airport validationAirport = ValidationFeatures.EAGER_AIRPORT;
        Runway validationRunway = ValidationFeatures.EAGER_RUNWAY_1;
        Feature[] features = new Feature[]{validationAirport, validationRunway};

        AIXMSource source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Lazy-load airport
        Airport airport = Airport.select(null);
        AssertionUtils.assertFeature(airport, ValidationFeatures.EAGER_AIRPORT);

        // Reset Jetway so we can load the new data
        Jetway.reset();

        Airport validationAirportNew = ValidationFeatures.EAGER_AIRPORT;
        Runway validationRunwayNew = ValidationFeatures.EAGER_RUNWAY_2;
        features = new Feature[]{validationAirportNew, validationRunwayNew};

        source = AIXMUtils.createSourceForFeatures(Airport.AIXM_FILE, features);
        TestUtils.initializeJetway(source);

        // Get runway (since we used lazy loading, it should be the second one since we've reloaded)
        Runway[] runways = airport.getRunways();
        Assert.assertEquals(1, runways.length);
        AssertionUtils.assertFeature(runways[0], ValidationFeatures.EAGER_RUNWAY_2);
    }
}
