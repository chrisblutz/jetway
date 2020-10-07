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

import com.github.chrisblutz.jetway.features.Feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link AIXMForeign} annotation indicates that the annotated
 * field refers to an ID of another feature, like foreign keys in a
 * database.
 *
 * @author Christopher Lutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AIXMForeign {

    /**
     * This attribute denotes the feature that this ID references.
     *
     * @return The feature that this ID references
     */
    Class<? extends Feature> feature() default Feature.class;

    /**
     * This attribute denotes the "path" to the field's data
     * within the AIXM feature.  See
     * {@link com.github.chrisblutz.jetway.aixm.crawling.AIXMData#crawl(String) AIXMData.crawl()}
     * to see the path description.
     *
     * @return The path to the field's data within the AIXM feature
     */
    String path();
}
