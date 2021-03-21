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
package com.github.chrisblutz.jetway.testing.utils;

import com.github.chrisblutz.jetway.features.*;

import static org.junit.Assert.*;

/**
 * This class provides utility methods for running
 * JUnit assertions on {@link Feature} instances.
 *
 * @author Christopher Lutz
 */
public class JetwayAssertions {

    /**
     * This method checks that the given features match the expected features
     * as specified by the indices given.
     * <p>
     * For example, let {@code values} be {@code [0, 2, 8]}, {@code expected}'
     * be {@code [0, 2, 5, 8, 13]}, and {@code expectedIndices} by {@link [0, 1, 3]}.
     * <p>
     * The actual expected features are those selected by the {@code expectedIndices} array.
     * So for the example that would be {@code [0, 2, 8]}, since those are the values
     * at the indices in {@code expectedIndices}.  Since the given {@code values} array
     * matches our expected values, this assertion would succeed.
     *
     * @param values          the actual features
     * @param expected        the expected features
     * @param expectedIndices the indices of features to check for
     * @param <T>             the feature type
     */
    public static <T extends Feature> void assertFeaturesOrdered(T[] values, T[] expected, int... expectedIndices) {

        assertNotNull(values);
        assertEquals(expectedIndices.length, values.length);

        // Check that each index matches its expected value
        for (int i = 0; i < expectedIndices.length; i++) {

            assertFeature(values[i], expected[expectedIndices[i]]);
        }
    }

    /**
     * This method checks that the given features match the expected features
     * as specified by the indices given, regardless of order
     * <p>
     * For example, let {@code values} be {@code [8, 0, 2]}, {@code expected}'
     * be {@code [0, 2, 5, 8, 13]}, and {@code expectedIndices} by {@link [0, 1, 3]}.
     * <p>
     * The actual expected features are those selected by the {@code expectedIndices} array.
     * So for the example that would be {@code [0, 2, 8]}, since those are the values
     * at the indices in {@code expectedIndices}.  Since the values in the given {@code values}
     * array matches our expected values (even though the order does not),
     * this assertion would succeed.
     *
     * @param values          the actual features
     * @param expected        the expected features
     * @param expectedIndices the indices of features to check for
     * @param <T>             the feature type
     */
    public static <T extends Feature> void assertFeatures(T[] values, T[] expected, int... expectedIndices) {

        assertNotNull(values);
        assertEquals(expectedIndices.length, values.length);

        // Go through every feature to compare with expected features
        boolean[] foundArray = new boolean[expectedIndices.length];
        for (T feature : values) {

            for (int i = 0; i < expectedIndices.length; i++) {

                // If feature has already been found, continue
                if (foundArray[i])
                    continue;

                // Check if feature matches
                if (assertFeatureChecked(feature, expected[expectedIndices[i]])) {
                    foundArray[i] = true;
                    break;
                }
            }
        }

        // Check that all expected features were found
        for (boolean found : foundArray)
            assertTrue(found);
    }

    /**
     * This method checks the provided array of features against the validation array,
     * verifying that all features in the validation array that are children of the provided
     * parent are present in the given array.
     * <p>
     * All features in the {@code values} array should be children of the {@code parent} feature.
     * <p>
     * This method checks to make sure that if an expected feature is a child of the {@code parent}
     * feature, it has a matching feature in the {@code values} array.  Otherwise, this method
     * will result in assertion errors.
     *
     * @param values   the actual features
     * @param expected the expected features
     * @param parent   the parent feature to check for
     * @param <T>      the child feature type
     * @param <V>      the parent feature type
     */
    public static <T extends NestedFeature, V extends Feature> void assertFeaturesIfChild(T[] values, T[] expected, V parent) {

        assertNotNull(values);

        boolean[] featureMatched = new boolean[expected.length];
        boolean[] isParent = new boolean[expected.length];

        // Go through all given features
        for (T value : values) {

            // Loop through all expected features, checking for matches
            for (int i = 0; i < expected.length; i++) {

                // If already matched, continue
                if (featureMatched[i])
                    continue;

                // If the expected feature's parent is the provided one, enter the conditional
                T expectedFeature = expected[i];
                if (expectedFeature.getParentId().equals(parent.getId())) {

                    // Set array index to true to indicate that this expected feature has the provided parent
                    isParent[i] = true;

                    // Check if the current feature matches the expected feature
                    if (assertFeatureChecked(value, expectedFeature))
                        featureMatched[i] = true;
                }
            }
        }

        // Check to make sure all child feature matched
        for (int i = 0; i < expected.length; i++) {

            // If feature was a child, check that it matched
            if (isParent[i])
                assertTrue(featureMatched[i]);
        }
    }

