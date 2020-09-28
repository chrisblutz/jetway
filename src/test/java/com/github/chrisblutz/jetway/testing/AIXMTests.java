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
import com.github.chrisblutz.jetway.aixm.crawling.AIXMData;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.conversion.DefaultConverters;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.testing.aixm.TestObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class handles testing of AIXM data conversion
 * and crawling.
 *
 * @author Christopher Lutz
 */
public class AIXMTests {

    /**
     * This method resets Jetway before each test
     * and registers data converters.
     */
    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
        DefaultConverters.registerAll();
    }

    /**
     * This method tests the crawling/conversion
     * functionality for {@link AIXMData} instances.
     */
    @Test
    public void testDataCrawl() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);

        // Test simple crawls
        assertEquals(true, data.crawl("Boolean").get(Boolean.class));
        assertEquals((byte) 5, data.crawl("Byte").get(Byte.class));
        assertEquals('_', data.crawl("Character").get(Character.class));
        assertEquals(1.234d, data.crawl("Double").get(Double.class));
        assertEquals(5.678f, data.crawl("Float").get(Float.class));
        assertEquals(1234567, data.crawl("Integer").get(Integer.class));
        assertEquals(123456789L, data.crawl("Long").get(Long.class));
        assertEquals("Value", data.crawl("String").get(String.class));
        assertEquals((short) 12345, data.crawl("Short").get(Short.class));

        // Test indexing
        assertEquals("String 1", data.crawl("List[0]").get(String.class));
        assertEquals("String 2", data.crawl("List[1]").get(String.class));
        assertEquals("String 3", data.crawl("List[2]").get(String.class));
        assertEquals("String 1", data.crawl("Array[0]").get(String.class));
        assertEquals("String 2", data.crawl("Array[1]").get(String.class));
        assertEquals("String 3", data.crawl("Array[2]").get(String.class));

        // Test invalid types/crawls
        assertNull(data.crawl("Null").get(String.class));
        assertNull(data.crawl("NoIndex[0]").get(String.class));
    }

    /**
     * This method tests that crawling to an invalid path throws
     * and exception.
     */
    @Test(expected = AIXMException.class)
    public void testInvalidDataPath() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);
        data.crawl("InvalidPath");
    }
}
