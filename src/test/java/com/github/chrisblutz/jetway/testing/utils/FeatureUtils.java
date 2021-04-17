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

import com.github.chrisblutz.jetway.features.*;
import com.github.chrisblutz.jetway.features.fields.Ownership;

import java.util.*;

/**
 * This class provides functionality to generate feature
 * instances for unit testing purposes.
 *
 * @author Christopher Lutz
 */
public class FeatureUtils {

    /**
     * This method merges feature arrays of different types
     * into a single array.  It is useful for combining
     * various types of features before passing them to the
     * {@link AIXMUtils#createSourceForFeatures(String, Feature...)}
     * method.
     *
     * @param featureArrays the feature arrays to combine
     * @return The merged array
     */
    public static Feature[] merge(Feature[]... featureArrays) {

        List<Feature> features = new ArrayList<>();

        // Add all features from the arrays into the final list
        for (Feature[] array : featureArrays)
            features.addAll(Arrays.asList(array));

        // Convert to a single array and return
        return features.toArray(new Feature[0]);
    }

    /**
     * This method generates a new {@link Airport} instance
     * using the specified index as an ID.
     * <p>
     * This method also automatically fills the extension values
     * for airports.
     *
     * @param index the ID of the airport (should be unique within
     *              an AIXM file)
     * @return The generated {@link Airport} instance
     */
    public static Airport generateAirport(int index) {

        return generateAirport(index, true);
    }

    /**
     * This method generates a new {@link Airport} instance
     * using the specified index as an ID.
     *
     * @param index     the ID of the airport (should be unique within
     *                  an AIXM file)
     * @param extension {@code true} to fill in the extension data,
     *                  {@code false} to leave it {@code null}
     * @return The generated {@link Airport} instance
     */
    public static Airport generateAirport(int index, boolean extension) {

        return generateAirport(index, extension, new HashMap<>());
    }

    /**
     * This method generates a new {@link Airport} instance
     * using the specified index as an ID.
     * <p>
     * The {@link Map} passed to this method is used to replace the data
     * that is automatically generated.  For example, if different airport
     * names are needed for testing, the map can be used to force a specific
     * value.  If no replacement value is provided, the generated default
     * will be used.
     *
     * @param index             the ID of the airport (should be unique within
     *                          an AIXM file)
     * @param extension         {@code true} to fill in the extension data,
     *                          {@code false} to leave it {@code null}
     * @param replacementValues the map of replacement values
     * @return The generated {@link Airport} instance
     */
    public static Airport generateAirport(int index, boolean extension, Map<String, Object> replacementValues) {

        Airport airport = new Airport();
        airport.id = String.format("AH_%07d", index);
        airport.name = (String) replacementValues.getOrDefault("name", String.format("Airport %d", index));
        airport.iataDesignator = (String) replacementValues.getOrDefault("iataDesignator", String.format("APT%d", index));
        airport.icao = (String) replacementValues.getOrDefault("icao", String.format("KAH%d", index));
        airport.privateUseOnly = (Boolean) replacementValues.getOrDefault("privateUseOnly", index % 2 == 0);
        airport.fieldElevation = (Double) replacementValues.getOrDefault("fieldElevation", 500d);
        airport.longitude = (Double) replacementValues.getOrDefault("longitude", -170.5);
        airport.latitude = (Double) replacementValues.getOrDefault("latitude", 60.25);
        airport.servedCity = (String) replacementValues.getOrDefault("servedCity", String.format("City %d", index % 3));

        if (extension) {

            airport.siteNumber = (String) replacementValues.getOrDefault("siteNumber", String.format("%06d.*A", index));
            airport.landArea = (Double) replacementValues.getOrDefault("landArea", 1200d);
            airport.county = (String) replacementValues.getOrDefault("county", String.format("County %d", index % 3));
            airport.state = (String) replacementValues.getOrDefault("state", String.format("State %d", index % 2));
            airport.ownership = (Ownership) replacementValues.getOrDefault("ownership", Ownership.values()[index % Ownership.values().length]);
            airport.numberOfSingleEngineAircraft = (Integer) replacementValues.getOrDefault("numberOfSingleEngineAircraft", 2 * (index + 1));
            airport.numberOfMultiEngineAircraft = (Integer) replacementValues.getOrDefault("numberOfMultiEngineAircraft", 2 * (index + 2));
            airport.numberOfJetEngineAircraft = (Integer) replacementValues.getOrDefault("numberOfJetEngineAircraft", 2 * (index + 3));
            airport.numberOfHelicopters = (Integer) replacementValues.getOrDefault("numberOfHelicopters", 2 * (index + 4));
            airport.numberOfGliders = (Integer) replacementValues.getOrDefault("numberOfGliders", 3 * (index + 1));
            airport.numberOfMilitaryAircraft = (Integer) replacementValues.getOrDefault("numberOfMilitaryAircraft", 3 * (index + 2));
            airport.numberOfUltralightAircraft = (Integer) replacementValues.getOrDefault("numberOfUltralightAircraft", 3 * (index + 3));
        }

        return airport;
    }

