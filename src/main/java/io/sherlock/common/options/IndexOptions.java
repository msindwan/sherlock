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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexOptions {

    private Boolean forceFlag;
    private Boolean deleteFlag;
    private Boolean watchFlag;
    private Path targetPath;
    private Path indexPath;
    private Options options;

    public static final String FORCE_FLAG_OPTION  = "force";
    public static final String DELETE_FLAG_OPTION = "delete";
    public static final String TARGET_PATH_OPTION = "target";
    public static final String OUTPUT_PATH_OPTION = "output";
    public static final String WATCH_FLAG_OPTION  = "watch";

    public IndexOptions() {
        this.options = new Options();
        this.forceFlag = false;
        this.deleteFlag = false;
        this.watchFlag = false;

        options.addOption("h", "help", false, "Show the help menu.");

        Option forceFlag = Option.builder("f")
            .longOpt(FORCE_FLAG_OPTION)
            .desc("Forces all files in the documents path to be (re-)indexed.")
            .build();

        Option deleteFlag = Option.builder("d")
            .longOpt(DELETE_FLAG_OPTION)
            .desc("Forces all files in the documents path to be deleted from the indexes.")
            .build();

        Option watchFlag = Option.builder("w")
            .longOpt(WATCH_FLAG_OPTION)
            .desc("Watches files for changes.")
            .build();

        Option target = Option.builder("t")
            .required(true)
            .hasArg()
            .longOpt(TARGET_PATH_OPTION)
            .desc("{absolute path} : Recursively index files in this directory.")
            .build();

        Option output = Option.builder("o")
            .required(true)
            .hasArg()
            .longOpt(OUTPUT_PATH_OPTION)
            .desc("{absolute path} : Output the index files to this directory.")
            .build();

        target.setType(String.class);
        output.setType(String.class);

        options.addOption(forceFlag);
        options.addOption(deleteFlag);
        options.addOption(watchFlag);
        options.addOption(target);
        options.addOption(output);
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public Path getIndexPath() {
        return indexPath;
    }

    public boolean hasForceFlag() {
        return forceFlag;
    }

    public boolean hasDeleteFlag() {
        return deleteFlag;
    }

    public boolean hasWatchFlag() {
        return watchFlag;
    }

    public boolean hasHelpOption(String[] args) throws ParseException {
        CommandLineParser parser;
        CommandLine cmd;
        Options help;

        help = new Options();
        help.addOption("h", "help", false, "Show the help menu.");

        parser = new DefaultParser();
        cmd = parser.parse(help, args, true);
        return cmd.hasOption("h");
    }

    public void parse(String[] args) throws ParseException, InvalidOptionException {
        CommandLineParser parser;
        CommandLine cmd;

        parser = new BasicParser();
        cmd = parser.parse(options, args);

        Path target = Paths.get(cmd.getOptionValue(TARGET_PATH_OPTION));
        Path index  = Paths.get(cmd.getOptionValue(OUTPUT_PATH_OPTION));

        // Check if the path to index exists.
        if (!Files.exists(target)) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] Cannot read `%s`: Path does not exist or permission denied.",
                cmd.getOptionValue("documents")
            ));
        }

        targetPath = target;
        indexPath = index;
        forceFlag = cmd.hasOption(FORCE_FLAG_OPTION);
        deleteFlag = cmd.hasOption(DELETE_FLAG_OPTION);
        watchFlag = cmd.hasOption(WATCH_FLAG_OPTION);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Sherlock", options);
    }
}
