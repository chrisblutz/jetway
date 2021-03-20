/*
 * Copyright 2021 Christopher Lutz
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
package com.github.chrisblutz.jetway.testing.utils;

import com.github.chrisblutz.jetway.testing.RangeEnforcementTests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * This class handles loading template files for tests as strings.
 *
 * @author Christopher Lutz
 */
public class TemplateUtils {

    /**
     * This method loads a resource as a stream using {@link Class#getResourceAsStream(String)},
     * then loads that stream into a {@link String}.
     *
     * @param name the resource to load
     * @return The resource loaded as a {@link String}.
     */
    public static String loadResourceAsString(String name) {

        InputStream stream = RangeEnforcementTests.class.getResourceAsStream(name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        // Join all lines with newline characters
        return reader.lines().collect(Collectors.joining("\n"));
    }
}
