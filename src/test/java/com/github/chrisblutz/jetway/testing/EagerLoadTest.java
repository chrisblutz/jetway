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
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import com.github.chrisblutz.jetway.testing.utils.TemplateUtils;
import com.github.chrisblutz.jetway.testing.utils.ValidationArrays;
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

        String template = TemplateUtils.loadResourceAsString("/aixm/eager_template.xml");
        template = replaceTemplateFields(template, 1);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);
        Database.enableEagerLoading(true);
        JetwayTesting.initializeJetway(source);

        // Eager-load airport
        Airport airport = Airport.select(null);
        JetwayAssertions.assertFeature(airport, ValidationArrays.EAGER_LOADING_AIRPORT);

        // Reset Jetway so we can load the new data
        Jetway.reset();

        template = TemplateUtils.loadResourceAsString("/aixm/eager_template.xml");
        template = replaceTemplateFields(template, 2);

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);
        JetwayTesting.initializeJetway(source);

        // Get runway (since we used eager loading, it should still be the first one)
        Runway[] runways = airport.getRunways();
        Assert.assertEquals(1, runways.length);
        Runway runway = runways[0];
        JetwayAssertions.assertFeature(runway, ValidationArrays.EAGER_LOADING_RUNWAYS[0]);
    }

    /**
     * This method tests loading an airport using lazy loading.
     */
    @Test
    public void testLoadLazyAirport() {

        String template = TemplateUtils.loadResourceAsString("/aixm/eager_template.xml");
        template = replaceTemplateFields(template, 1);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);
        JetwayTesting.initializeJetway(source);

        // Lazy-load airport
        Airport airport = Airport.select(null);
        JetwayAssertions.assertFeature(airport, ValidationArrays.EAGER_LOADING_AIRPORT);

        // Reset Jetway so we can load the new data
        Jetway.reset();

        template = TemplateUtils.loadResourceAsString("/aixm/eager_template.xml");
        template = replaceTemplateFields(template, 2);

        source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);
        JetwayTesting.initializeJetway(source);

        // Get runway (since we used lazy loading, it should be the second one since we've reloaded)
        Runway[] runways = airport.getRunways();
        Assert.assertEquals(1, runways.length);
        Runway runway = runways[0];
        JetwayAssertions.assertFeature(runway, ValidationArrays.EAGER_LOADING_RUNWAYS[1]);
    }

    private String replaceTemplateFields(String template, int runwayDesignator) {

        template = template.replaceAll("\\{runway\\.designator}", Integer.toString(runwayDesignator));

        return template;
    }
}
