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
package com.github.chrisblutz.jetway.cli;

import com.github.chrisblutz.jetway.database.Database;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles command-line interface
 * parsing and options.
 *
 * @author Christopher Lutz
 */
public class CLI {

    /**
     * This class handles command-line option values
     * and their defaults.
     */
    public static class Options {

        private static File nasrFile = null;
        private static boolean rebuild = false;

        /**
         * This method retrieves the location of the NASR ZIP file.
         *
         * @return The {@link File} referring to the NASR ZIP file
         */
        public static File getNASRFile() {

            return nasrFile;
        }

        /**
         * This method checks if the command-line options indicate
         * that the database should be rebuild from AIXM data.
         *
         * @return {@code true} if the database should be rebuilt,
         * {@code false} otherwise
         */
        public static boolean isRebuildRequired() {

            return rebuild;
        }

        /**
         * This method resets CLI options to their default values.
         */
        public static void reset() {

            nasrFile = null;
            rebuild = false;
        }
    }

    private static final List<String> AVAILABLE_OPTIONS = Arrays.asList("--server", "-s", "--user", "-u", "--password", "-p", "--aixm", "-a", "--rebuild", "-r");

    /**
     * This method parses command-line options from the
     * arguments passed to {@link com.github.chrisblutz.jetway.Jetway#main(String[]) Jetway.main()}.
     * <p>
     * The first command-line entry must be the identifier for the database manager to use.
     * <p>
     * Further available command-line options are:<br>
     * - {@code --aixm}/{@code -a}: this option defines the location of the NASR AIXM ZIP file<br>
     * - {@code --rebuild}/{@code -r}: this option indicates that the Jetway database should be rebuilt
     * from source AIXM data<br>
     * - {@code --server}/{@code -s}: this option defines the database server to use<br>
     * - {@code --user}/{@code -u}: this option defines the database username<br>
     * - {@code --password}/{@code -p}: this option defines the database password<br>
     *
     * @param args command-line arguments
     * @return {@code true} if no issues were found in command-line arguments, {@code false} otherwise
     */
    public static boolean parse(String[] args) {

        if (args.length < 1) {

            System.err.println("At least one argument is required.");
            printCLIFormat();
            return false;
        }

        String databaseManager = args[0];
        Database.setManager(databaseManager);

        for (int i = 1; i < args.length; i++) {

            if (args[i].startsWith("-")) {

                handleArgument(args, i);
            }
        }

        return checkNASRFile();
    }

    private static void handleArgument(String[] args, int i) {

        switch (args[i]) {

            case "--server":
            case "-s":
                String server = getOrPrompt(args, i, "Database Server", false);
                Database.getManager().setServer(server);
                break;

            case "--user":
            case "-u":
                String user = getOrPrompt(args, i, "Database User", false);
                Database.getManager().setUser(user);
                break;

            case "--password":
            case "-p":
                String password = getOrPrompt(args, i, "Database Password", true);
                Database.getManager().setPassword(password);
                break;

            case "--aixm":
            case "-a":
                Options.nasrFile = new File(getOrPrompt(args, i, "NASR AIXM File", false));
                break;

            case "--rebuild":
            case "-r":
                Options.rebuild = true;
                break;
        }
    }

    private static boolean checkNASRFile() {

        if (Options.getNASRFile() == null) {

            System.err.println("No NASR file path was provided.  Please include the --aixm/-a option.");
            printCLIFormat();
            return false;

        } else if (!Options.getNASRFile().exists()) {

            System.err.println("NASR file does not exist at '" + Options.getNASRFile().getPath() + "'.");
            return false;
        }

        // Otherwise, all is well
        return true;
    }

    private static String getOrPrompt(String[] args, int i, String prompt, boolean passwordPrompt) {

        if (i + 1 >= args.length || AVAILABLE_OPTIONS.contains(args[i + 1])) {

            if (passwordPrompt) {

                return new String(System.console().readPassword(prompt + ":"));

            } else {

                return System.console().readLine(prompt + ":");
            }
        }

        return args[i + 1];
    }

    private static void printCLIFormat() {

        // Print format information for Jetway command-line arguments
        System.err.println("Argument format: [db-manager] [--aixm/-a path] ([--server/-s server] [--user/-u username] [--password/-p password] [--rebuild/-r])");
        System.err.println("Descriptions:");
        System.err.println("\tdb-manager: The identifier for the database manager Jetway should use");
        System.err.println("\tpath: The path to the NASR AIXM ZIP file");
        System.err.println("\tserver: The server address for the database (Optional)");
        System.err.println("\tusername: The username for the database (Optional)");
        System.err.println("\tpassword: The password for the database (Optional)");
        System.err.println("\tThe --rebuild/-r option tells Jetway to rebuild all database tables from source AIXM data.");
    }
}
