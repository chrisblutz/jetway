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
package com.github.chrisblutz.jetway.aixm;

import com.github.chrisblutz.jetway.Jetway;
import com.github.chrisblutz.jetway.aixm.crawling.AIXMData;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.aixm.utils.TestObject;
import com.github.chrisblutz.jetway.conversion.DefaultConverters;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AIXMTests {

    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
        DefaultConverters.registerAll();
    }

    @Test
    public void testDataCrawl() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);

        assertEquals(true, data.crawl("Boolean").get(Boolean.class));
        assertEquals((byte) 5, data.crawl("Byte").get(Byte.class));
        assertEquals('_', data.crawl("Character").get(Character.class));
        assertEquals(1.234d, data.crawl("Double").get(Double.class));
        assertEquals(5.678f, data.crawl("Float").get(Float.class));
        assertEquals(1234567, data.crawl("Integer").get(Integer.class));
        assertEquals(123456789L, data.crawl("Long").get(Long.class));
        assertEquals("Value", data.crawl("String").get(String.class));
        assertEquals((short) 12345, data.crawl("Short").get(Short.class));
    }

    @Test(expected = AIXMException.class)
    public void testInvalidDataPath() {

        TestObject object = new TestObject();
        AIXMData data = new AIXMData(object);
        data.crawl("InvalidPath");
    }
}
