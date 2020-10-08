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
package com.github.chrisblutz.jetway.features;

import com.github.chrisblutz.jetway.aixm.annotations.AIXMAttribute;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMFeature;
import com.github.chrisblutz.jetway.aixm.annotations.AIXMId;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabasePrimaryKey;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;
import com.github.chrisblutz.jetway.database.queries.Query;
import com.github.chrisblutz.jetway.database.queries.Sort;

/**
 * An {@code Airport} represents an airport or a
 * heliport and contains the geography and
 * identifying information for that site.
 *
 * @author Christopher Lutz
 */
@DatabaseTable("Airports")
@AIXMFeature(name = "AirportHeliport", id = "AH", aixmFile = Airport.AIXM_FILE)
public class Airport implements Feature {

    /**
     * This constant defines the AIXM file
     * for airports and airport components (runways, etc.)
     */
    public static final String AIXM_FILE = "APT_AIXM";

    public static final String ID = "id";
    public static final String NAME = "Name";
    public static final String IATA_DESIGNATOR = "IATA";
    public static final String ICAO = "ICAO";
    public static final String SITE_NUMBER = "SiteNumber";
    public static final String FIELD_ELEVATION = "FieldElevation";
    public static final String LAND_AREA = "LandArea";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String COUNTY = "County";
    public static final String STATE = "State";
    public static final String SERVED_CITY = "ServedCity";
    public static final String NUMBER_OF_SINGLE_ENGINE_AIRCRAFT = "NumberOfSingleEngineAircraft";
    public static final String NUMBER_OF_MULTI_ENGINE_AIRCRAFT = "NumberOfMultiEngineAircraft";
    public static final String NUMBER_OF_JET_ENGINE_AIRCRAFT = "NumberOfJetEngineAircraft";
    public static final String NUMBER_OF_HELICOPTERS = "NumberOfHelicopters";
    public static final String NUMBER_OF_GLIDERS = "NumberOfGliders";
    public static final String NUMBER_OF_MILITARY_AIRCRAFT = "NumberOfMilitaryAircraft";
    public static final String NUMBER_OF_ULTRALIGHT_AIRCRAFT = "NumberOfUltralightAircraft";

    private Runway[] runways;
    private RunwayEnd[] runwayEnds;

    @DatabaseColumn(name = ID, type = DatabaseType.STRING)
    @DatabasePrimaryKey
    @AIXMId
    public String id;

    @DatabaseColumn(name = NAME, type = DatabaseType.STRING)
    @AIXMAttribute(value = "Feature/AIXMName")
    public String name;

    @DatabaseColumn(name = IATA_DESIGNATOR, type = DatabaseType.STRING)
    @AIXMAttribute("Feature/Designator")
    public String iataDesignator;

    @DatabaseColumn(name = ICAO, type = DatabaseType.STRING)
    @AIXMAttribute("Feature/LocationIndicatorICAO")
    public String icao;

    @DatabaseColumn(name = SITE_NUMBER, type = DatabaseType.STRING)
    @AIXMAttribute("Extension/AirportSiteNumber")
    public String siteNumber;

    // TODO public use?

