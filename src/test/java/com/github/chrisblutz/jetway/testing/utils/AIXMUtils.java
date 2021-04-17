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

import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMStreamSource;
import com.github.chrisblutz.jetway.features.*;
import com.github.chrisblutz.jetway.features.fields.Ownership;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles the creation of AIXM sources
 * and the conversion of features into AIXM strings.
 *
 * @author Christopher Lutz
 */
public class AIXMUtils {

    // Regex to match empty tags, comment lines, or empty lines
    private static final String EMPTY_TAG_REGEX = "\\s*<(?!/)[a-zA-Z0-9\"':._/=#%@\\s]+(?<!/)>[\\s\\n]*</[a-zA-Z0-9:]+>\\s*|\\s*<!--[a-zA-Z0-9\\s]+-->\\s*|\\n\\s*(?=\\n)";
    private static final Pattern EMPTY_TAG_PATTERN = Pattern.compile(EMPTY_TAG_REGEX);

    // Regex to strip AH_ and RWY_ from the front of IDs
    private static final String ID_SANITIZATION_REGEX = "[A-Z_]+_([0-9_]+)";
    private static final Pattern ID_SANITIZATION_PATTERN = Pattern.compile(ID_SANITIZATION_REGEX);

    /**
     * This method constructs a new {@link AIXMSource} using
     * the specified {@link InputStream} for the given feature file name.
     *
     * @param feature the feature file name
     * @param stream  the {@link InputStream} for the feature
     * @return The constructed {@link AIXMSource}
     */
    public static AIXMSource constructSource(String feature, InputStream stream) {

        AIXMStreamSource source = new AIXMStreamSource();
        source.addStream(feature, stream);
        return source;
    }

    /**
     * This method creates a new AIXM source based on the specified features.
     * <p>
     * The features are converted into XML using templates, and then combined
     * to form a single AIXM file, which is passed to an AIXM source.
     *
     * @param aixmFile the AIXM file the features are part of ({@code APT_AIXM}, etc.)
     * @param features the features to combine
     * @return The AIXM source for the features.
     */
    public static AIXMSource createSourceForFeatures(String aixmFile, Feature... features) {

        // Create source that is valid from one hour prior to the current time
        ZonedDateTime currentTime = ZonedDateTime.now().minus(1, ChronoUnit.HOURS);

        return createSourceForFeatures(aixmFile, currentTime, features);
    }

    /**
     * This method creates a new AIXM source based on the specified features.
     * <p>
     * The features are converted into XML using templates, and then combined
     * to form a single AIXM file, which is passed to an AIXM source.
     * <p>
     * This method also provides a parameter to specify the {@code validFrom}
     * date for the data, so that out-of-date data can be tested.
     *
     * @param aixmFile  the AIXM file the features are part of ({@code APT_AIXM}, etc.)
     * @param validFrom the date when this data goes into effect (the data becomes ineffective again
     *                  after 28 days)
     * @param features  the features to combine
     * @return The AIXM source for the features.
     */
    public static AIXMSource createSourceForFeatures(String aixmFile, ZonedDateTime validFrom, Feature... features) {

        // Turn the features into the contents of an AIXM file
        String aixmString = createForFeatures(validFrom, features);

        // Create byte array stream for the string
        InputStream stream = new ByteArrayInputStream(aixmString.getBytes(StandardCharsets.UTF_8));

        // Construct the source itself based on the stream
        return constructSource(aixmFile, stream);
    }

