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

import com.github.chrisblutz.jetway.aixm.exceptions.AIXMException;
import com.github.chrisblutz.jetway.cli.CLI;
import com.github.chrisblutz.jetway.logging.JetwayLog;
import gov.faa.aixm51.SubscriberFileComponentPropertyType;
import gov.faa.aixm51.SubscriberFileDocument;
import org.apache.xmlbeans.XmlException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class handles loading NASR AIXM ZIP files into memory
 * as XMLBeans types.
 *
 * @author Christopher Lutz
 */
public class AIXMFiles {

    private static ZipInputStream outerZipStream;
    private static final Map<String, InputStream> customInputStreams = new HashMap<>();

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
            InputStream is;
            if (customInputStreams.containsKey(featureFileName))
                is = customInputStreams.get(featureFileName);
            else
                is = load(featureFileName);

            // If input stream is null, return empty array
            if (is == null)
                return new SubscriberFileComponentPropertyType[0];

            // Load data into an AIXM subscriber file document
            SubscriberFileDocument doc = SubscriberFileDocument.Factory.parse(is);

            // Close input stream after loading
            is.close();
            if (outerZipStream != null)
                outerZipStream.close();

            // Extract array of properties and return
            return doc.getSubscriberFile().getMemberArray();

        } catch (IOException | XmlException e) {

            AIXMException exception = new AIXMException("An error occurred while loading the feature file '" + featureFileName + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    /**
     * This method opens an {@link InputStream} to the specified feature file,
     * which is located within the NASR AIXM ZIP file.
     *
     * @param featureFileName the feature file to load
     * @return An {@link InputStream} pointing to the specified file
     */
    public static InputStream load(String featureFileName) {

        try {

            // Open ZIP input stream for full NASR file
            JetwayLog.getJetwayLogger().info("Opening NASR ZIP file...");
            outerZipStream = new ZipInputStream(new FileInputStream(CLI.Options.getNASRFile()));

            // Find inner ZIP file for specific feature
            JetwayLog.getJetwayLogger().info("Locating inner ZIP for feature with name '" + featureFileName + "'...");
            outerZipStream = findZipEntry(outerZipStream, "AIXM_5.1/XML-Subscriber-Files/" + featureFileName + ".zip");

            // Check that inner ZIP exists
            if (outerZipStream == null)
                return null;

            // Find XML file for the feature
            JetwayLog.getJetwayLogger().info("Locating XML for feature with name '" + featureFileName + "'...");
            ZipInputStream innerZipStream = new ZipInputStream(outerZipStream);
            return findZipEntry(innerZipStream, featureFileName + ".xml");

        } catch (IOException e) {

            AIXMException exception = new AIXMException("An error occurred while locating the ZIP entry for the feature file '" + featureFileName + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private static ZipInputStream findZipEntry(ZipInputStream zipStream, String entryName) {

        ZipEntry entry;
        try {

            while ((entry = zipStream.getNextEntry()) != null)
                if (!entry.isDirectory() && entry.getName().equals(entryName))
                    return zipStream;

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method allows for custom input streams to be defined for
     * each AIXM XML file.  For instance, if data has been removed
     * from its ZIP file or split, this method can be used to tell
     * Jetway where to load the data from.
     *
     * @param featureFileName the file name for the feature this input stream is for
     * @param stream          the stream itself
     */
    public static void registerCustomInputStream(String featureFileName, InputStream stream) {

        customInputStreams.put(featureFileName, stream);
    }

    /**
     * This method resets all custom input stream designations.
     */
    public static void reset() {

        customInputStreams.clear();
    }
}
