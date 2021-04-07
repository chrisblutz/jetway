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

import com.github.chrisblutz.jetway.features.Airport;
import com.github.chrisblutz.jetway.features.Runway;
import com.github.chrisblutz.jetway.features.RunwayDirection;
import com.github.chrisblutz.jetway.features.RunwayEnd;
import com.github.chrisblutz.jetway.features.fields.Ownership;

/**
 * This class handles arrays used for verification
 * of features in {@link JetwayAssertions} assertions.
 *
 * @author Christopher Lutz
 */
public class ValidationArrays {

    /**
     * Airports in basic_extension.xml
     */
    public static final Airport[] LOAD_EXTENSION_AIRPORTS;
    /**
     * Airports in nested.xml/basic.xml
     */
    public static final Airport[] LOAD_NO_EXTENSION_AIRPORTS;
    /**
     * Runways in nested.xml
     */
    public static final Runway[] LOAD_NO_EXTENSION_RUNWAYS;
    /**
     * Runway ends in nested.xml
     */
    public static final RunwayEnd[] LOAD_NO_EXTENSION_RUNWAY_ENDS;
    /**
     * Runway directions in nested.xml
     */
    public static final RunwayDirection[] LOAD_NO_EXTENSION_RUNWAY_DIRECTIONS;

    /**
     * Airports in basic_multiple.xml
     */
    public static final Airport[] LOAD_MULTIPLE_AIRPORTS;

    /**
     * Airports in rebuild_initial.xml
     */
    public static final Airport[] LOAD_REBUILD_INITIAL_AIRPORTS;
    /**
     * Airports in rebuild_final.xml
     */
    public static final Airport[] LOAD_REBUILD_FINAL_AIRPORTS;

    /**
     * Airports from query_basic.xml
     */
    public static final Airport[] QUERY_BASIC_AIRPORTS;

    /**
     * Airports from query_nested.xml
     */
    public static final Airport[] QUERY_NESTED_AIRPORTS;
    /**
     * Runways from query_nested.xml
     */
    public static final Runway[] QUERY_NESTED_RUNWAYS;
    /**
     * Runway ends from query_nested.xml
     */
    public static final RunwayEnd[] QUERY_NESTED_RUNWAY_ENDS;
    /**
     * Runway directions from query_nested.xml
     */
    public static final RunwayDirection[] QUERY_NESTED_RUNWAY_DIRECTIONS;

    /**
     * Airports from sorting_basic.xml
     */
    public static final Airport[] SORTING_BASIC_AIRPORTS;

    /**
     * Airports from sorting_nested.xml
     */
    public static final Airport[] SORTING_NESTED_AIRPORTS;
    /**
     * Runways from sorting_nested.xml
     */
    public static final Runway[] SORTING_NESTED_RUNWAYS;
    /**
     * Runway ends from sorting_nested.xml
     */
    public static final RunwayEnd[] SORTING_NESTED_RUNWAY_ENDS;
    /**
     * Runway directions from sorting_nested.xml
     */
    public static final RunwayDirection[] SORTING_NESTED_RUNWAY_DIRECTIONS;
    /**
     * Airport used during eager loading testing
     */
    public static final Airport EAGER_LOADING_AIRPORT;
    /**
     * Runways used during eager loading testing
     */
    public static final Runway[] EAGER_LOADING_RUNWAYS;