    /**
     * This method generates the provided amount of
     * {@link Airport} instances.  They will automatically
     * have the extension values filled in.
     *
     * @param count the number of {@link Airport} instances to generate
     * @return The generated {@link Airport} instances
     */
    public static Airport[] generateAirports(int count) {

        return generateAirports(count, new HashMap<>());
    }

    /**
     * This method generates the provided amount of
     * {@link Airport} instances.  They will automatically
     * have the extension values filled in.
     * <p>
     * The {@link Map} provided to this method can be used
     * to replace values for fields on generated airports.
     * <p>
     * The key for the map should be the field name, and the value
     * should be an array of values that has {@code count} number
     * of values.
     *
     * @param count             the number of {@link Airport} instances to generate
     * @param replacementValues the map of replacement values
     * @return The generated {@link Airport} instances
     */
    public static Airport[] generateAirports(int count, Map<String, Object[]> replacementValues) {

        Airport[] airports = new Airport[count];

        for (int airportIndex = 0; airportIndex < count; airportIndex++) {

            // Build map of replacement values for this specific airport
            Map<String, Object> singleReplacementValues = getIndexReplacementValues(replacementValues, airportIndex);

            // Generate the airport
            airports[airportIndex] = generateAirport(airportIndex + 1, true, singleReplacementValues);
        }

        return airports;
    }

    /**
     * This method generates a new {@link Runway} instance
     * using the specified index as an ID, and the specified
     * {@link Airport} as a parent.
     *
     * @param airport the parent {@link Airport}
     * @param index   the ID of the runway (should be unique within
     *                an airport)
     * @return The generated {@link Runway} instance
     */
    public static Runway generateRunway(Airport airport, int index) {

        return generateRunway(airport, index, new HashMap<>());
    }

    /**
     * This method generates a new {@link Runway} instance
     * using the specified index as an ID, and the specified
     * {@link Airport} as a parent.
     * <p>
     * The {@link Map} passed to this method is used to replace the data
     * that is automatically generated.  For example, if different runway
     * designations are needed for testing, the map can be used to force a specific
     * value.  If no replacement value is provided, the generated default
     * will be used.
     *
     * @param airport           the parent {@link Airport}
     * @param index             the ID of the runway (should be unique within
     *                          an airport)
     * @param replacementValues the map of replacement values
     * @return The generated {@link Runway} instance
     */
    public static Runway generateRunway(Airport airport, int index, Map<String, Object> replacementValues) {

        Runway runway = new Runway();
        runway.id = String.format("%s_%d", airport.id.replace("AH", "RWY"), index);
        runway.airportId = airport.id;
        runway.designator = (String) replacementValues.getOrDefault("designator", String.format("%02d/%02d", index % 36, (index + 18) % 36));
        runway.length = (Double) replacementValues.getOrDefault("length", 1000d * (index + 5));
        runway.width = (Double) replacementValues.getOrDefault("width", 100d * (index + 2));

        return runway;
    }

