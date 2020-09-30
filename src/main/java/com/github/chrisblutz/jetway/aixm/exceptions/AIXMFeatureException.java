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
package com.github.chrisblutz.jetway.aixm.exceptions;

import com.github.chrisblutz.jetway.features.Feature;

/**
 * This exception class is used when errors occur related
 * to the structure or contents of AIXM feature classes,
 * which are annotated by
 * {@link com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature AIXMFeature}.
 *
 * @author Christopher Lutz
 */
public class AIXMFeatureException extends RuntimeException {

    /**
     * This constructor creates a new instance using the specified feature class
     * and the specified message.
     *
     * @param featureClass the class of the feature that caused the error
     * @param message      the error message
     */
    public AIXMFeatureException(Class<? extends Feature> featureClass, String message) {

        super("Feature: " + featureClass.getSimpleName() + " -> " + message);
    }
}
