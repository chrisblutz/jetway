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
package com.github.chrisblutz.jetway.testing.features;

import com.github.chrisblutz.jetway.aixm.annotations.AIXMAttribute;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMForeign;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMId;
import com.github.chrisblutz.jetway.features.NestedFeature;

/**
 * This class is used when testing for
 * invalid foreign key types.
 *
 * @author Christopher lutz
 */
@AIXMFeature(name = "TestFeature", id = "FEATURE", aixmFile = "File")
public class InvalidForeignKeyFeature implements NestedFeature {

    /**
     * This attribute should not affect the test.
     */
    @AIXMId
    public String id;

    /**
     * This ID should cause an error because it is not
     * of type {@code String}.
     */
    @AIXMForeign(feature = RootFeature.class, path = "Path")
    public int parentId;

    /**
     * This attribute should not affect the test.
     */
    @AIXMAttribute("Path")
    public String path;

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getParentId() {

        return null;
    }
}
