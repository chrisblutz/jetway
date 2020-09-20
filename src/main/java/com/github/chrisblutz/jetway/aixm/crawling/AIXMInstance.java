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
package com.github.chrisblutz.jetway.aixm.crawling;

import com.github.chrisblutz.jetway.aixm.mappings.FeatureEntry;

import java.lang.reflect.InvocationTargetException;

/**
 * This class represents a special type of {@link AIXMData} that
 * contains the full feature (i.e. {@code AirportHeliport}) and has
 * not yet been crawled.
 *
 * @author Christopher Lutz
 */
public class AIXMInstance extends AIXMData {

    private AIXMData extension = null;
    private final FeatureEntry entry;

    /**
     * This constructor generates a new instance using the specified
     * data and associated {@link FeatureEntry}.
     *
     * @param data  the underlying data to use
     * @param entry the {@link FeatureEntry} for the associated feature
     */
    public AIXMInstance(Object data, FeatureEntry entry) {

        super(data);

        this.entry = entry;
    }

    /**
     * This method retrieves the AIXM extension for this feature data,
     * using XMLBeans methods.  If the extension does not exist,
     * {@link AIXMNullData} is returned.
     *
     * @return The AIXM extension for this feature, or {@link AIXMNullData}
     */
    public AIXMData extension() {

        if (extension == null) {

            try {

                Object[] extensionAbstractArray = (Object[]) data.getClass().getMethod("getExtensionArray").invoke(data);
                if (extensionAbstractArray == null || extensionAbstractArray.length == 0) {

                    extension = AIXMNullData.getInstance();

                } else {

                    Object extensionAbstract = extensionAbstractArray[0];
                    extension = new AIXMData(extensionAbstract.getClass().getMethod("getAbstract" + entry.getName() + "Extension").invoke(extensionAbstract));
                }

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

                // No extension exists, so set the extension to the null data instance
                extension = AIXMNullData.getInstance();
            }
        }

        return extension;
    }
}
