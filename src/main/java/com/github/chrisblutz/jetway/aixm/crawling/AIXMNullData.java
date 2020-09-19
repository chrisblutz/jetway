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

/**
 * This class represents an {@link AIXMData} instance
 * where the underlying data is {@code null}.  This class overrides
 * methods to automatically return {@code null}, which allows
 * {@code null} values to cascade safely through the crawling/indexing
 * process in {@link AIXMData}.
 *
 * @author Christopher Lutz
 */
public class AIXMNullData extends AIXMData {

    private static AIXMNullData instance = null;

    private AIXMNullData() {

        super(null);
    }

    /**
     * This method retrieves the singleton instance
     * of this class.
     *
     * @return The singleton instance of this class
     */
    public static AIXMNullData getInstance() {

        if (instance == null)
            instance = new AIXMNullData();

        return instance;
    }

    @Override
    public Object get(Class<?> typeClass) {

        return null;
    }

    @Override
    public AIXMData crawl(String path) {

        return this;
    }
}
