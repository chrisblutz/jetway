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
import com.github.chrisblutz.jetway.aixm.annotations.AIXMRoot;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;

@DatabaseTable("Airports")
@AIXMRoot("APT_AIXM")
@AIXMFeature(name = "AirportHeliport", id = "AH")
public class Airport {

    @DatabaseColumn(name = "id", type = DatabaseType.STRING, primary = true)
    @AIXMId
    public String id;

    @DatabaseColumn(name = "Name", type = DatabaseType.STRING)
    @AIXMAttribute(value = "Feature/AIXMName")
    public String name;

    @DatabaseColumn(name = "IATA", type = DatabaseType.STRING)
    @AIXMAttribute("Feature/Designator")
    public String iataDesignator;

    @DatabaseColumn(name = "ICAO", type = DatabaseType.STRING)
    @AIXMAttribute("Feature/LocationIndicatorICAO")
    public String icao;

    @DatabaseColumn(name = "SiteNumber", type = DatabaseType.STRING)
    @AIXMAttribute("Extension/AirportSiteNumber")
    public String siteNumber;

    // TODO public use?

    @DatabaseColumn(name = "FieldElevation", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/FieldElevation")
    public Double fieldElevation;

    @DatabaseColumn(name = "LandArea", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/LandSize")
    public Double landArea;

    @DatabaseColumn(name = "Latitude", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/ARP/ElevatedPoint/Position/ListValue[1]")
    public Double latitude;

    @DatabaseColumn(name = "Longitude", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/ARP/ElevatedPoint/Position/ListValue[0]")
    public Double longitude;

    @DatabaseColumn(name = "County", type = DatabaseType.STRING)
    @AIXMAttribute("Extension/CountyName")
    public String county;

    @DatabaseColumn(name = "State", type = DatabaseType.STRING)
    @AIXMAttribute("Extension/StateName")
    public String state;

    @DatabaseColumn(name = "ServedCity", type = DatabaseType.STRING)
    @AIXMAttribute("Feature/ServedCityArray[0]/City/AIXMName")
    public String servedCity;

    // TODO ownership? facilityType?

    @DatabaseColumn(name = "NumberOfSingleEngineAircraft", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfSingleEngineAircraft")
    public Integer numberOfSingleEngineAircraft;

    @DatabaseColumn(name = "NumberOfMultiEngineAircraft", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfMultiEngineAircraft")
    public Integer numberOfMultiEngineAircraft;

    @DatabaseColumn(name = "NumberOfJetEngineAircraft", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfJetEngineAircraft")
    public Integer numberOfJetEngineAircraft;

    @DatabaseColumn(name = "NumberOfHelicopters", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfHelicopter")
    public Integer numberOfHelicopters;

    @DatabaseColumn(name = "NumberOfGliders", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfOperationalGlider")
    public Integer numberOfGliders;

    @DatabaseColumn(name = "NumberOfMilitaryAircraft", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfMilitaryAircraft")
    public Integer numberOfMilitaryAircraft;

    @DatabaseColumn(name = "NumberOfUltralightAircraft", type = DatabaseType.INTEGER)
    @AIXMAttribute("Extension/NumberOfUltralightAircraft")
    public Integer numberOfUltralightAircraft;

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
}
