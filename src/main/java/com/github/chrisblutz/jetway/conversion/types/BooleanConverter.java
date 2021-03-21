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

import aero.aixm.v5.CodeYesNoType;
import aero.aixm.v5.impl.CodeYesNoTypeImpl;
import com.github.chrisblutz.jetway.conversion.Converter;

/**
 * This class takes AIXM XML types or basic Java objects
 * and converts them to {@link Boolean} objects.
 *
 * @author Christopher Lutz
 */
public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean convert(Object value) {

        if (value instanceof CodeYesNoType) {

            return ((CodeYesNoType) value).getObjectValue() == CodeYesNoTypeImpl.YES;

        } else {

            return false;
        }
    }

    @Override
    public String convertToString(Boolean value) {

        return Boolean.toString(value);
    }

    @Override
    public Boolean convert(String value) {

        // Since our databases might use 1 or 0,
        // we need to catch the 1's
        if (value.equals("1"))
            return true;

        return Boolean.parseBoolean(value);
    }

    @Override
    public Class<Boolean> getProducedType() {

        return Boolean.class;
    }

    @Override
    public Class<?>[] getAcceptedTypes() {

        return new Class<?>[]{CodeYesNoType.class};
    }
}