    static {

        // =====================================================
        // * * * * *    LOAD EXTENSION/NO EXTENSION    * * * * *
        // =====================================================

        Airport loadExtensionAirport = new Airport();
        loadExtensionAirport.id = "AH_0000001";
        loadExtensionAirport.name = "TEST AIRPORT";
        loadExtensionAirport.fieldElevation = 19.5;
        loadExtensionAirport.latitude = 50.5678;
        loadExtensionAirport.longitude = -170.1234;
        loadExtensionAirport.servedCity = "TEST CITY";
        loadExtensionAirport.iataDesignator = "TST";
        loadExtensionAirport.icao = "KTST";
        loadExtensionAirport.landArea = 234d;
        loadExtensionAirport.siteNumber = "50000.*A";
        loadExtensionAirport.county = "TEST COUNTY";
        loadExtensionAirport.state = "TEST STATE";
        loadExtensionAirport.privateUseOnly = true;
        loadExtensionAirport.ownership = Ownership.PUBLIC;
        loadExtensionAirport.numberOfSingleEngineAircraft = 1;
        loadExtensionAirport.numberOfMultiEngineAircraft = 2;
        loadExtensionAirport.numberOfJetEngineAircraft = 3;
        loadExtensionAirport.numberOfHelicopters = 4;
        loadExtensionAirport.numberOfGliders = 5;
        loadExtensionAirport.numberOfMilitaryAircraft = 6;
        loadExtensionAirport.numberOfUltralightAircraft = 7;
        LOAD_EXTENSION_AIRPORTS = new Airport[]{loadExtensionAirport};

        Airport loadNoExtensionAirport = new Airport();
        loadNoExtensionAirport.id = "AH_0000001";
        loadNoExtensionAirport.name = "TEST AIRPORT";
        loadNoExtensionAirport.fieldElevation = 19.5;
        loadNoExtensionAirport.latitude = 50.5678;
        loadNoExtensionAirport.longitude = -170.1234;
        loadNoExtensionAirport.servedCity = "TEST CITY";
        loadNoExtensionAirport.privateUseOnly = false;
        loadNoExtensionAirport.iataDesignator = "TST";
        loadNoExtensionAirport.icao = "KTST";
        LOAD_NO_EXTENSION_AIRPORTS = new Airport[]{loadNoExtensionAirport};

        Runway loadNoExtensionRunwayA1 = new Runway();
        loadNoExtensionRunwayA1.id = "RWY_0000001_1";
        loadNoExtensionRunwayA1.airportId = loadNoExtensionAirport.id;
        loadNoExtensionRunwayA1.designator = "05/23";
        loadNoExtensionRunwayA1.length = 8525d;
        loadNoExtensionRunwayA1.width = 200d;
        LOAD_NO_EXTENSION_RUNWAYS = new Runway[]{loadNoExtensionRunwayA1};

        RunwayEnd loadNoExtensionRunwayEndBaseR1A1 = new RunwayEnd();
        loadNoExtensionRunwayEndBaseR1A1.id = "RWY_BASE_END_0000001_1";
        loadNoExtensionRunwayEndBaseR1A1.airportId = loadNoExtensionAirport.id;
        loadNoExtensionRunwayEndBaseR1A1.designator = "05";

        RunwayEnd loadNoExtensionRunwayEndReciprocalR1A1 = new RunwayEnd();
        loadNoExtensionRunwayEndReciprocalR1A1.id = "RWY_RECIPROCAL_END_0000001_1";
        loadNoExtensionRunwayEndReciprocalR1A1.airportId = loadNoExtensionAirport.id;
        loadNoExtensionRunwayEndReciprocalR1A1.designator = "23";

        LOAD_NO_EXTENSION_RUNWAY_ENDS = new RunwayEnd[]{loadNoExtensionRunwayEndBaseR1A1,
                loadNoExtensionRunwayEndReciprocalR1A1};

        RunwayDirection loadNoExtensionRunwayDirectionBaseR1A1 = new RunwayDirection();
        loadNoExtensionRunwayDirectionBaseR1A1.id = "RWY_DIRECTION_BASE_END_0000001_1";
        loadNoExtensionRunwayDirectionBaseR1A1.runwayEndId = loadNoExtensionRunwayEndBaseR1A1.id;
        loadNoExtensionRunwayDirectionBaseR1A1.latitude = 50.5679;
        loadNoExtensionRunwayDirectionBaseR1A1.longitude = -170.1233;

        RunwayDirection loadNoExtensionRunwayDirectionReciprocalR1A1 = new RunwayDirection();
        loadNoExtensionRunwayDirectionReciprocalR1A1.id = "RWY_DIRECTION_RECIPROCAL_END_0000001_1";
        loadNoExtensionRunwayDirectionReciprocalR1A1.runwayEndId = loadNoExtensionRunwayEndReciprocalR1A1.id;
        loadNoExtensionRunwayDirectionReciprocalR1A1.latitude = 50.9765;
        loadNoExtensionRunwayDirectionReciprocalR1A1.longitude = -170.3211;

        LOAD_NO_EXTENSION_RUNWAY_DIRECTIONS = new RunwayDirection[]{loadNoExtensionRunwayDirectionBaseR1A1,
                loadNoExtensionRunwayDirectionReciprocalR1A1};

        // =====================================================
        // * * * * *           LOAD MULTIPLE           * * * * *
        // =====================================================

        Airport loadMultipleAirport1 = new Airport();
        loadMultipleAirport1.id = "AH_0000001";
        loadMultipleAirport1.name = "TEST AIRPORT 1";
        loadMultipleAirport1.fieldElevation = 19.5;
        loadMultipleAirport1.latitude = 50.5678;
        loadMultipleAirport1.longitude = -170.1234;
        loadMultipleAirport1.servedCity = "TEST CITY 1";
        loadMultipleAirport1.privateUseOnly = false;
        loadMultipleAirport1.iataDesignator = "TST1";
        loadMultipleAirport1.icao = "KTS1";

        Airport loadMultipleAirport2 = new Airport();
        loadMultipleAirport2.id = "AH_0000002";
        loadMultipleAirport2.name = "TEST AIRPORT 2";
        loadMultipleAirport2.fieldElevation = 10d;
        loadMultipleAirport2.latitude = -20.8765;
        loadMultipleAirport2.longitude = 120.4321;
        loadMultipleAirport2.servedCity = "TEST CITY 2";
        loadMultipleAirport2.privateUseOnly = true;
        loadMultipleAirport2.iataDesignator = "TST2";
        loadMultipleAirport2.icao = "KTS2";

        LOAD_MULTIPLE_AIRPORTS = new Airport[]{loadMultipleAirport1, loadMultipleAirport2};

        // =====================================================
        // * * * * *           LOAD REBUILD            * * * * *
        // =====================================================

        Airport loadRebuildInitialAirport = new Airport();
        loadRebuildInitialAirport.id = "AH_0000001";
        loadRebuildInitialAirport.name = "AIRPORT A";
        loadRebuildInitialAirport.fieldElevation = 20d;
        loadRebuildInitialAirport.latitude = 60d;
        loadRebuildInitialAirport.longitude = -170d;
        loadRebuildInitialAirport.servedCity = "TEST CITY";
        loadRebuildInitialAirport.privateUseOnly = false;
        loadRebuildInitialAirport.iataDesignator = "TST";
        loadRebuildInitialAirport.icao = "KTST";
        LOAD_REBUILD_INITIAL_AIRPORTS = new Airport[]{loadRebuildInitialAirport};

        Airport loadRebuildFinalAirport = new Airport();
        loadRebuildFinalAirport.id = "AH_0000001";
        loadRebuildFinalAirport.name = "AIRPORT B";
        loadRebuildFinalAirport.fieldElevation = 10d;
        loadRebuildFinalAirport.latitude = 50d;
        loadRebuildFinalAirport.longitude = -160d;
        loadRebuildFinalAirport.servedCity = "TEST CITY";
        loadRebuildFinalAirport.privateUseOnly = false;
        loadRebuildFinalAirport.iataDesignator = "TST";
        loadRebuildFinalAirport.icao = "KTST";
        LOAD_REBUILD_FINAL_AIRPORTS = new Airport[]{loadRebuildFinalAirport};

        // =====================================================
        // * * * * *            QUERY BASIC            * * * * *
        // =====================================================

        Airport queryBasicAirport1 = new Airport();
        queryBasicAirport1.id = "AH_0000001";
        queryBasicAirport1.name = "FIRST AIRPORT INTL";
        queryBasicAirport1.fieldElevation = 10d;
        queryBasicAirport1.latitude = 42d;
        queryBasicAirport1.longitude = -155d;
        queryBasicAirport1.servedCity = "FIRST CITY";
        queryBasicAirport1.privateUseOnly = false;
        queryBasicAirport1.iataDesignator = "TST";
        queryBasicAirport1.icao = "KTST";

        Airport queryBasicAirport2 = new Airport();
        queryBasicAirport2.id = "AH_0000002";
        queryBasicAirport2.name = "SECOND AIRPORT";
        queryBasicAirport2.fieldElevation = 12d;
        queryBasicAirport2.latitude = 44d;
        queryBasicAirport2.longitude = -160d;
        queryBasicAirport2.servedCity = "SECOND CITY";
        queryBasicAirport2.privateUseOnly = false;
        queryBasicAirport2.iataDesignator = "TST";
        queryBasicAirport2.icao = "KTST";

        Airport queryBasicAirport3 = new Airport();
        queryBasicAirport3.id = "AH_0000003";
        queryBasicAirport3.name = "THIRD AIRPORT";
        queryBasicAirport3.fieldElevation = 14d;
        queryBasicAirport3.latitude = 46d;
        queryBasicAirport3.longitude = -165d;
        queryBasicAirport3.servedCity = "FIRST CITY";
        queryBasicAirport3.privateUseOnly = false;
        queryBasicAirport3.iataDesignator = "TST";
        queryBasicAirport3.icao = "KTST";

        Airport queryBasicAirport4 = new Airport();
        queryBasicAirport4.id = "AH_0000004";
        queryBasicAirport4.name = "FOURTH AIRPORT INTL";
        queryBasicAirport4.fieldElevation = 16d;
        queryBasicAirport4.latitude = 48d;
        queryBasicAirport4.longitude = -170d;
        queryBasicAirport4.servedCity = "SECOND CITY";
        queryBasicAirport4.privateUseOnly = false;
        queryBasicAirport4.iataDesignator = "TST";
        queryBasicAirport4.icao = "KTST";

        Airport queryBasicAirport5 = new Airport();
        queryBasicAirport5.id = "AH_0000005";
        queryBasicAirport5.name = "FIFTH AIRPORT";
        queryBasicAirport5.fieldElevation = 18d;
        queryBasicAirport5.latitude = 50d;
        queryBasicAirport5.longitude = -175d;
        queryBasicAirport5.servedCity = "THIRD CITY";
        queryBasicAirport5.privateUseOnly = false;
        queryBasicAirport5.iataDesignator = "TST";
        queryBasicAirport5.icao = "KTST";

        QUERY_BASIC_AIRPORTS = new Airport[]{queryBasicAirport1, queryBasicAirport2,
                queryBasicAirport3, queryBasicAirport4, queryBasicAirport5};

        // =====================================================
        // * * * * *           QUERY NESTED            * * * * *
        // =====================================================

        Airport queryNestedAirport = new Airport();
        queryNestedAirport.id = "AH_0000001";
        queryNestedAirport.name = "TEST AIRPORT";
        queryNestedAirport.fieldElevation = 10d;
        queryNestedAirport.latitude = 60d;
        queryNestedAirport.longitude = -155d;
        queryNestedAirport.servedCity = "FIRST CITY";
        queryNestedAirport.privateUseOnly = false;
        queryNestedAirport.iataDesignator = "TST";
        queryNestedAirport.icao = "KTST";
        QUERY_NESTED_AIRPORTS = new Airport[]{queryNestedAirport};

        Runway queryNestedRunway1 = new Runway();
        queryNestedRunway1.id = "RWY_0000001_1";
        queryNestedRunway1.airportId = queryNestedAirport.id;
        queryNestedRunway1.designator = "05/23";
        queryNestedRunway1.length = 8525d;
        queryNestedRunway1.width = 200d;

        Runway queryNestedRunway2 = new Runway();
        queryNestedRunway2.id = "RWY_0000001_2";
        queryNestedRunway2.airportId = queryNestedAirport.id;
        queryNestedRunway2.designator = "10/28";
        queryNestedRunway2.length = 15000d;
        queryNestedRunway2.width = 280d;

        Runway queryNestedRunway3 = new Runway();
        queryNestedRunway3.id = "RWY_0000001_3";
        queryNestedRunway3.airportId = queryNestedAirport.id;
        queryNestedRunway3.designator = "15L/33R";
        queryNestedRunway3.length = 10000d;
        queryNestedRunway3.width = 250d;

        QUERY_NESTED_RUNWAYS = new Runway[]{queryNestedRunway1, queryNestedRunway2, queryNestedRunway3};

        RunwayEnd queryNestedRunwayEndBase = new RunwayEnd();
        queryNestedRunwayEndBase.id = "RWY_BASE_END_0000001_1";
        queryNestedRunwayEndBase.airportId = queryNestedAirport.id;
        queryNestedRunwayEndBase.designator = "05";

        RunwayEnd queryNestedRunwayEndReciprocal = new RunwayEnd();
        queryNestedRunwayEndReciprocal.id = "RWY_RECIPROCAL_END_0000001_1";
        queryNestedRunwayEndReciprocal.airportId = queryNestedAirport.id;
        queryNestedRunwayEndReciprocal.designator = "23";

        QUERY_NESTED_RUNWAY_ENDS = new RunwayEnd[]{queryNestedRunwayEndBase, queryNestedRunwayEndReciprocal};

        RunwayDirection queryNestedRunwayDirectionBase = new RunwayDirection();
        queryNestedRunwayDirectionBase.id = "RWY_DIRECTION_BASE_END_0000001_1";
        queryNestedRunwayDirectionBase.runwayEndId = queryNestedRunwayEndBase.id;
        queryNestedRunwayDirectionBase.latitude = 50.5679;
        queryNestedRunwayDirectionBase.longitude = -170.1233;

        QUERY_NESTED_RUNWAY_DIRECTIONS = new RunwayDirection[]{queryNestedRunwayDirectionBase};

        // =====================================================
        // * * * * *           SORTING BASIC           * * * * *
        // =====================================================

        Airport sortingBasicAirport1 = new Airport();
        sortingBasicAirport1.id = "AH_0000001";
        sortingBasicAirport1.name = "AIRPORT C";
        sortingBasicAirport1.fieldElevation = 10d;
        sortingBasicAirport1.latitude = 60d;
        sortingBasicAirport1.longitude = -155d;
        sortingBasicAirport1.servedCity = "FIRST CITY";
        sortingBasicAirport1.privateUseOnly = false;
        sortingBasicAirport1.iataDesignator = "TST";
        sortingBasicAirport1.icao = "KTST";

        Airport sortingBasicAirport2 = new Airport();
        sortingBasicAirport2.id = "AH_0000002";
        sortingBasicAirport2.name = "AIRPORT A";
        sortingBasicAirport2.fieldElevation = 12d;
        sortingBasicAirport2.latitude = 48d;
        sortingBasicAirport2.longitude = -160d;
        sortingBasicAirport2.servedCity = "SECOND CITY";
        sortingBasicAirport2.privateUseOnly = false;
        sortingBasicAirport2.iataDesignator = "TST";
        sortingBasicAirport2.icao = "KTST";

        Airport sortingBasicAirport3 = new Airport();
        sortingBasicAirport3.id = "AH_0000003";
        sortingBasicAirport3.name = "AIRPORT F";
        sortingBasicAirport3.fieldElevation = 14d;
        sortingBasicAirport3.latitude = 46d;
        sortingBasicAirport3.longitude = -165d;
        sortingBasicAirport3.servedCity = "FIRST CITY";
        sortingBasicAirport3.privateUseOnly = false;
        sortingBasicAirport3.iataDesignator = "TST";
        sortingBasicAirport3.icao = "KTST";

        Airport sortingBasicAirport4 = new Airport();
        sortingBasicAirport4.id = "AH_0000004";
        sortingBasicAirport4.name = "AIRPORT Z";
        sortingBasicAirport4.fieldElevation = 16d;
        sortingBasicAirport4.latitude = 52d;
        sortingBasicAirport4.longitude = -170d;
        sortingBasicAirport4.servedCity = "SECOND CITY";
        sortingBasicAirport4.privateUseOnly = false;
        sortingBasicAirport4.iataDesignator = "TST";
        sortingBasicAirport4.icao = "KTST";

        Airport sortingBasicAirport5 = new Airport();
        sortingBasicAirport5.id = "AH_0000005";
        sortingBasicAirport5.name = "AIRPORT H";
        sortingBasicAirport5.fieldElevation = 18d;
        sortingBasicAirport5.latitude = 50d;
        sortingBasicAirport5.longitude = -175d;
        sortingBasicAirport5.servedCity = "THIRD CITY";
        sortingBasicAirport5.privateUseOnly = false;
        sortingBasicAirport5.iataDesignator = "TST";
        sortingBasicAirport5.icao = "KTST";

        SORTING_BASIC_AIRPORTS = new Airport[]{sortingBasicAirport1, sortingBasicAirport2,
                sortingBasicAirport3, sortingBasicAirport4, sortingBasicAirport5};

        // =====================================================
        // * * * * *          SORTING NESTED           * * * * *
        // =====================================================

        Airport sortingNestedAirport1 = new Airport();
        sortingNestedAirport1.id = "AH_0000001";
        sortingNestedAirport1.name = "AIRPORT C";
        sortingNestedAirport1.fieldElevation = 10d;
        sortingNestedAirport1.latitude = 60d;
        sortingNestedAirport1.longitude = -155d;
        sortingNestedAirport1.servedCity = "FIRST CITY";
        sortingNestedAirport1.privateUseOnly = false;
        sortingNestedAirport1.iataDesignator = "TST";
        sortingNestedAirport1.icao = "KTST";

        Airport sortingNestedAirport2 = new Airport();
        sortingNestedAirport2.id = "AH_0000002";
        sortingNestedAirport2.name = "AIRPORT A";
        sortingNestedAirport2.fieldElevation = 12d;
        sortingNestedAirport2.latitude = 48d;
        sortingNestedAirport2.longitude = -160d;
        sortingNestedAirport2.servedCity = "SECOND CITY";
        sortingNestedAirport2.privateUseOnly = false;
        sortingNestedAirport2.iataDesignator = "TST";
        sortingNestedAirport2.icao = "KTST";

        Airport sortingNestedAirport3 = new Airport();
        sortingNestedAirport3.id = "AH_0000003";
        sortingNestedAirport3.name = "AIRPORT F";
        sortingNestedAirport3.fieldElevation = 14d;
        sortingNestedAirport3.latitude = 46d;
        sortingNestedAirport3.longitude = -165d;
        sortingNestedAirport3.servedCity = "FIRST CITY";
        sortingNestedAirport3.privateUseOnly = false;
        sortingNestedAirport3.iataDesignator = "TST";
        sortingNestedAirport3.icao = "KTST";

        SORTING_NESTED_AIRPORTS = new Airport[]{sortingNestedAirport1, sortingNestedAirport2, sortingNestedAirport3};

        Runway sortingNestedRunway1A1 = new Runway();
        sortingNestedRunway1A1.id = "RWY_0000001_1";
        sortingNestedRunway1A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunway1A1.designator = "05/23";
        sortingNestedRunway1A1.length = 8525d;
        sortingNestedRunway1A1.width = 200d;

        Runway sortingNestedRunway2A1 = new Runway();
        sortingNestedRunway2A1.id = "RWY_0000001_2";
        sortingNestedRunway2A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunway2A1.designator = "10/28";
        sortingNestedRunway2A1.length = 15000d;
        sortingNestedRunway2A1.width = 280d;

        Runway sortingNestedRunway1A2 = new Runway();
        sortingNestedRunway1A2.id = "RWY_0000002_1";
        sortingNestedRunway1A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunway1A2.designator = "06/24";
        sortingNestedRunway1A2.length = 7525d;
        sortingNestedRunway1A2.width = 180d;

        Runway sortingNestedRunway2A2 = new Runway();
        sortingNestedRunway2A2.id = "RWY_0000002_2";
        sortingNestedRunway2A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunway2A2.designator = "11/29";
        sortingNestedRunway2A2.length = 16000d;
        sortingNestedRunway2A2.width = 300d;

        Runway sortingNestedRunway1A3 = new Runway();
        sortingNestedRunway1A3.id = "RWY_0000003_1";
        sortingNestedRunway1A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunway1A3.designator = "07/25";
        sortingNestedRunway1A3.length = 6525d;
        sortingNestedRunway1A3.width = 160d;

        Runway sortingNestedRunway2A3 = new Runway();
        sortingNestedRunway2A3.id = "RWY_0000003_2";
        sortingNestedRunway2A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunway2A3.designator = "12/30";
        sortingNestedRunway2A3.length = 17000d;
        sortingNestedRunway2A3.width = 320d;

        SORTING_NESTED_RUNWAYS = new Runway[]{
                sortingNestedRunway1A1, sortingNestedRunway2A1,
                sortingNestedRunway1A2, sortingNestedRunway2A2,
                sortingNestedRunway1A3, sortingNestedRunway2A3
        };

        RunwayEnd sortingNestedRunwayEndBaseR1A1 = new RunwayEnd();
        sortingNestedRunwayEndBaseR1A1.id = "RWY_BASE_END_0000001_1";
        sortingNestedRunwayEndBaseR1A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunwayEndBaseR1A1.designator = "05";

        RunwayEnd sortingNestedRunwayEndReciprocalR1A1 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR1A1.id = "RWY_RECIPROCAL_END_0000001_1";
        sortingNestedRunwayEndReciprocalR1A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunwayEndReciprocalR1A1.designator = "23";

        RunwayEnd sortingNestedRunwayEndBaseR2A1 = new RunwayEnd();
        sortingNestedRunwayEndBaseR2A1.id = "RWY_BASE_END_0000001_2";
        sortingNestedRunwayEndBaseR2A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunwayEndBaseR2A1.designator = "10";

        RunwayEnd sortingNestedRunwayEndReciprocalR2A1 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR2A1.id = "RWY_RECIPROCAL_END_0000001_2";
        sortingNestedRunwayEndReciprocalR2A1.airportId = sortingNestedAirport1.id;
        sortingNestedRunwayEndReciprocalR2A1.designator = "28";

        RunwayEnd sortingNestedRunwayEndBaseR1A2 = new RunwayEnd();
        sortingNestedRunwayEndBaseR1A2.id = "RWY_BASE_END_0000002_1";
        sortingNestedRunwayEndBaseR1A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunwayEndBaseR1A2.designator = "06";

        RunwayEnd sortingNestedRunwayEndReciprocalR1A2 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR1A2.id = "RWY_RECIPROCAL_END_0000002_1";
        sortingNestedRunwayEndReciprocalR1A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunwayEndReciprocalR1A2.designator = "24";

        RunwayEnd sortingNestedRunwayEndBaseR2A2 = new RunwayEnd();
        sortingNestedRunwayEndBaseR2A2.id = "RWY_BASE_END_0000002_2";
        sortingNestedRunwayEndBaseR2A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunwayEndBaseR2A2.designator = "11";

        RunwayEnd sortingNestedRunwayEndReciprocalR2A2 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR2A2.id = "RWY_RECIPROCAL_END_0000002_2";
        sortingNestedRunwayEndReciprocalR2A2.airportId = sortingNestedAirport2.id;
        sortingNestedRunwayEndReciprocalR2A2.designator = "29";

        RunwayEnd sortingNestedRunwayEndBaseR1A3 = new RunwayEnd();
        sortingNestedRunwayEndBaseR1A3.id = "RWY_BASE_END_0000003_1";
        sortingNestedRunwayEndBaseR1A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunwayEndBaseR1A3.designator = "07";

        RunwayEnd sortingNestedRunwayEndReciprocalR1A3 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR1A3.id = "RWY_RECIPROCAL_END_0000003_1";
        sortingNestedRunwayEndReciprocalR1A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunwayEndReciprocalR1A3.designator = "25";

        RunwayEnd sortingNestedRunwayEndBaseR2A3 = new RunwayEnd();
        sortingNestedRunwayEndBaseR2A3.id = "RWY_BASE_END_0000003_2";
        sortingNestedRunwayEndBaseR2A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunwayEndBaseR2A3.designator = "12";

        RunwayEnd sortingNestedRunwayEndReciprocalR2A3 = new RunwayEnd();
        sortingNestedRunwayEndReciprocalR2A3.id = "RWY_RECIPROCAL_END_0000003_2";
        sortingNestedRunwayEndReciprocalR2A3.airportId = sortingNestedAirport3.id;
        sortingNestedRunwayEndReciprocalR2A3.designator = "30";

        SORTING_NESTED_RUNWAY_ENDS = new RunwayEnd[]{
                sortingNestedRunwayEndBaseR1A1, sortingNestedRunwayEndReciprocalR1A1,
                sortingNestedRunwayEndBaseR2A1, sortingNestedRunwayEndReciprocalR2A1,
                sortingNestedRunwayEndBaseR1A2, sortingNestedRunwayEndReciprocalR1A2,
                sortingNestedRunwayEndBaseR2A2, sortingNestedRunwayEndReciprocalR2A2,
                sortingNestedRunwayEndBaseR1A3, sortingNestedRunwayEndReciprocalR1A3,
                sortingNestedRunwayEndBaseR2A3, sortingNestedRunwayEndReciprocalR2A3
        };

        RunwayDirection sortingNestedRunwayDirectionBaseR1A1 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR1A1.id = "RWY_DIRECTION_BASE_END_0000001_1";
        sortingNestedRunwayDirectionBaseR1A1.runwayEndId = sortingNestedRunwayEndBaseR1A1.id;
        sortingNestedRunwayDirectionBaseR1A1.latitude = 50.5679;
        sortingNestedRunwayDirectionBaseR1A1.longitude = -170.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR1A1 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR1A1.id = "RWY_DIRECTION_RECIPROCAL_END_0000001_1";
        sortingNestedRunwayDirectionReciprocalR1A1.runwayEndId = sortingNestedRunwayEndReciprocalR1A1.id;
        sortingNestedRunwayDirectionReciprocalR1A1.latitude = 50.9765;
        sortingNestedRunwayDirectionReciprocalR1A1.longitude = -170.3211;

        RunwayDirection sortingNestedRunwayDirectionBaseR2A1 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR2A1.id = "RWY_DIRECTION_BASE_END_0000001_2";
        sortingNestedRunwayDirectionBaseR2A1.runwayEndId = sortingNestedRunwayEndBaseR2A1.id;
        sortingNestedRunwayDirectionBaseR2A1.latitude = 60.5679;
        sortingNestedRunwayDirectionBaseR2A1.longitude = -160.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR2A1 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR2A1.id = "RWY_DIRECTION_RECIPROCAL_END_0000001_2";
        sortingNestedRunwayDirectionReciprocalR2A1.runwayEndId = sortingNestedRunwayEndReciprocalR2A1.id;
        sortingNestedRunwayDirectionReciprocalR2A1.latitude = 60.9765;
        sortingNestedRunwayDirectionReciprocalR2A1.longitude = -160.3211;

        RunwayDirection sortingNestedRunwayDirectionBaseR1A2 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR1A2.id = "RWY_DIRECTION_BASE_END_0000002_1";
        sortingNestedRunwayDirectionBaseR1A2.runwayEndId = sortingNestedRunwayEndBaseR1A2.id;
        sortingNestedRunwayDirectionBaseR1A2.latitude = 40.5679;
        sortingNestedRunwayDirectionBaseR1A2.longitude = -180.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR1A2 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR1A2.id = "RWY_DIRECTION_RECIPROCAL_END_0000002_1";
        sortingNestedRunwayDirectionReciprocalR1A2.runwayEndId = sortingNestedRunwayEndReciprocalR1A2.id;
        sortingNestedRunwayDirectionReciprocalR1A2.latitude = 30.9765;
        sortingNestedRunwayDirectionReciprocalR1A2.longitude = -190.3211;

        RunwayDirection sortingNestedRunwayDirectionBaseR2A2 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR2A2.id = "RWY_DIRECTION_BASE_END_0000002_2";
        sortingNestedRunwayDirectionBaseR2A2.runwayEndId = sortingNestedRunwayEndBaseR2A2.id;
        sortingNestedRunwayDirectionBaseR2A2.latitude = 70.5679;
        sortingNestedRunwayDirectionBaseR2A2.longitude = -150.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR2A2 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR2A2.id = "RWY_DIRECTION_RECIPROCAL_END_0000002_2";
        sortingNestedRunwayDirectionReciprocalR2A2.runwayEndId = sortingNestedRunwayEndReciprocalR2A2.id;
        sortingNestedRunwayDirectionReciprocalR2A2.latitude = 80.9765;
        sortingNestedRunwayDirectionReciprocalR2A2.longitude = -140.3211;

        RunwayDirection sortingNestedRunwayDirectionBaseR1A3 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR1A3.id = "RWY_DIRECTION_BASE_END_0000003_1";
        sortingNestedRunwayDirectionBaseR1A3.runwayEndId = sortingNestedRunwayEndBaseR1A3.id;
        sortingNestedRunwayDirectionBaseR1A3.latitude = 20.5679;
        sortingNestedRunwayDirectionBaseR1A3.longitude = -200.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR1A3 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR1A3.id = "RWY_DIRECTION_RECIPROCAL_END_0000003_1";
        sortingNestedRunwayDirectionReciprocalR1A3.runwayEndId = sortingNestedRunwayEndReciprocalR1A3.id;
        sortingNestedRunwayDirectionReciprocalR1A3.latitude = 10.9765;
        sortingNestedRunwayDirectionReciprocalR1A3.longitude = -210.3211;

        RunwayDirection sortingNestedRunwayDirectionBaseR2A3 = new RunwayDirection();
        sortingNestedRunwayDirectionBaseR2A3.id = "RWY_DIRECTION_BASE_END_0000003_2";
        sortingNestedRunwayDirectionBaseR2A3.runwayEndId = sortingNestedRunwayEndBaseR2A3.id;
        sortingNestedRunwayDirectionBaseR2A3.latitude = 90.5679;
        sortingNestedRunwayDirectionBaseR2A3.longitude = -130.1233;

        RunwayDirection sortingNestedRunwayDirectionReciprocalR2A3 = new RunwayDirection();
        sortingNestedRunwayDirectionReciprocalR2A3.id = "RWY_DIRECTION_RECIPROCAL_END_0000003_2";
        sortingNestedRunwayDirectionReciprocalR2A3.runwayEndId = sortingNestedRunwayEndReciprocalR2A3.id;
        sortingNestedRunwayDirectionReciprocalR2A3.latitude = 100.9765;
        sortingNestedRunwayDirectionReciprocalR2A3.longitude = -120.3211;

        SORTING_NESTED_RUNWAY_DIRECTIONS = new RunwayDirection[]{
                sortingNestedRunwayDirectionBaseR1A1, sortingNestedRunwayDirectionReciprocalR1A1,
                sortingNestedRunwayDirectionBaseR2A1, sortingNestedRunwayDirectionReciprocalR2A1,
                sortingNestedRunwayDirectionBaseR1A2, sortingNestedRunwayDirectionReciprocalR1A2,
                sortingNestedRunwayDirectionBaseR2A2, sortingNestedRunwayDirectionReciprocalR2A2,
                sortingNestedRunwayDirectionBaseR1A3, sortingNestedRunwayDirectionReciprocalR1A3,
                sortingNestedRunwayDirectionBaseR2A3, sortingNestedRunwayDirectionReciprocalR2A3
        };

        // =====================================================
        // * * * * *          EAGER LOADING            * * * * *
        // =====================================================

        Airport eagerLoadAirport = new Airport();
        eagerLoadAirport.id = "AH_0000001";
        eagerLoadAirport.name = "TEST AIRPORT";
        eagerLoadAirport.fieldElevation = 19.5;
        eagerLoadAirport.latitude = 50.5678;
        eagerLoadAirport.longitude = -170.1234;
        eagerLoadAirport.servedCity = "TEST CITY";
        eagerLoadAirport.privateUseOnly = false;
        eagerLoadAirport.iataDesignator = "TST";
        eagerLoadAirport.icao = "KTST";

        EAGER_LOADING_AIRPORT = eagerLoadAirport;

        Runway eagerLoad1Runway = new Runway();
        eagerLoad1Runway.id = "RWY_0000001_1";
        eagerLoad1Runway.airportId = eagerLoadAirport.id;
        eagerLoad1Runway.designator = "1";
        eagerLoad1Runway.length = 8525d;
        eagerLoad1Runway.width = 200d;

        Runway eagerLoad2Runway = new Runway();
        eagerLoad2Runway.id = "RWY_0000001_1";
        eagerLoad2Runway.airportId = eagerLoadAirport.id;
        eagerLoad2Runway.designator = "2";
        eagerLoad2Runway.length = 8525d;
        eagerLoad2Runway.width = 200d;

        EAGER_LOADING_RUNWAYS = new Runway[]{eagerLoad1Runway, eagerLoad2Runway};
    }
}
