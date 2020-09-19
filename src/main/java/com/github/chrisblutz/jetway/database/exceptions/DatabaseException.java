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
package com.github.chrisblutz.jetway.database.exceptions;

/**
 * This exception class is used when errors occur related
 * to database operations.
 *
 * @author Christopher Lutz
 */
public class DatabaseException extends RuntimeException {

    /**
     * This constructor creates a new instance using the specified message.
     *
     * @param message the error message
     */
    public DatabaseException(String message) {

        super(message);
    }

    /**
     * This constructor creates a new instance using the specified message and
     * the specified cause.
     *
     * @param message the error message
     * @param cause   the {@link Exception} that caused this error
     */
    public DatabaseException(String message, Exception cause) {

        super(message + " (" + cause.getClass().getSimpleName() + ")", cause);
    }
}
