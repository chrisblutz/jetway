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
package com.github.chrisblutz.jetway.aixm.annotations;

import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link AIXMFeature} annotation denotes that a class
 * should be considered as an AIXM feature and that its fields
 * should be loaded based on the data in the specified AIXM features.
 *
 * @author Christopher Lutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AIXMFeature {

    /**
     * This attribute denotes the name of the feature as defined
     * in AIXM files.  For example, the {@link com.github.chrisblutz.jetway.features.Airport Airport}
     * feature is denoted in the AIXM files as {@code AirportHeliport}.
     *
     * @return The name of the AIXM feature as found in the AIXM files
     */
    String name();

    /**
     * This attribute denotes the regular expression that should be
     * used to identify this feature based on AIXM ID.
     * <p>
     * See {@link FeatureEntry#getId()} for details on the regular expressions.
     *
     * @return The ID regular expression for this feature as found in the AIXM files
     */
    String id();

    /**
     * This attribute denotes the AIXM file that contains this feature.
     *
     * @return The AIXM file that contains this feature
     */
    String aixmFile();
}
