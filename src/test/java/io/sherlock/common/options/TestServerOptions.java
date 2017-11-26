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

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.MissingOptionException;

import java.nio.file.Paths;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * TestServerOptions:
 * Tests the server options class.
 */
public class TestServerOptions {
    @Test
    public void testDefaults() {
        ServerOptions options = new ServerOptions();
        assertEquals(options.getRoot(), ServerOptions.DEFAULT_ROOT);
        assertEquals(options.getIndexes(), ServerOptions.DEFAULT_INDEXES);
        assertEquals(options.getPort(), ServerOptions.DEFAULT_PORT);
    }

    @Test(expected = MissingOptionException.class)
    public void testMissingOption() throws ParseException, InvalidOptionException {
        ServerOptions options = new ServerOptions();
        String[] args = {};
        options.parse(args);
    }

    @Test(expected = InvalidOptionException.class)
    public void testParsingInvalidIndexesPath() throws ParseException, InvalidOptionException {
        ServerOptions options = new ServerOptions();
        String indexesPath = "indexes_path";
        String[] args = {
            "-r",
            "<invalid_path>",
            "-i",
            indexesPath
        };
        options.parse(args);
    }

    @Test(expected = NumberFormatException.class)
    public void testParsingInvalidPort() throws ParseException, InvalidOptionException {
        ServerOptions options = new ServerOptions();
        String targetPath = Paths.get(".").toAbsolutePath().toString();
        String indexPath = targetPath;
        String port = "not a valid port";
        String[] args = {
            "-r",
            targetPath,
            "-i",
            indexPath,
            "-p",
            port
        };
        options.parse(args);
    }

    @Test
    public void testParsingValidArgs() throws ParseException, InvalidOptionException {
        ServerOptions options = new ServerOptions();
        String targetPath = Paths.get(".").toAbsolutePath().toString();
        String indexPath = targetPath;
        String port = "5000";
        String[] args = {
            "-r",
            targetPath,
            "-i",
            indexPath,
            "-p",
            port
        };
        options.parse(args);
        assertEquals(options.getRoot(), targetPath);
        assertEquals(options.getIndexes(), indexPath);
        assertEquals(options.getPort(), Integer.parseInt(port));
    }
}