    private static String createForFeatures(ZonedDateTime validFrom, Feature... features) {

        Map<String, Object> fileValues = new HashMap<>();

        // Set the validFrom field
        fileValues.put("file.validFrom", validFrom.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // Concatenate all feature template results into list, then combine
        List<String> fileContents = new ArrayList<>();
        for (Feature feature : features) {

            // Load template for feature and fill
            String featureString = "";
            if (feature instanceof Airport)
                featureString = getAirport((Airport) feature);
            else if (feature instanceof Runway)
                featureString = getRunway((Runway) feature);
            else if (feature instanceof RunwayEnd)
                featureString = getRunwayEnd((RunwayEnd) feature);
            else if (feature instanceof RunwayDirection)
                featureString = getRunwayDirection((RunwayDirection) feature);

            if (!featureString.isEmpty())
                fileContents.add(featureString);
        }
        fileValues.put("file.contents", String.join("\n", fileContents));

        return fillTemplate("file", fileValues);
    }

    private static String getAirport(Airport airport) {

        Map<String, Object> extensionValues = new HashMap<>();
        extensionValues.put("airport.id", sanitizeID(airport.id));
        extensionValues.put("airport.siteNumber", airport.siteNumber);
        extensionValues.put("airport.landArea", airport.landArea);
        extensionValues.put("airport.county", airport.county);
        extensionValues.put("airport.state", airport.state);
        extensionValues.put("airport.ownership", getOwnershipAIXM(airport.ownership));
        extensionValues.put("airport.numberOfSingleEngineAircraft", airport.numberOfSingleEngineAircraft);
        extensionValues.put("airport.numberOfMultiEngineAircraft", airport.numberOfMultiEngineAircraft);
        extensionValues.put("airport.numberOfJetEngineAircraft", airport.numberOfJetEngineAircraft);
        extensionValues.put("airport.numberOfHelicopters", airport.numberOfHelicopters);
        extensionValues.put("airport.numberOfGliders", airport.numberOfGliders);
        extensionValues.put("airport.numberOfMilitaryAircraft", airport.numberOfMilitaryAircraft);
        extensionValues.put("airport.numberOfUltralightAircraft", airport.numberOfUltralightAircraft);

        // Fill extension template with values
        String extension = fillTemplate("airport_extension", extensionValues);

        Map<String, Object> airportValues = new HashMap<>();
        airportValues.put("airport.id", sanitizeID(airport.id));
        airportValues.put("airport.name", airport.name);
        airportValues.put("airport.iataDesignator", airport.iataDesignator);
        airportValues.put("airport.icao", airport.icao);
        airportValues.put("airport.privateUseOnly", airport.privateUseOnly ? "YES" : "NO");
        airportValues.put("airport.fieldElevation", airport.fieldElevation);
        airportValues.put("airport.longitude", airport.longitude);
        airportValues.put("airport.latitude", airport.latitude);
        airportValues.put("airport.servedCity", airport.servedCity);
        airportValues.put("airport.extension", extension);

        // Fill airport template with values
        return fillTemplate("airport", airportValues);
    }

    private static String getOwnershipAIXM(Ownership ownership) {

        if (ownership == null)
            return null;

        switch (ownership) {
            case AIR_FORCE:
                return "MA";
            case NAVY:
                return "MN";
            case ARMY:
                return "MR";
            case PRIVATE:
                return "PR";
            case PUBLIC:
                return "PU";
            default:
                return null;
        }
    }

    private static String getRunway(Runway runway) {

        Map<String, Object> runwayValues = new HashMap<>();
        runwayValues.put("runway.id", sanitizeID(runway.id));
        runwayValues.put("runway.airportId", runway.airportId);
        runwayValues.put("runway.designator", runway.designator);
        runwayValues.put("runway.length", runway.length);
        runwayValues.put("runway.width", runway.width);

        // Fill runway template with values
        return fillTemplate("runway", runwayValues);
    }

    private static String getRunwayEnd(RunwayEnd runwayEnd) {

        Map<String, Object> runwayEndValues = new HashMap<>();
        runwayEndValues.put("runwayend.id", sanitizeID(runwayEnd.id));
        runwayEndValues.put("runwayend.baseReciprocal", runwayEnd.id.contains("BASE") ? "BASE" : "RECIPROCAL");
        runwayEndValues.put("runwayend.airportId", runwayEnd.airportId);
        runwayEndValues.put("runwayend.designator", runwayEnd.designator);

        // Fill runway end template with values
        return fillTemplate("runwayend", runwayEndValues);
    }

    private static String getRunwayDirection(RunwayDirection runwayDirection) {

        Map<String, Object> extensionValues = new HashMap<>();
        extensionValues.put("runwaydirection.id", sanitizeID(runwayDirection.id));
        extensionValues.put("runwaydirection.baseReciprocal", runwayDirection.id.contains("BASE") ? "BASE" : "RECIPROCAL");
        extensionValues.put("runwaydirection.longitude", runwayDirection.longitude);
        extensionValues.put("runwaydirection.latitude", runwayDirection.latitude);

        // Fill extension template with values
        String extension = fillTemplate("runwaydirection_extension", extensionValues);

        Map<String, Object> runwayDirectionValues = new HashMap<>();
        runwayDirectionValues.put("runwaydirection.id", sanitizeID(runwayDirection.id));
        runwayDirectionValues.put("runwaydirection.baseReciprocal", runwayDirection.id.contains("BASE") ? "BASE" : "RECIPROCAL");
        runwayDirectionValues.put("runwaydirection.runwayEndId", runwayDirection.runwayEndId);
        runwayDirectionValues.put("runwaydirection.extension", extension);

        // Fill runway direction template with values
        return fillTemplate("runwaydirection", runwayDirectionValues);
    }

    private static String sanitizeID(String id) {

        // Remove AH_, RWY_, etc. prefixes
        Matcher matcher = ID_SANITIZATION_PATTERN.matcher(id);
        if (matcher.matches())
            return matcher.group(1);
        else
            return null;
    }

    private static String fillTemplate(String filename, Map<String, Object> values) {

        // Load template from file
        String template = TemplateUtils.getAIXMFileTemplate(filename);

        // Replace all placeholders with values
        for (String key : values.keySet()) {

            // Extract object, insert "" if value is null
            Object value = values.get(key);
            template = template.replaceAll("\\{" + key + "}", value == null ? "" : value.toString());
        }

        // Remove any empty tags (tags that were filled by null values)
        Matcher matcher;
        while ((matcher = EMPTY_TAG_PATTERN.matcher(template)).find())
            template = matcher.replaceAll("");

        return template;
    }
}
