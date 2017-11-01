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

import io.sherlock.core.util.PathUtil;

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
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.HashMap;

/**
 * Search:
 * Defines handlers for search capabilities.
 */
public class Search {

    /**
     * Search Files
     *
     * Description: Searches files given a set of keywords.
     * @param {routingContext} // The vertx routing context.
     */
    public static void searchFiles(RoutingContext routingContext) {
        HttpServerRequest request;
        JsonArray responseArray;

        request = routingContext.request();
        responseArray = new JsonArray();

        try {
            // Open the index directory
            // TODO: Make the index directory configurable
            Directory dir = FSDirectory.open(Paths.get("C:/sherlock/indexes"));
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser qp = new QueryParser("text", analyzer);

            // TODO: Handle null
            String queryString = request.getParam("query");

            // Create the query
            Query query = qp.parse(queryString);

            TopDocs hits = searcher.search(query, 10);
            Formatter formatter = new SimpleHTMLFormatter();
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 10);

            //set fragmenter to highlighter
            highlighter.setTextFragmenter(fragmenter);

            TokenStream stream;
            JsonArray matches;
            JsonObject match;
            String[] frags;
            String path;
            Document doc;

            Map<String, JsonObject> results = new HashMap<>();

            //Iterate over found results
            for (ScoreDoc score : hits.scoreDocs)
            {
                doc = searcher.doc(score.doc);
                path = Paths.get("C://projects").relativize(Paths.get(doc.get("path"))).toString();
                // getLineNumber

                if (results.containsKey(path)) {
                    match = results.get(path);
                    matches = match.getJsonArray("frags");
                } else {
                    match = new JsonObject();
                    matches = new JsonArray();
                    // TODO: relativize this if possible.
                    match.put("path",  path);
                    match.put("frags", matches);
                    results.put(path, match);
                    responseArray.add(match);
                }

                stream = TokenSources.getAnyTokenStream(reader, score.doc, "text", analyzer);
                frags = highlighter.getBestFragments(stream, doc.get("text"), 100);

                JsonObject object = new JsonObject();
                object.put("frag", frags[0]);
                object.put("line", doc.get("line"));

                matches.add(object);
            }

            routingContext.response()
                .putHeader("content-type", "application/json")
                .end(responseArray.encode());

            dir.close();

        } catch (IOException | ParseException | InvalidTokenOffsetsException e) {
            // TODO: Handle exceptions
            e.printStackTrace();
            routingContext.response()
                .setStatusCode(500).end();
        }
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
            .end(responseArray.encode());
    }


    public static void getFile(RoutingContext routingContext) {
        // TODO: Make the root configurable.
        routingContext.response()
            .putHeader("content-type", "text/plain")
            .sendFile(
                PathUtil.fromJSONString(
                    "C://Projects/",
                    routingContext.request().getParam("path")
                )
            );
    }

}
