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
package com.github.chrisblutz.jetway.aixm.source;

import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.logging.JetwayLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a source of AIXM data
 * made up of an {@link InputStream} for each
 * AIXM feature file.
 *
 * @author Christopher Lutz
 */
public class AIXMStreamSource implements AIXMSource {

    private final Map<String, InputStream> streamMap = new HashMap<>();

    /**
     * This method registers a new {@link InputStream}
     * for the specified feature file name.
     *
     * @param feature     the feature file name
     * @param inputStream the {@link InputStream} for the feature
     */
    public void addStream(String feature, InputStream inputStream) {

        streamMap.put(feature, inputStream);
    }

    @Override
    public InputStream getStreamForFeature(String feature) {

        // Get feature's stream from mapping
        JetwayLog.getJetwayLogger().info("Retrieving input stream for feature file '" + feature + "'...");
        return streamMap.get(feature);
    }

    @Override
    public void close() {

        try {

            // Close all streams
            for (InputStream stream : streamMap.values())
                stream.close();

        } catch (IOException e) {

            AIXMException exception = new AIXMException("An error occurred while closing the AIXM ZIP file.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }
}