    /**
     * This method checks if the given feature is in the
     * provided array of features.
     *
     * @param value    the actual feature
     * @param expected the array of expected features
     * @param <T>      the feature type
     */
    public static <T extends Feature> void assertFeatureOneOf(T value, T[] expected) {

        assertNotNull(value);

        // Go through all expected features until one matches
        boolean found = false;
        for (T feature : expected) {

            // Verify that the expected feature matches the current feature
            if (assertFeatureChecked(value, feature)) {
                found = true;
                break;
            }
        }

        // Check to make sure a feature matched
        assertTrue(found);
    }

    /**
     * This method first checks if the actual feature's ID matches the ID
     * of the expected feature.  If it does, it then runs all of the assertions
     * on that feature using {@link #assertFeature(Feature, Feature)}.
     * <p>
     * If the ID does not match, this method returns {@code false} and no
     * assertions are run.
     *
     * @param value    the actual value
     * @param expected the expected value
     * @param <T>      the feature type
     * @return {@code true} if the features matched, {@code false} otherwise
     */
    public static <T extends Feature> boolean assertFeatureChecked(T value, T expected) {

        // Check that the current feature is the feature being checked for
        if (!value.getId().equals(expected.getId()))
            return false;

        assertFeature(value, expected);
        return true;
    }

    /**
     * This method checks that all internal values
     * in an actual feature match those of the expected feature.
     *
     * @param value    the actual feature
     * @param expected the expected feature
     * @param <T>      the feature type
     */
    public static <T extends Feature> void assertFeature(T value, T expected) {

        if (value instanceof Airport && expected instanceof Airport) {

            Airport airport = (Airport) value;
            Airport expectedAirport = (Airport) expected;

            assertEquals(expectedAirport.id, airport.id);
            assertEquals(expectedAirport.name, airport.name);
            assertEquals(expectedAirport.fieldElevation, airport.fieldElevation);
            assertEquals(expectedAirport.latitude, airport.latitude);
            assertEquals(expectedAirport.longitude, airport.longitude);
            assertEquals(expectedAirport.servedCity, airport.servedCity);
            assertEquals(expectedAirport.iataDesignator, airport.iataDesignator);
            assertEquals(expectedAirport.icao, airport.icao);
            assertEquals(expectedAirport.siteNumber, airport.siteNumber);
            assertEquals(expectedAirport.landArea, airport.landArea);
            assertEquals(expectedAirport.county, airport.county);
            assertEquals(expectedAirport.state, airport.state);
            assertEquals(expectedAirport.privateUseOnly, airport.privateUseOnly);
            assertEquals(expectedAirport.ownership, airport.ownership);
            assertEquals(expectedAirport.numberOfSingleEngineAircraft, airport.numberOfSingleEngineAircraft);
            assertEquals(expectedAirport.numberOfMultiEngineAircraft, airport.numberOfMultiEngineAircraft);
            assertEquals(expectedAirport.numberOfJetEngineAircraft, airport.numberOfJetEngineAircraft);
            assertEquals(expectedAirport.numberOfHelicopters, airport.numberOfHelicopters);
            assertEquals(expectedAirport.numberOfGliders, airport.numberOfGliders);
            assertEquals(expectedAirport.numberOfMilitaryAircraft, airport.numberOfMilitaryAircraft);
            assertEquals(expectedAirport.numberOfUltralightAircraft, airport.numberOfUltralightAircraft);

        } else if (value instanceof Runway && expected instanceof Runway) {

            Runway runway = (Runway) value;
            Runway expectedRunway = (Runway) expected;

            assertEquals(expectedRunway.id, runway.id);
            assertEquals(expectedRunway.airportId, runway.airportId);
            assertEquals(expectedRunway.designator, runway.designator);
            assertEquals(expectedRunway.length, runway.length);
            assertEquals(expectedRunway.width, runway.width);

        } else if (value instanceof RunwayEnd && expected instanceof RunwayEnd) {

            RunwayEnd runwayEnd = (RunwayEnd) value;
            RunwayEnd expectedRunwayEnd = (RunwayEnd) expected;

            assertEquals(expectedRunwayEnd.id, runwayEnd.id);
            assertEquals(expectedRunwayEnd.airportId, runwayEnd.airportId);
            assertEquals(expectedRunwayEnd.designator, runwayEnd.designator);

        } else if (value instanceof RunwayDirection && expected instanceof RunwayDirection) {

            RunwayDirection runwayDirection = (RunwayDirection) value;
            RunwayDirection expectedRunwayDirection = (RunwayDirection) expected;

            assertEquals(expectedRunwayDirection.id, runwayDirection.id);
            assertEquals(expectedRunwayDirection.runwayEndId, runwayDirection.runwayEndId);
            assertEquals(expectedRunwayDirection.latitude, runwayDirection.latitude);
            assertEquals(expectedRunwayDirection.longitude, runwayDirection.longitude);
        }
    }
}
