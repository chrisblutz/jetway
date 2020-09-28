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
package com.github.chrisblutz.jetway.aixm;

import com.github.chrisblutz.jetway.Jetway;
import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import gov.faa.aixm51.SubscriberFileComponentPropertyType;
import gov.faa.aixm51.SubscriberFileDocument;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class handles loading AIXM ZIP files into memory
 * as XMLBeans types.
 *
 * @author Christopher Lutz
 */
public final class AIXMFiles {

    private AIXMFiles() {}

    /**
     * This method loads the specified feature file into memory
     * as XMLBeans types.
     *
     * @param featureFileName the feature file to load
     * @return The array of XMLBeans properties that were loaded
     */
    public static SubscriberFileComponentPropertyType[] loadAIXMFile(String featureFileName) {

        try {

            // Handle overridden input streams
            InputStream is = Jetway.getAIXMSource().getStreamForFeature(featureFileName);

            // If input stream is null, throw an error
            if (is == null) {

                AIXMException exception = new AIXMException("Input stream to AIXM source was null.");
                JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
                throw exception;
            }

            // Load data into an AIXM subscriber file document
            SubscriberFileDocument doc = SubscriberFileDocument.Factory.parse(is);

            // Close AIXM source after loading
            Jetway.getAIXMSource().close();

            // Extract array of properties and return
            return doc.getSubscriberFile().getMemberArray();

        } catch (IOException | XmlException e) {

            AIXMException exception = new AIXMException("An error occurred while loading the feature file '" + featureFileName + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }
}
