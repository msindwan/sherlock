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
import java.nio.file.InvalidPathException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;

import java.util.HashSet;

import io.vertx.core.json.JsonArray;

/**
 * FileUtil:
 * Defines file-related utility functions.
 */
public final class FileUtil {

    /**
     * Hidden Constructor.
     */
    private FileUtil() { }

    /**
     * Path From JSON String
     *
     * Description: Returns the full path from a JSON string.
     * @param root // The root path.
     * @param json // The JSON string.
     * @return the path as a string.
     */
    public static String pathFromJSONString(final String root, final String json) {
        Path normalizedPath;
        JsonArray jsonPath;
        String[] pathList;

        // Parse the folder path if provided.
        if (json != null) {
            jsonPath = new JsonArray(json);
            pathList = new String[jsonPath.size()];

            // Build the path list.
            for (int i = 0; i < jsonPath.size(); i++) {
                pathList[i] = jsonPath.getString(i);
            }

            // Normalize the path and verify that it exists under the root.
            normalizedPath = Paths.get(root, pathList).normalize();
            if (!normalizedPath.startsWith(root)) {
                return null;
            }
            return normalizedPath.toString();
        }

        return root;
    }

    /**
     * Get File Extension
     *
     * Description: Returns the file's extension as a string.
     * @param path // The path to extract the extension from.
     * @return the path extension as a string.
     */
    public static String getFileExtension(final String path) {
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex > 0) {
            return path.substring(extensionIndex);
        }
        return null;
    }

    /**
     * Read File Into a Set
     *
     * Description: Reads a file line-by-line into a set of strings.
     * @param path         // The path to extract lines from.
     * @param lines        // The set of lines to update.
     * @throws IOException // Failure to read the specified file.
     */
    public static void readFileIntoSet(final String path, final HashSet<String> lines) throws IOException {
        File file = new File(path);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
               lines.add(line);
            }
        }
    }

    /**
     * Exists
     *
     * Description: Checks if a path exists.
     * @param path  // The path to check.
     * @return true if it exists; false otherwise.
     */
    public static Boolean exists(final String path) {
        Boolean fileExists;
        try {
            fileExists = Files.exists(Paths.get(path));
        } catch (InvalidPathException | NullPointerException ex) {
            fileExists = false;
        }
        return fileExists;
    }
}
