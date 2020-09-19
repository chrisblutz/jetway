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
package com.github.chrisblutz.jetway.utils;

/**
 * This class provides utilities for determining
 * information about the types of objects.
 *
 * @author Christopher Lutz
 */
public class TypeUtils {

    /**
     * This method determines whether a specific class is a primitive type
     * or not.
     *
     * @param type the type to determine
     * @return {@code true} if the type is primitive, {@code false} otherwise
     */
    public static boolean isPrimitive(Class<?> type) {

        return type == byte.class || type == short.class || type == int.class
                || type == long.class || type == float.class || type == double.class
                || type == char.class || type == boolean.class;
    }
}