    /**
     * This method generates the provided amount of
     * {@link Runway} instances for each airport.
     * <p>
     * Each airport provided will have {@code count} number
     * of runways generated, so the final array of runways
     * will have {@code airports.length * count} values.
     *
     * @param airports the parent {@link Airport} instances
     * @param count    the number of {@link Runway} instances to generate
     * @return The generated {@link Runway} instances
     */
    public static Runway[] generateRunways(Airport[] airports, int count) {

        return generateRunways(airports, count, new HashMap<>());
    }

    /**
     * This method generates the provided amount of
     * {@link Runway} instances for each airport.
     * <p>
     * Each airport provided will have {@code count} number
     * of runways generated, so the final array of runways
     * will have {@code airports.length * count} values.
     * <p>
     * The {@link Map} provided to this method can be used
     * to replace values for fields on generated runways.
     * <p>
     * The key for the map should be the field name, and the value
     * should be an array of values that has
     * {@code airports.length * count} number of values.
     *
     * @param airports          the parent {@link Airport} instances
     * @param count             the number of {@link Runway} instances to generate
     * @param replacementValues the map of replacement values
     * @return The generated {@link Runway} instances
     */
    public static Runway[] generateRunways(Airport[] airports, int count, Map<String, Object[]> replacementValues) {

        Runway[] runways = new Runway[airports.length * count];

        for (int airportIndex = 0; airportIndex < airports.length; airportIndex++) {

            for (int runwayIndex = 0; runwayIndex < count; runwayIndex++) {

                int index = (airportIndex * count) + runwayIndex;

                // Build map of replacement values for this specific runway
                Map<String, Object> singleReplacementValues = getIndexReplacementValues(replacementValues, index);

                // Generate the runway
                runways[index] = generateRunway(airports[airportIndex], runwayIndex + 1, singleReplacementValues);
            }
        }

        return runways;
    }

    /**
     * This method generates a new {@link RunwayEnd} instance
     * using the specified {@link Runway} to get information
     * about the runway end.
     * <p>
     * The {@code base} parameter specifies if the end is the
     * base end or the reciprocal end.
     *
     * @param runway the {@link Runway} this end is part of
     * @param base   {@code true} if this end is the base end,
     *               {@code false} if it is the reciprocal end
     * @return The generated {@link RunwayEnd} instance
     */
    public static RunwayEnd generateRunwayEnd(Runway runway, boolean base) {

        return generateRunwayEnd(runway, base, new HashMap<>());
    }

    /**
     * This method generates a new {@link RunwayEnd} instance
     * using the specified {@link Runway} to get information
     * about the runway end.
     * <p>
     * The {@code base} parameter specifies if the end is the
     * base end or the reciprocal end.
     * <p>
     * The {@link Map} passed to this method is used to replace the data
     * that is automatically generated.  For example, if different runway
     * end designations are needed for testing, the map can be used to
     * force a specific value.  If no replacement value is provided, the
     * generated default will be used.
     *
     * @param runway            the {@link Runway} this end is part of
     * @param base              {@code true} if this end is the base end,
     *                          {@code false} if it is the reciprocal end
     * @param replacementValues the map of replacement values
     * @return The generated {@link RunwayEnd} instance
     */
    public static RunwayEnd generateRunwayEnd(Runway runway, boolean base, Map<String, Object> replacementValues) {

        RunwayEnd runwayEnd = new RunwayEnd();
        runwayEnd.id = runway.id.replace("RWY", base ? "RWY_BASE_END" : "RWY_RECIPROCAL_END");
        runwayEnd.airportId = runway.airportId;

        // Split designator on "/" if possible (07/25 becomes 07, 25) and split into base/reciprocal parts
        String[] designatorSections = runway.designator != null ? runway.designator.split("/") : new String[]{"00"};
        String defaultDesignatorBase = designatorSections[0];
        String defaultDesignatorReciprocal = designatorSections.length > 1 ? designatorSections[1] : designatorSections[0];
        runwayEnd.designator = (String) replacementValues.getOrDefault("designator", base ? defaultDesignatorBase : defaultDesignatorReciprocal);

        return runwayEnd;
    }

