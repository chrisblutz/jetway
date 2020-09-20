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
package com.github.chrisblutz.jetway;

import com.github.chrisblutz.jetway.aixm.AIXM;
import com.github.chrisblutz.jetway.aixm.AIXMFeatureManager;
import com.github.chrisblutz.jetway.aixm.AIXMFiles;
import com.github.chrisblutz.jetway.cli.CLI;
import com.github.chrisblutz.jetway.conversion.DataConversion;
import com.github.chrisblutz.jetway.conversion.DefaultConverters;
import com.github.chrisblutz.jetway.database.Database;
import com.github.chrisblutz.jetway.database.SchemaManager;
import com.github.chrisblutz.jetway.features.*;

/**
 * This class serves as the main entry point for Jetway.
 *
 * @author Christopher Lutz
 */
public class Jetway {

    /**
     * This method is the entry point for Jetway.  It loads converts
     * and database managers, then registers features.  Then, it
     * initiates the AIXM loading sequence.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        initialize(args);
    }

    /**
     * This method initializes Jetway's registries and begins loading
     * AIXM data.
     *
     * @param args command-line arguments
     */
    public static void initialize(String[] args) {

        DefaultConverters.registerAll();
        Database.registerAll();

        FeatureManager.register(Airport.class);
        FeatureManager.register(Runway.class);
        FeatureManager.register(RunwayEnd.class);
        FeatureManager.register(RunwayDirection.class);

        // Parse arguments and continue if all arguments are loaded correctly
        if (CLI.parse(args)) {

            Database.buildAllTables();

            AIXM.load();
        }
    }

    /**
     * This method resets all of Jetway's registries.
     */
    public static void reset() {

        AIXMFeatureManager.reset();
        AIXMFiles.reset();
        SchemaManager.reset();
        Database.reset();
        DataConversion.reset();
    }
}
