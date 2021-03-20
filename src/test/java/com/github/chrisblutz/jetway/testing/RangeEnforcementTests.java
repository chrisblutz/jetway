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
import com.github.chrisblutz.jetway.exceptions.JetwayException;
import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.testing.utils.JetwayAssertions;
import com.github.chrisblutz.jetway.testing.utils.JetwayTesting;
import com.github.chrisblutz.jetway.testing.utils.TemplateUtils;
import com.github.chrisblutz.jetway.testing.utils.ValidationArrays;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

        String template = TemplateUtils.loadResourceAsString("/aixm/range_valid_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, false, true);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests using an out-of-date date range from the past using strict enforcement.
     */
    @Test(expected = JetwayException.class)
    public void testPastLoad() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_past_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, false, true);
    }

    /**
     * This method tests using a range from the future using strict enforcement.
     */
    @Test(expected = JetwayException.class)
    public void testFutureLoad() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_future_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, false, true);
    }

    /**
     * This method tests using an out-of-date range from the past using lenient enforcement.
     */
    @Test
    public void testPastLoadLenient() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_past_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, false, false);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests using a range from the future using lenient enforcement.
     */
    @Test
    public void testFutureLoadLenient() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_future_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, false, false);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests using an out-of-date range from the past using ignored, strict enforcement.
     */
    @Test
    public void testPastLoadIgnoreStrict() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_past_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, true, true);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    /**
     * This method tests using a range from the future using ignored, strict enforcement.
     */
    @Test
    public void testFutureLoadIgnoreStrict() {

        String template = TemplateUtils.loadResourceAsString("/aixm/range_invalid_future_template.xml");
        template = replaceTemplateFields(template);

        AIXMSource source = JetwayTesting.constructSource(Airport.AIXM_FILE, template);

        JetwayTesting.initializeJetway(source, false, true, true);

        Airport[] airports = Airport.selectAll(null);
        JetwayAssertions.assertFeatures(airports, ValidationArrays.LOAD_NO_EXTENSION_AIRPORTS, 0);
    }

    private String replaceTemplateFields(String template) {

        ZonedDateTime past = ZonedDateTime.now().minus(29, ChronoUnit.DAYS);
        ZonedDateTime current = ZonedDateTime.now().minus(1, ChronoUnit.HOURS);
        ZonedDateTime future = ZonedDateTime.now().plus(29, ChronoUnit.DAYS);

        template = template.replace("{past}", past.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        template = template.replace("{current}", current.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        template = template.replace("{future}", future.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        return template;
    }
}
