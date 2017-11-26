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
 * TestIndexOptions:
 * Tests the index options class.
 */
public class TestIndexOptions {
    @Test
    public void testDefaults() {
        IndexOptions options = new IndexOptions();
        assertEquals(options.hasForceFlag(), IndexOptions.DEFAULT_FORCE);
        assertEquals(options.hasDeleteFlag(), IndexOptions.DEFAULT_DELETE);
        assertEquals(options.hasWatchFlag(), IndexOptions.DEFAULT_WATCH);
        assertEquals(options.getTargetPath(), IndexOptions.DEFAULT_TARGET_PATH);
        assertEquals(options.getIndexPath(), IndexOptions.DEFAULT_INDEX_PATH);
    }

    @Test(expected = MissingOptionException.class)
    public void testMissingOption() throws ParseException, InvalidOptionException {
        IndexOptions options = new IndexOptions();
        String[] args = {};
        options.parse(args);
    }

    @Test(expected = InvalidOptionException.class)
    public void testParsingInvalidTargetPath() throws ParseException, InvalidOptionException {
        IndexOptions options = new IndexOptions();
        String indexPath = "index_path";
        String[] args = {
            "-t",
            "<invalid_path>",
            "-o",
            indexPath
        };
        options.parse(args);
    }

    @Test
    public void testParsingValidArgs() throws ParseException, InvalidOptionException {
        IndexOptions options = new IndexOptions();
        String targetPath = Paths.get(".").toAbsolutePath().toString();
        String indexPath = "index_path";
        String[] args = {
            "-w",
            "-f",
            "-d",
            "-t",
            targetPath,
            "-o",
            indexPath
        };
        options.parse(args);
        assertTrue(options.hasForceFlag());
        assertTrue(options.hasDeleteFlag());
        assertTrue(options.hasWatchFlag());
        assertEquals(options.getTargetPath(), targetPath);
        assertEquals(options.getIndexPath(), indexPath);
    }
}
