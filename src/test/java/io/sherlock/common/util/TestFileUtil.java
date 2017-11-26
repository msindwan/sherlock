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
 * TestFileUtil:
 * Tests the file utility class.
 */
public class TestFileUtil {
    @Test
    public void testPathFromJSONString() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/common/.sherlock").getFile());
        File parent1 = file.getParentFile();
        File parent2 = parent1.getParentFile();

        // Test that the path is normalized to the correct absolute path.
        assertEquals(
            FileUtil.pathFromJSONString(file.getAbsolutePath(), null),
            file.getAbsolutePath());
        assertEquals(
            FileUtil.pathFromJSONString(parent1.getAbsolutePath(), "[\".sherlock\"]"),
            file.getAbsolutePath());
        assertEquals(
            FileUtil.pathFromJSONString(parent2.getAbsolutePath(), "[\"common\", \".sherlock\"]"),
            file.getAbsolutePath());

        // Test that paths outside of the root return null.
        assertEquals(
            FileUtil.pathFromJSONString(parent1.getAbsolutePath(), "[\"..\", \"outside_root.txt\"]"),
            null);

        file = new File(classLoader.getResource("data/outside_root.txt").getFile());
        parent1 = file.getParentFile();
        assertEquals(
            FileUtil.pathFromJSONString(parent1.getAbsolutePath(), "[\"outside_root.txt\"]"),
            file.getAbsolutePath());
    }

    @Test
    public void testGetFileExtension() {
        assertEquals(FileUtil.getFileExtension("/some/file.png"), ".png");
        assertEquals(FileUtil.getFileExtension("/some/extension/file.a.b"), ".b");
        assertEquals(FileUtil.getFileExtension("/this/is/extension/no-extension"), null);
    }

    @Test
    public void testReadFileIntoSet() throws Exception, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        HashSet<String> lines = new HashSet<>();

        try {
            FileUtil.readFileIntoSet("<invalid_path>", lines);
            throw new Exception("Failed to raise an IOException on an invalid path.");
        } catch (IOException e) {
            // An IOException is thrown as expected.
        }

        FileUtil.readFileIntoSet(classLoader.getResource("data/common/.sherlock").getFile(), lines);
        assertTrue(lines.contains(".ext1"));
        assertTrue(lines.contains(".ext2"));
        assertTrue(lines.contains(".ext3"));
    }

    @Test
    public void testExists() {
        assertFalse(FileUtil.exists("<invalid_path>"));
        assertFalse(FileUtil.exists("/hopefully/this/is/a/fake/path"));
        assertTrue(FileUtil.exists(Paths.get(".").toAbsolutePath().toString()));
    }
}