    /**
     * This method generates {@link RunwayEnd} instances
     * for an array of {@link Runway} instances.
     * <p>
     * If a runway designation contains two numbers (like
     * {@code 05/23}, then two ends will be created ({@code 05}
     * and {@code 23}).  If the runway designation contains only
     * one number (like {@code 05}) then only one end will be created.
     *
     * @param runways the {@link Runway} instances
     * @return The generated {@link RunwayEnd} instances
     */
    public static RunwayEnd[] generateRunwayEnds(Runway[] runways) {

        return generateRunwayEnds(runways, new HashMap<>());
    }

    /**
     * This method generates {@link RunwayEnd} instances
     * for an array of {@link Runway} instances.
     * <p>
     * If a runway designation contains two numbers (like
     * {@code 05/23}, then two ends will be created ({@code 05}
     * and {@code 23}).  If the runway designation contains only
     * one number (like {@code 05}) then only one end will be created.
     * <p>
     * The {@link Map} provided to this method can be used
     * to replace values for fields on generated runway ends.
     * <p>
     * The key for the map should be the field name, and the value
     * should be an array of values that has
     * {@code runways.length * 2} number of values.  The data for a
     * specific runway end should be located at {@code runwayIndex * 2}
     * for the base end and {@code runwayIndex * 2 + 1} for reciprocal ends,
     * regardless of whether or not certain runways have reciprocal ends.
     * <p>
     * For example, this is what the runway end designation array
     * for the following runways would look like:
     *
     * <pre>
     *     05/23
     *     10
     *     18/36
     * </pre>
     *
     * <pre>
     *     ["05", "23", "10", null, "18", "36"]
     * </pre>
     *
     * @param runways           the {@link Runway} instances
     * @param replacementValues the map of replacement values
     * @return The generated {@link RunwayEnd} instances
     */
    public static RunwayEnd[] generateRunwayEnds(Runway[] runways, Map<String, Object[]> replacementValues) {

        // Since runways can have 1 or 2 designators, we can't use a basic array
        List<RunwayEnd> runwayEnds = new ArrayList<>();

        for (int runwayIndex = 0; runwayIndex < runways.length; runwayIndex++) {

            // Check to see if runway has reciprocal end
            Runway runway = runways[runwayIndex];
            boolean hasReciprocal = runways[runwayIndex].designator != null && runways[runwayIndex].designator.contains("/");
            if (hasReciprocal) {

                // Runway does have reciprocal end, so we only generate both
                int index = runwayIndex * 2;

                // Build map of replacement values for this specific runway end (increment index for reciprocal)
                Map<String, Object> singleReplacementValues = getIndexReplacementValues(replacementValues, index++);

                // Generate the base runway end
                runwayEnds.add(generateRunwayEnd(runway, true, singleReplacementValues));

                // Build map of replacement values for this specific runway end
                singleReplacementValues = getIndexReplacementValues(replacementValues, index);

                // Generate the reciprocal runway end
                runwayEnds.add(generateRunwayEnd(runway, false, singleReplacementValues));

            } else {

                // Runway does not have reciprocal end, so we only generate the base
                int index = runwayIndex * 2;

                // Build map of replacement values for this specific runway end
                Map<String, Object> singleReplacementValues = getIndexReplacementValues(replacementValues, index);

                // Generate the runway end
                runwayEnds.add(generateRunwayEnd(runway, true, singleReplacementValues));
            }
        }

        return runwayEnds.toArray(new RunwayEnd[0]);
    }

    /**
     * This method generates a new {@link RunwayDirection} instance
     * for the specified {@link RunwayEnd}.
     * <p>
     * This method also automatically fills the extension values
     * for the runway direction.
     *
     * @param runwayEnd the {@link RunwayEnd} to generate a {@link RunwayDirection} for
     * @return The generated {@link RunwayDirection} instance
     */
    public static RunwayDirection generateRunwayDirection(RunwayEnd runwayEnd) {

        return generateRunwayDirection(runwayEnd, true);
    }

