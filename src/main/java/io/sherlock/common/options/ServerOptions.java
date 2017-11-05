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

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ServerOptions:
 * Options for the server.
 */
public class ServerOptions extends SherlockOptions {

    private String indexes;
    private String root;
    private int port;

    public static final String INDEXES = "indexes";
    public static final String ROOT = "root";
    public static final String PORT = "port";
    public static final int DEFAULT_PORT = 8080;

    private static final Option ROOT_OPTION = Option.builder("r")
        .required(true)
        .hasArg()
        .longOpt(ROOT)
        .desc("{absolute path} : Display files from this directory.")
        .build();

    private static final Option INDEXES_OPTION = Option.builder("i")
        .required(true)
        .hasArg()
        .longOpt(INDEXES)
        .desc("{absolute path} : Searches indexes from this directory.")
        .build();

    private static final Option PORT_OPTION = Option.builder("p")
        .required(false)
        .hasArg()
        .longOpt(PORT)
        .desc("{integer} : Runs the server on this port.")
        .build();

    /**
     * Constructor.
     */
    public ServerOptions() {
        super();
        this.root = null;
        this.port = DEFAULT_PORT;

        // Add the options.
        addOption(ROOT_OPTION);
        addOption(INDEXES_OPTION);
        addOption(PORT_OPTION);
    }

    /**
     * Get Root
     *
     * Description: Getter for the root path.
     * @return the index path as a string.
     */
    public final String getRoot() {
        return root;
    }

    /**
     * Get Indexes
     *
     * Description: Getter for the indexes path.
     * @return the indexes path as a string.
     */
    public final String getIndexes() {
        return indexes;
    }

    /**
     * Get Port
     *
     * Description: Getter for the port number.
     * @return the port as an integer.
     */
    public final int getPort() {
        return port;
    }

    @Override
    public final void parse(final String[] args)
        throws ParseException, InvalidOptionException {

        CommandLineParser parser;
        CommandLine cmd;

        parser = new DefaultParser();
        cmd = parser.parse(getOptions(), args);

        String rootValue = cmd.getOptionValue(ROOT);
        String indexesValue = cmd.getOptionValue(INDEXES);

        // Check if the path to root exists.
        if (!Files.exists(Paths.get(rootValue))) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] %s does not exist or permission denied.",
                rootValue
            ));
        }

        // Check if indexes path exists.
        if (!Files.exists(Paths.get(indexesValue))) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] %s does not exist or permission denied.",
                indexesValue
            ));
        }

        this.root = rootValue;
        this.indexes = indexesValue;

        if (cmd.hasOption(PORT)) {
            this.port = ((Number) cmd.getParsedOptionValue(PORT)).intValue();
        }

    }
}
