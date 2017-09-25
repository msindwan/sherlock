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
package io.sherlock.core.handlers;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.FSDirectory;

import java.util.ArrayList;

/**
 * Search:
 * Defines handlers for search capabilities.
 */
public class Search {

    public static void searchFiles(RoutingContext routingContext) {
        FSDirectory directory;
        JsonArray arr = new JsonArray();
        try {
            directory = FSDirectory.open(new File("C:/sherlock/indexes").toPath());
            // Now search the index:
            Analyzer analyzer = new StandardAnalyzer();
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Parse a simple query that searches for "text":
            QueryParser parser = new QueryParser("contents", analyzer);
            Query query = parser.parse("jconf");
            ScoreDoc[] hits = isearcher.search(query, 1000, new Sort()).scoreDocs;

            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                JsonObject object = new JsonObject();
                object.put("filename", hitDoc.get("filename"));
                arr.add(object);
            }
            ireader.close();
            directory.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        HttpServerResponse response = routingContext.response();
        response
            .putHeader("content-type", "application/json")
            .end(arr.encodePrettily());
    }

    /**
     * listFileFolders
     * @description: Responds with a list of files and folders for the
     * specified path.
     */
    public static void listFileFolders(RoutingContext routingContext) {
        ArrayList<JsonObject> files;
        HttpServerRequest request;
        JsonArray responseArray;
        String path;
        File root;

        // TODO: Make the root configurable.
        root = new File("C://Projects/");
        request = routingContext.request();
        path = request.getParam("path");
        responseArray = new JsonArray();
        files = new ArrayList<>();

        // Parse the folder path if provided.
        if (path != null) {
            JsonArray jsonarray = new JsonArray(path);
            for (int i = 0; i < jsonarray.size(); i++) {
                root = new File(root, jsonarray.getString(i));
            }
        }

        // Iterate through files and create response objects.
        // TODO: Handle folderPath that isn't a folder.
        for (File file : root.listFiles()) {
            JsonObject object = new JsonObject();
            object.put("filename", file.getName());
            object.put("isDir", file.isDirectory());

            if (file.isDirectory()) {
                responseArray.add(object);
            } else {
                // Append files after folders.
                files.add(object);
            }
        }

        // Concatenate the responseArray with the list of files.
        for (JsonObject object : files) {
            responseArray.add(object);
        }

        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(responseArray.encodePrettily());
    }

}
