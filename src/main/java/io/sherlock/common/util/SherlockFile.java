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

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import java.util.HashSet;

/**
 * SherlockFile:
 * Defines an abstraction for .sherlock files.
 */
public class SherlockFile {

    private final HashSet<String> lines = new HashSet<String>();

    /**
     * Constructor.
     */
    public SherlockFile() { }

    /**
     * Constructor.
     *
     * @param path         // The path of the folder to search for the sherlock file.
     * @throws IOException // Failure to read the specified file.
     */
     public SherlockFile(final String path) throws IOException {
         this.read(path);
     }

    /**
     * Read
     *
     * Description: Reads the sherlock file.
     * @param path         // The path of the folder to search for the sherlock in.
     * @throws IOException // Failure to read the specified file.
     */
    public final void read(final String path) throws IOException {
        Path sherlockPath = Paths.get(path, ".sherlock");
        lines.clear();
        if (Files.exists(sherlockPath)) {
            FileUtil.readFileIntoSet(sherlockPath.toString(), lines);
        }
    }

    /**
     * Is Ignored
     *
     * Description: Checks if the specified file should be ignored.
     * @param filePath // The file path to look for.
     * @return true if the file should be ignored; false otherwise.
     */
    public final Boolean isIgnored(final String filePath) {
        return lines.contains(FileUtil.getFileExtension(filePath));
    }

}
