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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class handles loading template files for tests as strings.
 *
 * @author Christopher Lutz
 */
public class TemplateUtils {

    private static final Map<String, String> fileTemplates = new HashMap<>();

    /**
     * This method loads AIXM template files into strings for use in unit testing.
     * <p>
     * Files are assumed to be in the {@code /aixm/templates/} resource folder and
     * should have the {@code .xml} file extension.  To load a file at
     * {@code /aixm/templates/airport.xml}, the {@code filename} parameter
     * would be {@code airport}.
     *
     * @param filename the file name to load
     * @return The string containing the file's contents
     */
    public static String getAIXMFileTemplate(String filename) {

        if (!fileTemplates.containsKey(filename)) {

            // Load resource as stream
            InputStream stream = RangeEnforcementTests.class.getResourceAsStream("/aixm/templates/" + filename + ".xml");

            // Check that the stream exists, then create a reader for it
            assert stream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            // Join all lines with newline characters
            String fileContents = reader.lines().collect(Collectors.joining("\n"));

            // Store contents
            fileTemplates.put(filename, fileContents);
        }

        return fileTemplates.get(filename);
    }
}
