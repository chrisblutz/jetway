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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link AIXMRoot} annotation denotes that a class
 * (which should also feature an {@link AIXMFeature} annotation)
 * is a "root" feature for its file.  This is generally
 * a feature which does not "belong" to any other feature, such as how
 * runways belong to airports.
 * <p>
 * For example, {@link com.github.chrisblutz.jetway.features.Airport Airport}
 * is the root for its file.
 *
 * @author Christopher Lutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AIXMRoot {

    /**
     * The value attribute defines the name of the files that AIXM
     * data for this feature and all child features can be found in.
     * Both the ZIP and XML files should have this name.
     * <p>
     * For example, the file names for the {@link com.github.chrisblutz.jetway.features.Airport Airport}
     * feature and its children is {@code APT_AIXM}.
     *
     * @return the file name for AIXM files that contain this feature and its children
     */
    String value();
}
