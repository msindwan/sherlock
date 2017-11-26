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
package io.sherlock.common.util;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.MissingOptionException;

import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashSet;

/**
 * TestSherlockFile:
 * Tests the Sherlock file class.
 */
public class TestSherlockFile {

    @Test
    public void testSherlockFile() {
        SherlockFile sherlockFile = new SherlockFile();
        assertEquals(sherlockFile.size(), 0);
    }

    @Test
    public void testIsIgnored() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/common/.sherlock").getFile());
        String sherlockFilePath = file.getParentFile().getAbsolutePath();
        SherlockFile sherlockfile = new SherlockFile(sherlockFilePath);
        assertTrue(sherlockfile.isIgnored("/some/file/path.ext1"));
        assertTrue(sherlockfile.isIgnored("/some/file/path.ext2"));
        assertTrue(sherlockfile.isIgnored("/some/file/path.ext3"));
    }
}
