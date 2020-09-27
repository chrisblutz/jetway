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
package com.github.chrisblutz.jetway.testing.utils;

import com.github.chrisblutz.jetway.Jetway;

public class JetwayTesting {

    public static void initializeJetway() {

        initializeJetway("/", true);
    }

    public static void initializeJetway(boolean rebuild) {

        initializeJetway("/", rebuild);
    }

    public static void initializeJetway(String aixmPath) {

        initializeJetway(aixmPath, true);
    }

    public static void initializeJetway(String aixmPath, boolean rebuild) {

        String user = System.getenv("TEST_USER");
        String password = System.getenv("TEST_PASSWORD");
        String server = System.getenv("TEST_SERVER");
        Jetway.initialize(("mysql -a " + aixmPath +
                (user != null && !user.isEmpty() ? " -u " + user : "") +
                (password != null && !password.isEmpty() ? " -p " + password : "") +
                (server != null && !server.isEmpty() ? " -s " + server : "")
                + (rebuild ? " --rebuild" : "")).split(" "));
    }
}
