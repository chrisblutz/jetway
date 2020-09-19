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

@DatabaseTable("RunwayDirections")
@AIXMFeature(name = "RunwayDirection", id = "(RWY_DIRECTION_BASE_END|RWY_DIRECTION_RECIPROCAL_END)", parent = RunwayEnd.class)
public class RunwayDirection {

    @DatabaseColumn(name = "id", type = DatabaseType.STRING, primary = true)
    @AIXMId
    public String id;

    @DatabaseColumn(name = "runwayEndId", type = DatabaseType.STRING, foreign = true, foreignClass = RunwayEnd.class)
    @AIXMParent
    public String runwayEndId;

    @DatabaseColumn(name = "Latitude", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/ElevatedPoint/Position/ListValue[1]")
    public Double latitude;

    @DatabaseColumn(name = "Longitude", type = DatabaseType.DOUBLE)
    @AIXMAttribute("Extension/ElevatedPoint/Position/ListValue[0]")
    public Double longitude;

    @Override
    public String toString() {

        return "RunwayDirection{" +
                "id='" + id + '\'' +
                ", runwayEndId='" + runwayEndId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
