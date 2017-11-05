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
import java.nio.file.Path;

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
    public static String pathFromJSONString(
        final String root,
        final String json) {

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

}
