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

import io.sherlock.common.util.FileUtil;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * IndexOptions:
 * Options for the indexer.
 */
public class IndexOptions extends SherlockOptions {

    private Boolean deleteFlag;
    private Boolean forceFlag;
    private Boolean watchFlag;
    private String targetPath;
    private String indexPath;

    public static final String FORCE_FLAG  = "force";
    public static final String DELETE_FLAG = "delete";
    public static final String TERGET_PATH = "target";
    public static final String OUTPUT_PATH = "output";
    public static final String WATCH_FLAG  = "watch";

    public static final Boolean DEFAULT_FORCE = false;
    public static final Boolean DEFAULT_DELETE = false;
    public static final Boolean DEFAULT_WATCH = false;
    public static final String DEFAULT_TARGET_PATH = null;
    public static final String DEFAULT_INDEX_PATH = null;

    private static final Option FORCE_FLAG_OPTION = Option.builder("f")
        .longOpt(FORCE_FLAG)
        .desc("Forces all files in the documents path to be (re-)indexed.")
        .build();

    private static final Option DELETE_FLAG_OPTION = Option.builder("d")
        .longOpt(DELETE_FLAG)
        .desc("Deleres all files in the documents path from the indexes.")
        .build();

    private static final Option WATCH_FLAG_OPTION = Option.builder("w")
        .longOpt(WATCH_FLAG)
        .desc("Watches files for changes.")
        .build();

    private static final Option TARGET_OPTION = Option.builder("t")
        .required(true)
        .hasArg()
        .longOpt(TERGET_PATH)
        .desc("{absolute path} : Recursively index files in this directory.")
        .build();

    private static final Option OUTPUT_OPTION = Option.builder("o")
        .required(true)
        .hasArg()
        .longOpt(OUTPUT_PATH)
        .desc("{absolute path} : Output the index files to this directory.")
        .build();

    /**
     * Constructor.
     */
    public IndexOptions() {
        super();
        this.forceFlag  = DEFAULT_FORCE;
        this.deleteFlag = DEFAULT_DELETE;
        this.watchFlag  = DEFAULT_WATCH;
        this.targetPath = DEFAULT_TARGET_PATH;
        this.indexPath  = DEFAULT_INDEX_PATH;

        // Add the options.
        addOption(FORCE_FLAG_OPTION);
        addOption(DELETE_FLAG_OPTION);
        addOption(WATCH_FLAG_OPTION);
        addOption(TARGET_OPTION);
        addOption(OUTPUT_OPTION);
    }

    /**
     * Get Target Path
     *
     * Description: Getter for the terget path.
     * @return the target path as a string.
     */
    public final String getTargetPath() {
        return targetPath;
    }

    /**
     * Get Index Path
     *
     * Description: Getter for the index path.
     * @return the index path as a string.
     */
    public final String getIndexPath() {
        return indexPath;
    }

    /**
     * Has Force Flag
     *
     * Description: Getter for Force flag.
     * @return true if the flag was provided; false otherwise
     */
    public final boolean hasForceFlag() {
        return forceFlag;
    }

    /**
     * Has Delete Flag
     *
     * Description: Getter for Delete flag.
     * @return true if the flag was provided; false otherwise
     */
    public final boolean hasDeleteFlag() {
        return deleteFlag;
    }

    /**
     * Has Watch Flag
     *
     * Description: Getter for watch flag.
     * @return true if the flag was provided; false otherwise
     */
    public final boolean hasWatchFlag() {
        return watchFlag;
    }

    @Override
    public final void parse(final String[] args)
        throws ParseException, InvalidOptionException {

        CommandLineParser parser;
        CommandLine cmd;

        parser = new DefaultParser();
        cmd = parser.parse(getOptions(), args);

        String target = cmd.getOptionValue(TERGET_PATH);
        String index  = cmd.getOptionValue(OUTPUT_PATH);

        if (!FileUtil.exists(target)) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] %s does not exist or permission denied.",
                target
            ));
        }

        // Apply the options.
        targetPath = target;
        indexPath = index;
        forceFlag = cmd.hasOption(FORCE_FLAG);
        deleteFlag = cmd.hasOption(DELETE_FLAG);
        watchFlag = cmd.hasOption(WATCH_FLAG);
    }
}
