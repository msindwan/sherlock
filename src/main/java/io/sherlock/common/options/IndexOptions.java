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

    private Path targetPath;
    private Path indexPath;
    private Options options;
    private int interval;

    public static final int MAX_INTERVAL = 1000;

    public IndexOptions() {
        this.options = new Options();
        options.addOption("h", "help", false, "Show the help menu.");

        Option interval = Option.builder("i")
            .required(true)
            .hasArg()
            .longOpt("interval")
            .desc("{integer} : Index files every `i` minutes.")
            .build();

        Option documents = Option.builder("d")
            .required(true)
            .hasArg()
            .longOpt("documents")
            .desc("{absolute path} : Recursively index files in directory `d`.")
            .build();

        Option output = Option.builder("o")
            .required(true)
            .hasArg()
            .longOpt("output")
            .desc("{absolute path} : Output the index files to directory `o`.")
            .build();

        interval.setType(Number.class);
        documents.setType(String.class);
        output.setType(String.class);

        options.addOption(interval);
        options.addOption(documents);
        options.addOption(output);
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public Path getIndexPath() {
        return indexPath;
    }

    public int getInterval() {
        return interval;
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

        int i = ((Number)cmd.getParsedOptionValue("interval")).intValue();
        Path target = Paths.get(cmd.getOptionValue("documents"));
        Path index  = Paths.get(cmd.getOptionValue("output"));

        // Check if the path to index exists.
        if (!Files.exists(target)) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] Cannot read `%s`: Path does not exist or permission denied.",
                cmd.getOptionValue("documents")
            ));
        }

        // Check if the indexer can write to the specified path.
        if (!Files.isWritable(index)) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] Cannot write to `%s`: Path does not exist or permission denied.",
                cmd.getOptionValue("output")
            ));
        }

        // Check if the interval is within bounds.
        if (i <= 0 || i > MAX_INTERVAL) {
            throw new InvalidOptionException(String.format(
                "[Invalid Option] Interval must be within 0 and %d.",
                MAX_INTERVAL
            ));
        }

        interval = i;
        targetPath = target;
        indexPath = index;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Sherlock", options);
    }
}
