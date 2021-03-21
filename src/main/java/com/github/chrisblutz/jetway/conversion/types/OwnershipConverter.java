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
package com.github.chrisblutz.jetway.conversion.types;

import com.github.chrisblutz.jetway.conversion.EnumConverter;
import com.github.chrisblutz.jetway.features.fields.Ownership;
import gov.faa.aixm51.apt.AirportHeliportExtensionType;
import gov.faa.aixm51.apt.impl.AirportHeliportExtensionTypeImpl;

/**
 * This class takes AIXM ownership enum types and converts them
 * into {@link Ownership} enum values.
 *
 * @author Christopher Lutz
 */
public class OwnershipConverter extends EnumConverter<Ownership> {

    /**
     * Creates a new converter for {@link Ownership} values.
     */
    public OwnershipConverter() {

        super(Ownership.class, AirportHeliportExtensionType.OwnershipType.Enum.class);
    }

    @Override
    public Ownership convert(Object value) {

        AirportHeliportExtensionType.OwnershipType.Enum enumValue = (AirportHeliportExtensionType.OwnershipType.Enum) value;
        if (enumValue == AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MA)
            return Ownership.AIR_FORCE;
        else if (enumValue == AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MN)
            return Ownership.NAVY;
        else if (enumValue == AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.MR)
            return Ownership.ARMY;
        else if (enumValue == AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.PR)
            return Ownership.PRIVATE;
        else if (enumValue == AirportHeliportExtensionTypeImpl.OwnershipTypeImpl.PU)
            return Ownership.PUBLIC;
        else
            return null;
    }
}
