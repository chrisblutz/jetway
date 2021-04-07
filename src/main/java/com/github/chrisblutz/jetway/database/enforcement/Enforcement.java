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
package com.github.chrisblutz.jetway.database.enforcement;

/**
 * This enum represents various levels of enforcement
 * for warnings or errors within Jetway.
 * <p>
 * For example, these levels are used when determining
 * how to handle out-of-date data.
 *
 * @author Christopher Lutz
 */
public enum Enforcement {

    /**
     * This level indicates that Jetway should not take action
     */
    IGNORE,
    /**
     * This level indicates that Jetway should give a notification
     * that a violation has occurred, but should not fail outright
     */
    LENIENT,
    /**
     * This level indicates that Jetway should throw an error
     * when a violation occurs
     */
    STRICT
}
