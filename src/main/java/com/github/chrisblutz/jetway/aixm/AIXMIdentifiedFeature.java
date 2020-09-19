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

import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;

/**
 * This class represents an AIXM feature and its associated
 * ID and {@link FeatureEntry}.
 *
 * @author Christopher Lutz
 */
public class AIXMIdentifiedFeature {

    private final String id;
    private final Object value;
    private final FeatureEntry entry;

    /**
     * This constructor generates a new instance with the specified value,
     * ID, and associated {@link FeatureEntry}.
     *
     * @param id    the ID for the feature
     * @param value the value of the feature itself
     * @param entry the {@link FeatureEntry} for the feature
     */
    public AIXMIdentifiedFeature(String id, Object value, FeatureEntry entry) {

        this.id = id;
        this.value = value;
        this.entry = entry;
    }

    /**
     * This method gets the ID for the feature
     *
     * @return The ID for the feature
     */
    public String getId() {

        return id;
    }

    /**
     * This method gets the value for the feature
     *
     * @return The value for the feature
     */
    public Object getValue() {

        return value;
    }

    /**
     * This method gets the {@link FeatureEntry} associated
     * with this feature.
     *
     * @return The {@link FeatureEntry} for this feature
     */
    public FeatureEntry getEntry() {

        return entry;
    }
}
