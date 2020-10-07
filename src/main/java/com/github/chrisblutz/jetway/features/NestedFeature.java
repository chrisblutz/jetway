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
package com.github.chrisblutz.jetway.features;

/**
 * This interface represents an AIXM feature
 * that is nested within another feature, such as
 * {@link Runway} (nested inside {@link Airport}).
 *
 * @author Christopher Lutz
 */
public interface NestedFeature extends Feature {

    /**
     * This method retrieves the unique ID of the parent feature.
     * The "parent" feature of a feature should be the feature
     * that this feature "belongs to," like how {@link Runway} instances
     * belong to {@link Airport} instances.
     *
     * @return The unique ID of the parent feature
     */
    String getParentId();
}