    @DatabaseColumn(name = FIELD_ELEVATION, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/FieldElevation")
    public Double fieldElevation;

    @DatabaseColumn(name = LAND_AREA, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/LandSize")
    public Double landArea;

    @DatabaseColumn(name = LATITUDE, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/ARP/ElevatedPoint/Position/ListValue[1]")
    public Double latitude;

    @DatabaseColumn(name = LONGITUDE, type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/ARP/ElevatedPoint/Position/ListValue[0]")
    public Double longitude;

    @DatabaseColumn(name = COUNTY, type = DatabaseType.STRING)
    @AIXMAttribute("Extension/CountyName")
    public String county;

    @DatabaseColumn(name = STATE, type = DatabaseType.STRING)
    @AIXMAttribute("Extension/StateName")
    public String state;

    @DatabaseColumn(name = SERVED_CITY, type = DatabaseType.STRING)
    @AIXMAttribute("Feature/ServedCityArray[0]/City/AIXMName")
    public String servedCity;

    // TODO ownership? facilityType?

    @DatabaseColumn(name = NUMBER_OF_SINGLE_ENGINE_AIRCRAFT, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfSingleEngineAircraft")
    public Integer numberOfSingleEngineAircraft;

    @DatabaseColumn(name = NUMBER_OF_MULTI_ENGINE_AIRCRAFT, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfMultiEngineAircraft")
    public Integer numberOfMultiEngineAircraft;

    @DatabaseColumn(name = NUMBER_OF_JET_ENGINE_AIRCRAFT, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfJetEngineAircraft")
    public Integer numberOfJetEngineAircraft;

    @DatabaseColumn(name = NUMBER_OF_HELICOPTERS, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfHelicopter")
    public Integer numberOfHelicopters;

    @DatabaseColumn(name = NUMBER_OF_GLIDERS, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfOperationalGlider")
    public Integer numberOfGliders;

    @DatabaseColumn(name = NUMBER_OF_MILITARY_AIRCRAFT, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfMilitaryAircraft")
    public Integer numberOfMilitaryAircraft;

    @DatabaseColumn(name = NUMBER_OF_ULTRALIGHT_AIRCRAFT, type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfUltralightAircraft")
    public Integer numberOfUltralightAircraft;

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String toString() {

        return "Airport{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", iataDesignator='" + iataDesignator + '\'' +
                ", icao='" + icao + '\'' +
                ", siteNumber='" + siteNumber + '\'' +
                ", fieldElevation=" + fieldElevation +
                ", landArea=" + landArea +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", servedCity='" + servedCity + '\'' +
                ", numberOfSingleEngineAircraft=" + numberOfSingleEngineAircraft +
                ", numberOfMultiEngineAircraft=" + numberOfMultiEngineAircraft +
                ", numberOfJetEngineAircraft=" + numberOfJetEngineAircraft +
                ", numberOfHelicopters=" + numberOfHelicopters +
                ", numberOfGliders=" + numberOfGliders +
                ", numberOfMilitaryAircraft=" + numberOfMilitaryAircraft +
                ", numberOfUltralightAircraft=" + numberOfUltralightAircraft +
                '}';
    }

    /**
     * This method selects all runways at this airport from
     * the database and returns them.
     * <p>
     * This method also caches its result, so calling this method
     * more than once will not result in more database calls.
     *
     * @return The array of runways
     */
    public Runway[] getRunways() {

        // Check if cached value exists
        if (runways == null) {

            Query query = Query.whereEquals(Runway.class, Runway.AIRPORT_ID, id);
            runways = Runway.selectAll(query);
        }

        return runways;
    }

    /**
     * This method selects all runways at this airport that
     * fit the {@link Query} from the database and returns them.
     * <p>
     * Unlike {@link #getRunways()}, this method does not cache its
     * result, so calling this method will result in further
     * database calls, even if the same {@link Query} is used.
     *
     * @param query the {@link Query} to use
     * @return The array of runways
     */
    public Runway[] getRunways(Query query) {

        Query fullQuery = query.and(Query.whereEquals(Runway.class, Runway.AIRPORT_ID, id));
        return Runway.selectAll(fullQuery);
    }

    /**
     * This method selects all runway ends for this airport from
     * the database and returns them.
     * <p>
     * This method also caches its result, so calling this method
     * more than once will not result in more database calls.
     *
     * @return The array of runway ends
     */
    public RunwayEnd[] getRunwayEnds() {

        // Check if cached value exists
        if (runwayEnds == null) {

            Query query = Query.whereEquals(RunwayEnd.class, RunwayEnd.AIRPORT_ID, id);
            runwayEnds = RunwayEnd.selectAll(query);
        }

        return runwayEnds;
    }

    /**
     * This method selects all runway ends for this airport that
     * fit the {@link Query} from the database and returns them.
     * <p>
     * Unlike {@link #getRunwayEnds()}, this method does not cache its
     * result, so calling this method will result in further
     * database calls, even if the same {@link Query} is used.
     *
     * @param query the {@link Query} to use
     * @return The array of runway ends
     */
    public RunwayEnd[] getRunwayEnds(Query query) {

        Query fullQuery = query.and(Query.whereEquals(RunwayEnd.class, RunwayEnd.AIRPORT_ID, id));
        return RunwayEnd.selectAll(fullQuery);
    }

    /**
     * This method selects a single airport from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all airports.
     * <p>
     * There is no guarantee that calling this method twice
     * will yield the same airport.  As such, this method
     * should generally be reserved for selections and {@link Query}s
     * where only one match will be found in the database (i.e.
     * selecting on ICAO designator).
     *
     * @param query the {@link Query} to use
     * @return The selected airport
     */
    public static Airport select(Query query) {

        return Database.select(Airport.class, query);
    }

    /**
     * This method selects all airports from the database
     * based on the {@link Query}.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all airports.
     *
     * @param query the {@link Query} to use
     * @return The selected airports
     */
    public static Airport[] selectAll(Query query) {

        return Database.selectAll(Airport.class, query);
    }

    /**
     * This method selects all airports from the database
     * based on the {@link Query}, sorting based on the
     * defined {@link Sort} instance.
     * <p>
     * Passing a {@code null} {@link Query} to this method
     * is interpreted as a selection of all airports.
     * <p>
     * Passing a {@code null} {@link Sort} to this method
     * returns results in a database manager-specific
     * order that is not guaranteed to be consistent.
     *
     * @param query the {@link Query} to use
     * @param sort  the {@link Sort} defining the order to use
     * @return The selected airports
     */
    public static Airport[] selectAll(Query query, Sort sort) {

        return Database.selectAll(Airport.class, query, sort);
    }
}
