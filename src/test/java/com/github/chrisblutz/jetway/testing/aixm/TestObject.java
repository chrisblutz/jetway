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
package com.github.chrisblutz.jetway.testing.aixm;

import aero.aixm.v5.CodeYesNoType;
import aero.aixm.v5.impl.CodeYesNoTypeImpl;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for testing of AIXM data conversion
 * and retrieval.
 *
 * @author Christopher Lutz
 */
public class TestObject {

    /**
     * This is a test method for testing
     * AIXM type to {@code Boolean} conversion.
     *
     * @return The AIXM type
     */
    public CodeYesNoType getBoolean() {

        return new CodeYesNoTypeImpl(null) {

            @Override
            public String getStringValue() {

                return "YES";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Byte} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getByte() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "5";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Character} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getCharacter() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "_";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Double} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getDouble() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "1.234";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Float} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getFloat() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "5.678";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Integer} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getInteger() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "1234567";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Long} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getLong() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "123456789";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code String} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getString() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "Value";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type to {@code Short} conversion.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getShort() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "12345";
            }
        };
    }

    /**
     * This is a test method for testing
     * AIXM type conversion when the AIXM type is {@code null}.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getNull() {

        return null;
    }

    /**
     * This is a test method for testing
     * AIXM type conversion with indexing
     * on a {@link List}.
     *
     * @return The AIXM type
     */
    public List<XmlAnySimpleType> getList() {

        List<XmlAnySimpleType> list = new ArrayList<>();
        list.add(new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 1";
            }
        });
        list.add(new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 2";
            }
        });
        list.add(new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 3";
            }
        });

        return list;
    }

    /**
     * This is a test method for testing
     * AIXM type conversion with indexing
     * on arrays.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType[] getArray() {

        XmlAnySimpleType[] array = new XmlAnySimpleType[3];
        array[0] = new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 1";
            }
        };
        array[1] = new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 2";
            }
        };
        array[2] = new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String 3";
            }
        };

        return array;
    }

    /**
     * This is a test method for testing
     * AIXM indexing when the AIXM type
     * cannot be indexed.
     *
     * @return The AIXM type
     */
    public XmlAnySimpleType getNoIndex() {

        return new XmlAnySimpleTypeImpl() {

            @Override
            public String getStringValue() {

                return "String";
            }
        };
    }
}
