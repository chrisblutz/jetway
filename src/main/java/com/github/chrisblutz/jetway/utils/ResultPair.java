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
package com.github.chrisblutz.jetway.utils;

/**
 * This class represents a pair of values that can
 * be returned from a method.
 *
 * @param <T> the type of the first value
 * @param <U> the type of the second value
 * @author Christopher Lutz
 */
public class ResultPair<T, U> {

    private final T first;
    private final U second;

    /**
     * This constructor creates a new pair using the provided values.
     *
     * @param first  the first value
     * @param second the second value
     */
    public ResultPair(T first, U second) {

        this.first = first;
        this.second = second;
    }

    /**
     * This method retrieves the first value in this pair.
     *
     * @return The first value
     */
    public T getFirst() {

        return first;
    }

    /**
     * This method retrieves the second value in this pair.
     *
     * @return The second value
     */
    public U getSecond() {

        return second;
    }
}
