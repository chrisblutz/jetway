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

import com.github.chrisblutz.jetway.aixm.AIXMFeatureManager;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.logging.JetwayLog;

/**
 * This class manages feature registration between the AIXM manager
 * and the database manager.
 *
 * @author Christopher Lutz
 */
public final class FeatureManager {

    private FeatureManager() {}

    /**
     * This method registers a feature class with both
     * {@link AIXMFeatureManager} and {@link SchemaManager}.
     * <p>
     * Classes passed to this method should be annotated with both
     * {@link com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature AIXMFeature}
     * and {@link com.github.chrisblutz.jetway.database.annotations.DatabaseTable DatabaseTable}.
     *
     * @param featureClass the feature class to register
     */
    public static void register(Class<? extends Feature> featureClass) {

        JetwayLog.getJetwayLogger().info("Registering feature '" + featureClass.getSimpleName() + "'...");

        SchemaManager.registerFeatureType(featureClass);
        AIXMFeatureManager.registerFeatureType(featureClass);
    }
}
