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
package com.github.chrisblutz.jetway.database.annotations;

import com.github.chrisblutz.jetway.database.keys.Relationship;
import com.github.chrisblutz.jetway.features.Feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link DatabaseColumn} annotation denotes that a field
 * should be a foreign key in the feature's database
 * table.
 *
 * @author Christopher Lutz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatabaseForeignKey {

    /**
     * This attribute defines which feature class this
     * foreign key links to.
     *
     * @return The feature class this key references
     */
    Class<? extends Feature> value();

    /**
     * This attribute defines the relationship between this
     * feature and the foreign key's referenced feature.
     * <p>
     * See {@link Relationship} for descriptions of the values.
     *
     * @return The relationship between this feature and the referenced feature
     */
    Relationship relationship() default Relationship.BELONGS_TO;
}
