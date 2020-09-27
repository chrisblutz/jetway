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
import com.github.chrisblutz.jetway.aixm.AIXMFeatureManager;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMFeatureException;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;
import com.github.chrisblutz.jetway.aixm.mappings.FeatureMapping;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import com.github.chrisblutz.jetway.testing.features.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * This class handles testing of feature
 * registration functionality.
 *
 * @author Christopher Lutz
 */
public class FeatureTests {

    /**
     * This method resets Jetway before each test.
     */
    @Before
    public void beforeAll() {

        JetwayLog.setLoggingEnabled(false);
        Jetway.reset();
    }

    /**
     * This method tests that unannotated features are not registered.
     */
    @Test
    public void testNonAnnotatedFeature() {

        AIXMFeatureManager.registerFeatureType(NoAnnotationFeature.class);
        assertNull(AIXMFeatureManager.get(NoAnnotationFeature.class));
    }

    /**
     * This method tests that unannotated fields are not registered.
     */
    @Test
    public void testNonAnnotatedFieldFeature() {

        AIXMFeatureManager.registerFeatureType(NoAnnotationFieldFeature.class);
        FeatureEntry entry = AIXMFeatureManager.get(NoAnnotationFieldFeature.class);
        assertEquals(0, entry.getMapping().getFields().size());
    }

    /**
     * This method tests that orphaned features (no parent and not root)
     * throw exceptions when registered.
     */
    @Test(expected = AIXMFeatureException.class)
    public void testOrphanedFeature() {

        AIXMFeatureManager.registerFeatureType(OrphanFeature.class);
    }

    /**
     * This method tests that features with the wrong ID type
     * throw exceptions when registered.
     */
    @Test(expected = AIXMFeatureException.class)
    public void testInvalidIdFeature() {

        AIXMFeatureManager.registerFeatureType(InvalidIdFeature.class);
    }

    /**
     * This method tests that features with the wrong parent ID type
     * throw exceptions when registered.
     */
    @Test(expected = AIXMFeatureException.class)
    public void testInvalidParentIdFeature() {

        AIXMFeatureManager.registerFeatureType(RootFeature.class);
        AIXMFeatureManager.registerFeatureType(InvalidParentIdFeature.class);
    }

    /**
     * This method tests that features with primitive type fields
     * (which are declared as attributes) throw exceptions when
     * registered.
     */
    @Test(expected = AIXMFeatureException.class)
    public void testPrimitiveTypeFieldFeature() {

        SchemaManager.registerFeatureType(PrimitiveTypeFieldFeature.class);
    }

    /**
     * This method tests that features marked as root features
     * are registered as such.
     *
     * @throws NoSuchFieldException if either attribute field is not found
     *                              (this should never happen)
     */
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
