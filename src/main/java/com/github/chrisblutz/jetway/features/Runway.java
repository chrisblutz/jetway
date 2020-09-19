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
import com.github.chrisblutz.jetway.aixm.annotations.AIXMParent;
import com.github.chrisblutz.jetway.database.DatabaseType;
import com.github.chrisblutz.jetway.database.annotations.DatabaseColumn;
import com.github.chrisblutz.jetway.database.annotations.DatabaseTable;

@DatabaseTable("Runways")
@AIXMFeature(name = "Runway", id = "RWY", parent = Airport.class)
public class Runway {

    @DatabaseColumn(name = "id", type = DatabaseType.STRING, primary = true)
    @AIXMId
    public String id;

    @DatabaseColumn(name = "airportId", type = DatabaseType.STRING, foreign = true, foreignClass = Airport.class)
    @AIXMParent
    public String airportId;

    @DatabaseColumn(name = "Designator", type = DatabaseType.STRING)
    @AIXMAttribute("Feature/Designator")
    public String designator;

    @DatabaseColumn(name = "Length", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/LengthStrip")
    public Double length;

    @DatabaseColumn(name = "Width", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Feature/WidthStrip")
    public Double width;

    @Override
    public String toString() {

        return "Runway{" +
                "id='" + id + '\'' +
                ", airportId='" + airportId + '\'' +
                ", designator='" + designator + '\'' +
                ", length=" + length +
                ", width=" + width +
                '}';
    }
}