    /**
     * This method generates a new {@link RunwayDirection} instance
     * for the specified {@link RunwayEnd}.
     *
     * @param runwayEnd the {@link RunwayEnd} to generate a {@link RunwayDirection} for
     * @param extension {@code true} to fill in the extension data,
     *                  {@code false} to leave it {@code null}
     * @return The generated {@link RunwayDirection} instance
     */
    public static RunwayDirection generateRunwayDirection(RunwayEnd runwayEnd, boolean extension) {

        return generateRunwayDirection(runwayEnd, extension, new HashMap<>());
    }

    /**
     * This method generates a new {@link RunwayDirection} instance
     * for the specified {@link RunwayEnd}.
     * <p>
     * The {@link Map} passed to this method is used to replace the data
     * that is automatically generated.  For example, if different runway
     * direction location data is needed for testing, the map can be used
     * to force specific values.  If no replacement value is provided, the
     * generated default will be used.
     *
     * @param runwayEnd         the {@link RunwayEnd} to generate a {@link RunwayDirection} for
     * @param extension         {@code true} to fill in the extension data,
     *                          {@code false} to leave it {@code null}
     * @param replacementValues the map of replacement values
     * @return The generated {@link RunwayDirection} instance
     */
    public static RunwayDirection generateRunwayDirection(RunwayEnd runwayEnd, boolean extension, Map<String, Object> replacementValues) {

        RunwayDirection runwayDirection = new RunwayDirection();
        runwayDirection.id = runwayEnd.id.replace("RWY", "RWY_DIRECTION");
        runwayDirection.runwayEndId = runwayEnd.id;

        if (extension) {

            runwayDirection.longitude = (Double) replacementValues.getOrDefault("longitude", -170.55);
            runwayDirection.latitude = (Double) replacementValues.getOrDefault("latitude", 60.2);
        }

        return runwayDirection;
    }

    /**
     * This method generates {@link RunwayDirection} instances
     * for each of the provided {@link RunwayEnd} instances.
     * <p>
     * The resulting array will contain {@code runwayEnds.length} values.
     *
     * @param runwayEnds the {@link RunwayEnd} instances to generate
     *                   {@link RunwayDirection} instances for
     * @return The generated {@link RunwayDirection} instances
     */
    public static RunwayDirection[] generateRunwayDirections(RunwayEnd[] runwayEnds) {

        return generateRunwayDirections(runwayEnds, new HashMap<>());
    }

    /**
     * This method generates {@link RunwayDirection} instances
     * for each of the provided {@link RunwayEnd} instances.
     * <p>
     * The resulting array will contain {@code runwayEnds.length} values.
     * <p>
     * The {@link Map} provided to this method can be used
     * to replace values for fields on generated runway directions.
     * <p>
     * The key for the map should be the field name, and the value
     * should be an array of values that has {@code runwayEnds.length}
     * number of values.
     *
     * @param runwayEnds        the {@link RunwayEnd} instances to generate
     *                          {@link RunwayDirection} instances for
     * @param replacementValues the map of replacement values
     * @return The generated {@link RunwayDirection} instances
     */
    public static RunwayDirection[] generateRunwayDirections(RunwayEnd[] runwayEnds, Map<String, Object[]> replacementValues) {

        RunwayDirection[] runwayDirections = new RunwayDirection[runwayEnds.length];

        for (int runwayEndIndex = 0; runwayEndIndex < runwayEnds.length; runwayEndIndex++) {

            // Build map of replacement values for this specific runway direction
            Map<String, Object> singleReplacementValues = getIndexReplacementValues(replacementValues, runwayEndIndex);

            // Generate the runway direction
            runwayDirections[runwayEndIndex] = generateRunwayDirection(runwayEnds[runwayEndIndex], true, singleReplacementValues);
        }

        return runwayDirections;
    }

    private static Map<String, Object> getIndexReplacementValues(Map<String, Object[]> replacementValues, int index) {

        Map<String, Object> singleReplacementValues = new HashMap<>();
        for (String key : replacementValues.keySet())
            singleReplacementValues.put(key, replacementValues.get(key)[index]);

        return singleReplacementValues;
    }
}
