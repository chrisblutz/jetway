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
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMFeatureException;
import com.github.chrisblutz.jetway.aixm.features.*;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureMapping;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FeatureTests {

    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    @Test
    public void testNonAnnotatedFeature() {

        AIXMFeatureManager.registerFeatureType(NoAnnotationFeature.class);
        assertNull(AIXMFeatureManager.get(NoAnnotationFeature.class));
    }

    @Test
    public void testNonAnnotatedFieldFeature() {

        AIXMFeatureManager.registerFeatureType(NoAnnotationFieldFeature.class);
        FeatureEntry entry = AIXMFeatureManager.get(NoAnnotationFieldFeature.class);
        assertEquals(0, entry.getMapping().getFields().size());
    }

    @Test(expected = AIXMFeatureException.class)
    public void testOrphanedFeature() {

        AIXMFeatureManager.registerFeatureType(OrphanFeature.class);
    }

    @Test(expected = AIXMFeatureException.class)
    public void testInvalidIdFeature() {

        AIXMFeatureManager.registerFeatureType(InvalidIdFeature.class);
    }

    @Test(expected = AIXMFeatureException.class)
    public void testInvalidParentIdFeature() {

        AIXMFeatureManager.registerFeatureType(RootFeature.class);
        AIXMFeatureManager.registerFeatureType(InvalidParentIdFeature.class);
    }

    @Test
    public void testRootFeature() throws NoSuchFieldException {

        AIXMFeatureManager.registerFeatureType(RootFeature.class);
        FeatureEntry entry = AIXMFeatureManager.get(RootFeature.class);
        assertTrue(entry.isRoot());
        assertFalse(entry.isChild());
        assertEquals("ROOT", entry.getRootPath());
        assertEquals("TestFeature", entry.getName());
        assertEquals("FEATURE", entry.getId());

        FeatureMapping mapping = entry.getMapping();
        assertEquals(RootFeature.class.getField("id"), mapping.getIDField());
        assertEquals(1, mapping.getFields().size());

        Field field = mapping.getFields().iterator().next();
        assertEquals(RootFeature.class.getField("path"), field);
        assertEquals("Path", mapping.getPathForField(field));
    }
}
