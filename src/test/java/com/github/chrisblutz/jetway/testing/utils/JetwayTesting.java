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
import com.github.chrisblutz.jetway.aixm.source.AIXMSource;
import com.github.chrisblutz.jetway.aixm.source.AIXMStreamSource;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.enforcement.Enforcement;
import com.github.chrisblutz.jetway.database.managers.MySQLDatabaseManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This method provides general-purpose
 * utility methods for Jetway unit tests.
 *
 * @author Christopher Lutz
 */
public class JetwayTesting {

    /**
     * This method constructs a new {@link AIXMSource} using
     * the specified {@link InputStream} for the given feature file name.
     *
     * @param feature the feature file name
     * @param stream  the {@link InputStream} for the feature
     * @return The constructed {@link AIXMSource}
     */
    public static AIXMSource constructSource(String feature, InputStream stream) {

        AIXMStreamSource source = new AIXMStreamSource();
        source.addStream(feature, stream);
        return source;
    }

    /**
     * This method constructs a new {@link AIXMSource} using
     * the specified {@link String} for the given feature file name.
     *
     * @param feature the feature file name
     * @param string  the {@link String} for the feature
     * @return The constructed {@link AIXMSource}
     */
    public static AIXMSource constructSource(String feature, String string) {

        // Convert string into byte array stream
        AIXMStreamSource source = new AIXMStreamSource();
        source.addStream(feature, new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
        return source;
    }

    /**
     * This method initializes Jetway and forces it to
     * rebuild the database.
     *
     * @param aixmSource the AIXM source to use
     */
    public static void initializeJetway(AIXMSource aixmSource) {

        initializeJetway(aixmSource, true);
    }

    /**
     * This method initializes Jetway and  and forces it to
     * rebuild the database if {@code rebuild} is {@code true}.
     *
     * @param aixmSource the AIXM source to use
     * @param rebuild    if {@code true}, the Jetway database will be force-rebuilt.
     *                   If {@code false}, Jetway will handle loading normally.
     */
    public static void initializeJetway(AIXMSource aixmSource, boolean rebuild) {

        initializeJetway(aixmSource, rebuild, Enforcement.IGNORE, true);
    }

    /**
     * This method initializes Jetway and  and forces it to
     * rebuild the database if {@code rebuild} is {@code true}.
     * <p>
     * It also takes into account effective date range enforcement.
     *
     * @param aixmSource  the AIXM source to use
     * @param rebuild     if {@code true}, the Jetway database will be force-rebuilt.
     *                    If {@code false}, Jetway will handle loading normally.
     * @param enforcement the level of {@link Enforcement} to be used for out-of-date data
     * @param clearData   if {@code true}, the Jetway database will clear any existing data before loading.
     *                    If {@code false}, Jetway will not touch existing data.
     */
    public static void initializeJetway(AIXMSource aixmSource, boolean rebuild, Enforcement enforcement, boolean clearData) {

        Jetway.setAIXMSource(aixmSource);
        Jetway.setDatabaseManager(MySQLDatabaseManager.getInstance());

        String server = System.getenv("TEST_MYSQL_SERVER");
        String port = System.getenv("TEST_MYSQL_PORT");
        String user = System.getenv("TEST_MYSQL_USER");
        String password = System.getenv("TEST_MYSQL_PASSWORD");

        if (server != null && !server.isEmpty())
            Jetway.setDatabaseServer(server);

        if (port != null && !port.isEmpty())
            Jetway.setDatabasePort(Integer.parseInt(port));

        if (user != null && !user.isEmpty())
            Jetway.setDatabaseUser(user);

        if (password != null && !password.isEmpty())
            Jetway.setDatabasePassword(password);

        if (rebuild)
            Jetway.forceDatabaseRebuild();

        Database.setEffectiveRangeEnforcement(enforcement);

        if (clearData)
            Database.dropAllTables();

        Jetway.initialize();
    }
}
