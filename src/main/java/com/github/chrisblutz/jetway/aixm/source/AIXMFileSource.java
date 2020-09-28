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

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class represents a source of AIXM data contained
 * within the FAA AIXM ZIP file.
 *
 * @author Christopher Lutz
 */
public class AIXMFileSource implements AIXMSource {

    private final InputStream inputStream;
    private ZipInputStream outerZipStream, innerZipStream;

    /**
     * This constructor generates a new file source
     * using the given {@link File}.
     *
     * @param file the AIXM file
     * @throws FileNotFoundException if the file isn't found
     */
    public AIXMFileSource(File file) throws FileNotFoundException {

        this(new FileInputStream(file));
    }

    /**
     * This constructor generates a new file source
     * using the given {@link InputStream}.
     *
     * @param inputStream the AIXM input stream
     */
    public AIXMFileSource(InputStream inputStream) {

        this.inputStream = inputStream;
    }

    @Override
    public InputStream getStreamForFeature(String feature) {

        // Open ZIP input stream for full AIXM file
        JetwayLog.getJetwayLogger().info("Opening AIXM ZIP file...");
        outerZipStream = new ZipInputStream(inputStream);

        // Find inner ZIP file for specific feature
        JetwayLog.getJetwayLogger().info("Locating inner ZIP file for feature with name '" + feature + "'...");
        outerZipStream = findZipEntry(outerZipStream, "AIXM_5.1/XML-Subscriber-Files/" + feature + ".zip");

        // Check that inner ZIP exists
        if (outerZipStream == null)
            return null;

        // Find XML file for the feature
        JetwayLog.getJetwayLogger().info("Locating XML for feature with name '" + feature + "'...");
        innerZipStream = new ZipInputStream(outerZipStream);
        return findZipEntry(innerZipStream, feature + ".xml");
    }

    private static ZipInputStream findZipEntry(ZipInputStream zipStream, String entryName) {

        ZipEntry entry;
        try {

            // Loop until the ZIP file runs out of entries or finds the entry it's looking for
            while ((entry = zipStream.getNextEntry()) != null)
                if (!entry.isDirectory() && entry.getName().equals(entryName))
                    return zipStream;

        } catch (IOException e) {

            AIXMException exception = new AIXMException("An error occurred while locating the ZIP entry for the feature file '" + entryName + "'.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }

        return null;
    }

    @Override
    public void close() {

        try {

            // If ZIP stream is open, close it (it closes the regular input stream also)
            // Otherwise, close the regular input stream
            if (outerZipStream != null) {

                innerZipStream.close();
                outerZipStream.close();

            } else {

                inputStream.close();
            }

        } catch (IOException e) {

            AIXMException exception = new AIXMException("An error occurred while closing the AIXM ZIP file.", e);
            JetwayLog.getJetwayLogger().error(exception.getMessage(), exception);
            throw exception;
        }
    }
}
