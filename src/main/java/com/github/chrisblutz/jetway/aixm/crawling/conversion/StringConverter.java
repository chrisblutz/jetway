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
package com.github.chrisblutz.jetway.aixm.crawling.conversion;

/**
 * This interface declares the basic structure for classes
 * that convert {@link String} types into various other types.
 * <p>
 * This class is used by {@link BasicConverter} instances.
 *
 * @param <T> the type to convert to
 * @author Christopher Lutz
 */
public interface StringConverter<T> {

    /**
     * This method converts the given {@link String} into a value
     * of the specified type, or {@code null} if such a conversion
     * is not possible.
     *
     * @param string the {@link String} to convert
     * @return The converted value
     */
    T convert(String string);
}
