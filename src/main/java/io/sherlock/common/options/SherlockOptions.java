/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sherlock.common.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Options:
 * Base class for cli options.
 */
public abstract class SherlockOptions {

    /**
     * The collection of CLI options.
     */
    private final Options options = new Options();

    /**
     * Constructor.
     */
    public SherlockOptions() {
        this.options.addOption("h", "help", false, "Show the help menu.");
    }

    /**
     * Add Option
     *
     * Description: Adds an option to the list.
     * @param option // The option to add.
     */
    public final void addOption(final Option option) {
        this.options.addOption(option);
    }


    /**
     * Get Options
     *
     * Description: Getter for options.
     * @return the collection of cli options.
     */
    public final Options getOptions() {
        return this.options;
    }

    /**
     * Has Help Option
     *
     * Description: Checks if a help argument was provided.
     * @param args // The cli args to parse.
     * @throws ParseException // Failure to parse cli options.
     * @return true if the help option was provided; false otherwise
     */
    public final boolean hasHelpOption(final String[] args)
        throws ParseException {

        CommandLineParser parser;
        CommandLine cmd;
        Options help;

        help = new Options();
        help.addOption("h", "help", false, "Show the help menu.");

        parser = new DefaultParser();
        cmd = parser.parse(help, args, true);
        return cmd.hasOption("h");
    }

    /**
     * Print Help
     *
     * Description: Prints the options.
     */
    public final void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Sherlock", options);
    }

    /**
     * Parse
     *
     * Description: Parses the command line arguments.
     * @param args // The cli args to parse.
     * @throws ParseException // Failure to parse cli options.
     * @throws InvalidOptionException // One or more argument(s) is invalid.
     */
    public abstract void parse(final String[] args)
        throws ParseException, InvalidOptionException;
}
