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
package io.sherlock.core.util;

import java.nio.file.Paths;
import java.nio.file.Path;

import io.vertx.core.json.JsonArray;

public class PathUtil {

    public static String fromJSONString(String root, String path) {
        // Parse the folder path if provided.
        if (path != null) {
            JsonArray jsonarray = new JsonArray(path);
            String[] pList = new String[jsonarray.size()];

            for (int i = 0; i < jsonarray.size(); i++) {
                pList[i] = jsonarray.getString(i);
            }

            return Paths.get(root, pList).toString();
        }

        return root;
    }

}
