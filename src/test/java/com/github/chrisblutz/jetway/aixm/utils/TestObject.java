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
package com.github.chrisblutz.jetway.aixm.utils;

import aero.aixm.v5.CodeYesNoType;
import aero.aixm.v5.impl.CodeYesNoTypeImpl;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl;

public class TestObject {

    public CodeYesNoType getBoolean() {

        return new CodeYesNoTypeImpl(null) {

            @Override
            public Object getObjectValue() {

                return YES;
            }
        };
    }

    public XmlAnySimpleType getByte() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "5";
            }
        };
    }

    public XmlAnySimpleType getCharacter() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "_";
            }
        };
    }

    public XmlAnySimpleType getDouble() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "1.234";
            }
        };
    }

    public XmlAnySimpleType getFloat() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "5.678";
            }
        };
    }

    public XmlAnySimpleType getInteger() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "1234567";
            }
        };
    }

    public XmlAnySimpleType getLong() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "123456789";
            }
        };
    }

    public XmlAnySimpleType getString() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "Value";
            }
        };
    }

    public XmlAnySimpleType getShort() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "12345";
            }
        };
    }
}
