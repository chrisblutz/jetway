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
package com.github.chrisblutz.jetway.database.managers.metadata;

/**
 * This class manages the various metadata used by Jetway and its databases.
 *
 * @author Christopher Lutz
 */
public final class Metadata {

    private static final String JETWAY_VERSION_COLUMN = "jetway_version";
    private static final String EFFECTIVE_FROM_DATE_COLUMN = "effective_from";
    private static final String EFFECTIVE_TO_DATE_COLUMN = "effective_to";

    /**
     * This metadata represents the version of Jetway used to generate the current database information.
     */
    public static final StringMetadata JETWAY_VERSION = new StringMetadata(JETWAY_VERSION_COLUMN);

    /**
     * This metadata represents the date on which the current database information becomes effective.
     */
    public static final DateMetadata EFFECTIVE_FROM_DATE = new DateMetadata(EFFECTIVE_FROM_DATE_COLUMN);

    /**
     * This metadata represents the date after which the current database information is no longer effective.
     */
    public static final DateMetadata EFFECTIVE_TO_DATE = new DateMetadata(EFFECTIVE_TO_DATE_COLUMN);

    /**
     * This array contains all declared metadata columns that Jetway uses.
     * It is used when generating the metadata table within Jetway's database.
     */
    public static final BasicMetadata<?>[] METADATA = new BasicMetadata[]{JETWAY_VERSION, EFFECTIVE_FROM_DATE, EFFECTIVE_TO_DATE};
}
