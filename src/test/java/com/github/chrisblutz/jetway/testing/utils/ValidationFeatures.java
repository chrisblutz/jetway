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

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains feature instances for use
 * in unit testing.  The features can be used
 * with {@link AIXMUtils#createSourceForFeatures(String, Feature...)}
 * to generate AIXM sources for the features.
 *
 * @author Christopher Lutz
 */
@SuppressWarnings("javadoc")
public class ValidationFeatures {

    public static final Airport BASIC_NO_EXTENSION_AIRPORT;
    public static final Airport BASIC_NO_EXTENSION_AIRPORT_OTHER;
    public static final Airport BASIC_EXTENSION_AIRPORT;

    public static final Airport[] BASIC_MULTIPLE_AIRPORTS;
    public static final int[] BASIC_MULTIPLE_AIRPORT_INDICES;

    public static final Airport[] NESTED_AIRPORTS;
    public static final int[] NESTED_AIRPORT_INDICES;
    public static final Runway[] NESTED_RUNWAYS;
    public static final int[] NESTED_RUNWAY_INDICES;
    public static final RunwayEnd[] NESTED_RUNWAY_ENDS;
    public static final int[] NESTED_RUNWAY_END_INDICES;
    public static final RunwayDirection[] NESTED_RUNWAY_DIRECTIONS;
    public static final int[] NESTED_RUNWAY_DIRECTION_INDICES;

    public static final Airport EAGER_AIRPORT;
    public static final Runway EAGER_RUNWAY_1, EAGER_RUNWAY_2;

    public static final Airport[] QUERY_AIRPORTS;
    public static final int[] QUERY_AIRPORT_ALL_INDICES;
    public static final int[] QUERY_AIRPORT_EQUALS_INDICES;
    public static final int[] QUERY_AIRPORT_NOT_EQUALS_INDICES;
    public static final int[] QUERY_AIRPORT_GREATER_THAN_INDICES;
    public static final int[] QUERY_AIRPORT_GREATER_THAN_EQUALS_INDICES;
    public static final int[] QUERY_AIRPORT_LESS_THAN_INDICES;
    public static final int[] QUERY_AIRPORT_LESS_THAN_EQUALS_INDICES;
    public static final int[] QUERY_AIRPORT_LIKE_INDICES;
    public static final int[] QUERY_AIRPORT_AND_INDICES;
    public static final int[] QUERY_AIRPORT_OR_INDICES;
    public static final int[] QUERY_AIRPORT_AND_AND_INDICES;
    public static final int[] QUERY_AIRPORT_AND_OR_INDICES;
    public static final int[] QUERY_AIRPORT_OR_AND_INDICES;
    public static final int[] QUERY_AIRPORT_OR_OR_INDICES;

    public static final Airport[] QUERY_NESTED_AIRPORTS;
    public static final int[] QUERY_NESTED_AIRPORT_ALL_INDICES;
    public static final Runway[] QUERY_NESTED_RUNWAYS;
    public static final int[] QUERY_NESTED_RUNWAY_ALL_INDICES;
    public static final int[] QUERY_NESTED_RUNWAY_GREATER_THAN_EQUALS_INDICES;
    public static final RunwayEnd[] QUERY_NESTED_RUNWAY_ENDS;
    public static final int[] QUERY_NESTED_RUNWAY_END_ALL_INDICES;
    public static final int[] QUERY_NESTED_RUNWAY_END_EQUALS_INDICES;
    public static final RunwayDirection[] QUERY_NESTED_RUNWAY_DIRECTIONS;
    public static final int[] QUERY_NESTED_RUNWAY_DIRECTION_ALL_INDICES;

    public static final Airport[] SORT_AIRPORTS;
    public static final int[] SORT_AIRPORT_ALL_INDICES;
    public static final int[] SORT_AIRPORT_STRING_ASC_INDICES;
    public static final int[] SORT_AIRPORT_STRING_DESC_INDICES;
    public static final int[] SORT_AIRPORT_NUMERIC_ASC_INDICES;
    public static final int[] SORT_AIRPORT_NUMERIC_DESC_INDICES;
    public static final int[] SORT_AIRPORT_QUERY_STRING_ASC_INDICES;
    public static final int[] SORT_AIRPORT_QUERY_STRING_DESC_INDICES;
    public static final int[] SORT_AIRPORT_QUERY_NUMERIC_ASC_INDICES;
    public static final int[] SORT_AIRPORT_QUERY_NUMERIC_DESC_INDICES;

    public static final Airport[] SORT_NESTED_AIRPORTS;
    public static final int[] SORT_NESTED_AIRPORT_ALL_INDICES;
    public static final Runway[] SORT_NESTED_RUNWAYS;
    public static final int[] SORT_NESTED_RUNWAY_ALL_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_SORTED_DESC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_QUERY_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_QUERY_SORTED_DESC_INDICES;
    public static final RunwayEnd[] SORT_NESTED_RUNWAY_ENDS;
    public static final int[] SORT_NESTED_RUNWAY_END_ALL_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_END_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_END_SORTED_DESC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_END_QUERY_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_END_QUERY_SORTED_DESC_INDICES;
    public static final RunwayDirection[] SORT_NESTED_RUNWAY_DIRECTIONS;
    public static final int[] SORT_NESTED_RUNWAY_DIRECTION_ALL_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_DIRECTION_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_DIRECTION_SORTED_DESC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_ASC_INDICES;
    public static final int[] SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_DESC_INDICES;

    static {

        // ----------------
        //  BASIC FEATURES
        // ----------------

        BASIC_NO_EXTENSION_AIRPORT = FeatureUtils.generateAirport(1, false);
        BASIC_NO_EXTENSION_AIRPORT_OTHER = FeatureUtils.generateAirport(2, false);
        BASIC_EXTENSION_AIRPORT = FeatureUtils.generateAirport(1);

        BASIC_MULTIPLE_AIRPORTS = FeatureUtils.generateAirports(2);
        BASIC_MULTIPLE_AIRPORT_INDICES = new int[]{0, 1};

        // -----------------------
        //  BASIC NESTED FEATURES
        // -----------------------

        NESTED_AIRPORTS = FeatureUtils.generateAirports(2);
        NESTED_AIRPORT_INDICES = new int[]{0, 1};
        NESTED_RUNWAYS = FeatureUtils.generateRunways(NESTED_AIRPORTS, 2);
        NESTED_RUNWAY_INDICES = new int[]{0, 1, 2, 3};
        NESTED_RUNWAY_ENDS = FeatureUtils.generateRunwayEnds(NESTED_RUNWAYS);
        NESTED_RUNWAY_END_INDICES = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        NESTED_RUNWAY_DIRECTIONS = FeatureUtils.generateRunwayDirections(NESTED_RUNWAY_ENDS);
        NESTED_RUNWAY_DIRECTION_INDICES = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

        // ------------------------
        //  EAGER LOADING FEATURES
        // ------------------------

        EAGER_AIRPORT = FeatureUtils.generateAirport(1);
        EAGER_RUNWAY_1 = FeatureUtils.generateRunway(EAGER_AIRPORT, 1);
        EAGER_RUNWAY_2 = FeatureUtils.generateRunway(EAGER_AIRPORT, 2);

        // ----------------
        //  QUERY FEATURES
        // ----------------

        Map<String, Object[]> queryAirportValues = new HashMap<>();
        queryAirportValues.put("name", new String[]{
                "AIRPORT 1 INTL",
                "AIRPORT 2",
                "AIRPORT 3",
                "AIRPORT 4 INTL",
                "AIRPORT 5"
        });
        queryAirportValues.put("servedCity", new String[]{
                "CITY 1",
                "CITY 2",
                "CITY 1",
                "CITY 2",
                "CITY 3"
        });
        queryAirportValues.put("fieldElevation", new Double[]{
                10d,
                12d,
                14d,
                16d,
                18d
        });
        queryAirportValues.put("latitude", new Double[]{
                42d,
                44d,
                46d,
                48d,
                50d
        });
        queryAirportValues.put("longitude", new Double[]{
                -155d,
                -160d,
                -165d,
                -170d,
                -175d
        });

        QUERY_AIRPORTS = FeatureUtils.generateAirports(5, queryAirportValues);
        QUERY_AIRPORT_ALL_INDICES = new int[]{0, 1, 2, 3, 4};
        // Query airports where "servedCity" = "City 1"
        QUERY_AIRPORT_EQUALS_INDICES = new int[]{0, 2};
        // Query airports where "servedCity" != "City 1"
        QUERY_AIRPORT_NOT_EQUALS_INDICES = new int[]{1, 3, 4};
        // Query airports where "fieldElevation" > 14
        QUERY_AIRPORT_GREATER_THAN_INDICES = new int[]{3, 4};
        // Query airports where "fieldElevation" >= 14
        QUERY_AIRPORT_GREATER_THAN_EQUALS_INDICES = new int[]{2, 3, 4};
        // Query airports where "fieldElevation" < 14
        QUERY_AIRPORT_LESS_THAN_INDICES = new int[]{0, 1};
        // Query airports where "fieldElevation" <= 14
        QUERY_AIRPORT_LESS_THAN_EQUALS_INDICES = new int[]{0, 1, 2};
        // Query airports where "name" like "%INTL"
        QUERY_AIRPORT_LIKE_INDICES = new int[]{0, 3};
        // Query airports where "fieldElevation" > 12 and "latitude" < 50
        QUERY_AIRPORT_AND_INDICES = new int[]{2, 3};
        // Query airports where "fieldElevation" < 12 and "latitude" > 46
        QUERY_AIRPORT_OR_INDICES = new int[]{0, 3, 4};
        // Query airports where ("fieldElevation" >= 12 and "latitude" < 50) and "longitude" > -170
        QUERY_AIRPORT_AND_AND_INDICES = new int[]{1, 2};
        // Query airports where ("fieldElevation" > 12 and "latitude" < 50) or "longitude" > -160
        QUERY_AIRPORT_AND_OR_INDICES = new int[]{0, 2, 3};
        // Query airports where ("fieldElevation" < 12 or "latitude" > 44) and "servedCity" = "City 1"
        QUERY_AIRPORT_OR_AND_INDICES = new int[]{0, 2};
        // Query airports where ("fieldElevation" < 12 or "latitude" > 48) or "servedCity" = "City 1"
        QUERY_AIRPORT_OR_OR_INDICES = new int[]{0, 2, 4};

        // -----------------------
        //  NESTED QUERY FEATURES
        // -----------------------

        QUERY_NESTED_AIRPORTS = FeatureUtils.generateAirports(1);
        QUERY_NESTED_AIRPORT_ALL_INDICES = new int[]{0};

        Map<String, Object[]> queryNestedRunwayValues = new HashMap<>();
        queryNestedRunwayValues.put("designator", new String[]{
                "05/23",
                "10",
                "15/33"
        });
        queryNestedRunwayValues.put("length", new Double[]{
                15000d,
                10000d,
                8000d
        });

        QUERY_NESTED_RUNWAYS = FeatureUtils.generateRunways(QUERY_NESTED_AIRPORTS, 3, queryNestedRunwayValues);
        QUERY_NESTED_RUNWAY_ALL_INDICES = new int[]{0, 1, 2};
        // Query nested runways where "length" >= 10000
        QUERY_NESTED_RUNWAY_GREATER_THAN_EQUALS_INDICES = new int[]{0, 1};

        QUERY_NESTED_RUNWAY_ENDS = FeatureUtils.generateRunwayEnds(QUERY_NESTED_RUNWAYS);
        QUERY_NESTED_RUNWAY_END_ALL_INDICES = new int[]{0, 1, 2, 3, 4};
        // Query nested runway ends where "designator" = "05"
        QUERY_NESTED_RUNWAY_END_EQUALS_INDICES = new int[]{0};

        Map<String, Object[]> queryNestedRunwayDirectionValues = new HashMap<>();
        queryNestedRunwayDirectionValues.put("latitude", new Double[]{
                55d, 60d,
                70d, null,
                50d, 40d
        });

        QUERY_NESTED_RUNWAY_DIRECTIONS = FeatureUtils.generateRunwayDirections(QUERY_NESTED_RUNWAY_ENDS, queryNestedRunwayDirectionValues);
        QUERY_NESTED_RUNWAY_DIRECTION_ALL_INDICES = new int[]{0, 1, 2, 3, 4};

        // ------------------
        //  SORTING FEATURES
        // ------------------

        Map<String, Object[]> sortAirportValues = new HashMap<>();
        sortAirportValues.put("name", new String[]{
                "AIRPORT C",
                "AIRPORT A",
                "AIRPORT F",
                "AIRPORT Z",
                "AIRPORT H"
        });
        sortAirportValues.put("servedCity", new String[]{
                "CITY 1",
                "CITY 2",
                "CITY 1",
                "CITY 2",
                "CITY 3"
        });
        sortAirportValues.put("fieldElevation", new Double[]{
                10d,
                12d,
                14d,
                16d,
                18d
        });
        sortAirportValues.put("latitude", new Double[]{
                60d,
                48d,
                46d,
                52d,
                50d
        });

        SORT_AIRPORTS = FeatureUtils.generateAirports(5, sortAirportValues);
        SORT_AIRPORT_ALL_INDICES = new int[]{0, 1, 2, 3, 4};
        // Sort airports by "name" ascending
        SORT_AIRPORT_STRING_ASC_INDICES = new int[]{1, 0, 2, 4, 3};
        // Sort airports by "name" descending
        SORT_AIRPORT_STRING_DESC_INDICES = new int[]{3, 4, 2, 0, 1};
        // Sort airports by "latitude" ascending
        SORT_AIRPORT_NUMERIC_ASC_INDICES = new int[]{2, 1, 4, 3, 0};
        // Sort airports by "latitude" descending
        SORT_AIRPORT_NUMERIC_DESC_INDICES = new int[]{0, 3, 4, 1, 2};
        // Sort airports by "name" ascending where "fieldElevation" > 10
        SORT_AIRPORT_QUERY_STRING_ASC_INDICES = new int[]{1, 2, 4, 3};
        // Sort airports by "name" descending where "fieldElevation" > 10
        SORT_AIRPORT_QUERY_STRING_DESC_INDICES = new int[]{3, 4, 2, 1};
        // Sort airports by "latitude" ascending where "fieldElevation" <= 16
        SORT_AIRPORT_QUERY_NUMERIC_ASC_INDICES = new int[]{2, 1, 3, 0};
        // Sort airports by "latitude" descending where "fieldElevation" <= 16
        SORT_AIRPORT_QUERY_NUMERIC_DESC_INDICES = new int[]{0, 3, 1, 2};

        // -------------------------
        //  NESTED SORTING FEATURES
        // -------------------------

        SORT_NESTED_AIRPORTS = FeatureUtils.generateAirports(3);
        SORT_NESTED_AIRPORT_ALL_INDICES = new int[]{0, 1, 2};

        Map<String, Object[]> sortNestedRunwayValues = new HashMap<>();
        sortNestedRunwayValues.put("designator", new String[]{
                "05/23",
                "10/28",
                "06/24",
                "11/29",
                "07/25",
                "12/30"
        });
        sortNestedRunwayValues.put("length", new Double[]{
                8500d,
                15000d,
                7500d,
                16000d,
                6500d,
                17000d
        });

        SORT_NESTED_RUNWAYS = FeatureUtils.generateRunways(SORT_NESTED_AIRPORTS, 2, sortNestedRunwayValues);
        SORT_NESTED_RUNWAY_ALL_INDICES = new int[]{0, 1, 2, 3, 4, 5};
        // Sort nested runways by "length" ascending
        SORT_NESTED_RUNWAY_SORTED_ASC_INDICES = new int[]{4, 2, 0, 1, 3, 5};
        // Sort nested runways by "length" descending
        SORT_NESTED_RUNWAY_SORTED_DESC_INDICES = new int[]{5, 3, 1, 0, 2, 4};
        // Sort nested runways by "length" ascending where "length" > 10000
        SORT_NESTED_RUNWAY_QUERY_SORTED_ASC_INDICES = new int[]{1, 3, 5};
        // Sort nested runways by "length" descending where "length" > 10000
        SORT_NESTED_RUNWAY_QUERY_SORTED_DESC_INDICES = new int[]{5, 3, 1};

        SORT_NESTED_RUNWAY_ENDS = FeatureUtils.generateRunwayEnds(SORT_NESTED_RUNWAYS);
        SORT_NESTED_RUNWAY_END_ALL_INDICES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        // Sort nested runway ends by "designator" ascending
        SORT_NESTED_RUNWAY_END_SORTED_ASC_INDICES = new int[]{0, 4, 8, 2, 6, 10, 1, 5, 9, 3, 7, 11};
        // Sort nested runway ends by "designator" descending
        SORT_NESTED_RUNWAY_END_SORTED_DESC_INDICES = new int[]{11, 7, 3, 9, 5, 1, 10, 6, 2, 8, 4, 0};
        // Sort nested runway ends by "designator" ascending where "designator" like "0_"
        SORT_NESTED_RUNWAY_END_QUERY_SORTED_ASC_INDICES = new int[]{0, 4, 8};
        // Sort nested runway ends by "designator" descending where "designator" like "0_"
        SORT_NESTED_RUNWAY_END_QUERY_SORTED_DESC_INDICES = new int[]{8, 4, 0};

        Map<String, Object[]> sortNestedRunwayDirectionValues = new HashMap<>();
        sortNestedRunwayDirectionValues.put("latitude", new Double[]{
                50d, 55d,
                60d, 65d,
                40d, 30d,
                70d, 80d,
                20d, 10d,
                90d, 100d
        });

        SORT_NESTED_RUNWAY_DIRECTIONS = FeatureUtils.generateRunwayDirections(SORT_NESTED_RUNWAY_ENDS, sortNestedRunwayDirectionValues);
        SORT_NESTED_RUNWAY_DIRECTION_ALL_INDICES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        // Sort nested runway directions by "latitude" ascending
        SORT_NESTED_RUNWAY_DIRECTION_SORTED_ASC_INDICES = new int[]{9, 8, 5, 4, 0, 1, 2, 3, 6, 7, 10, 11};
        // Sort nested runway directions by "latitude" descending
        SORT_NESTED_RUNWAY_DIRECTION_SORTED_DESC_INDICES = new int[]{11, 10, 7, 6, 3, 2, 1, 0, 4, 5, 8, 9};
        // Sort nested runway directions by "latitude" ascending where "latitude" >= 50
        SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_ASC_INDICES = new int[]{0, 1, 2, 3, 6, 7, 10, 11};
        // Sort nested runway directions by "latitude" descending where "latitude" >= 50
        SORT_NESTED_RUNWAY_DIRECTION_QUERY_SORTED_DESC_INDICES = new int[]{11, 10, 7, 6, 3, 2, 1, 0};
    }
}
