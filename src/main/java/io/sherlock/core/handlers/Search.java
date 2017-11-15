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

import io.sherlock.common.util.FileUtil;
import io.sherlock.common.util.SherlockFile;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
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
import java.util.Arrays;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Search:
 * Defines handlers for search capabilities.
 */
public final class Search {

    private static final int MAX_FRAGMENT_SIZE = 100;
    private static final int NUM_FRAGMENTS = 10;
    private static final int NUM_HITS = 10;

    /**
     * Hidden Constructor.
     */
    private Search() { }

    /**
     * Search Files
     *
     * Description: Searches files given a set of keywords.
     * @param routingContext // The vertx routing context.
     * @param indexes        // The indexes directory.
     * @param root           // The root directory.
     */
    public static void searchFiles(
        final RoutingContext routingContext,
        final String indexes,
        final String root) {

        HttpServerRequest request;
        JsonArray responseArray;

        request = routingContext.request();
        responseArray = new JsonArray();

        try {
            // Open the index directory
            Directory dir = FSDirectory.open(Paths.get(indexes));
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser qp = new QueryParser("text", analyzer);

            String queryString = request.getParam("query");

            if (queryString == null) {
                Handler.sendError(
                    routingContext,
                    Handler.HTTP_NOT_FOUND,
                    "Missing field `query`"
                );
                return;
            }

            // Create the query
            Query query = qp.parse(queryString);

            TopDocs hits = searcher.search(query, NUM_HITS);
            Formatter formatter = new SimpleHTMLFormatter();
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(
                scorer,
                NUM_FRAGMENTS
            );

            //set fragmenter to highlighter
            highlighter.setTextFragmenter(fragmenter);

            TokenStream stream;
            JsonArray matches;
            JsonObject match;
            String[] frags;
            String path;
            Document doc;

            Map<String, JsonObject> results = new HashMap<>();
            Path rootPath = Paths.get(root);

            // Iterate over found results.
            for (ScoreDoc score : hits.scoreDocs) {
                doc = searcher.doc(score.doc);
                Path absolutePath = Paths.get(doc.get("path"));

                // Add results only if they exist under the root path.
                if (absolutePath.startsWith(rootPath)) {

                    path = rootPath.relativize(absolutePath).toString();

                    // Group results by file path.
                    if (results.containsKey(path)) {
                        match = results.get(path);
                        matches = match.getJsonArray("frags");
                    } else {
                        match = new JsonObject();
                        matches = new JsonArray();

                        // Add the match.
                        String[] pList = path.split(
                            Pattern.quote(File.separator)
                        );
                        match.put("path",  new JsonArray(Arrays.asList(pList)));
                        match.put("frags", matches);
                        results.put(path, match);
                        responseArray.add(match);
                    }

                    stream = TokenSources.getTokenStream(
                        "text",
                        null,
                        doc.get("text"),
                        analyzer, -1
                    );
                    frags = highlighter.getBestFragments(
                        stream,
                        doc.get("text"),
                        MAX_FRAGMENT_SIZE
                    );

                    // Add the fragment.
                    JsonObject object = new JsonObject();
                    object.put("frag", frags[0]);
                    object.put("line", doc.get("line"));
                    matches.add(object);
                }
            }

            routingContext.response()
                .putHeader("content-type", "application/json")
                .end(responseArray.encode());

            dir.close();

        } catch (
            IOException
            | ParseException
            | InvalidTokenOffsetsException e
        ) {
            Handler.sendError(
                routingContext,
                Handler.HTTP_INTERNAL_SERVER_ERROR,
                "Failed to search files."
            );
        }
    }

    /**
     * List Files and Folders
     *
     * Description: Responds with a list of files and folders for the path.
     * @param routingContext // The vertx routing context.
     * @param root           // The root directory.
     * @param sherlockFile   // The sherlock file.
     */
    public static void listFileFolders(
        final RoutingContext routingContext,
        final String root,
        final SherlockFile sherlockFile) {

        ArrayList<JsonObject> files;
        HttpServerRequest request;
        JsonArray responseArray;
        File targetDirectory;
        String path;

        path = FileUtil.pathFromJSONString(
            root,
            routingContext.request().getParam("path")
        );
        if (path == null || !Files.isDirectory(Paths.get(path))) {
            Handler.sendError(
                routingContext,
                Handler.HTTP_NOT_FOUND,
                "Path not found."
            );
            return;
        }

        targetDirectory = new File(path);
        responseArray = new JsonArray();
        files = new ArrayList<>();

        // Iterate through files and create response objects.
        for (File file : targetDirectory.listFiles()) {
            if (file.isDirectory() || !sherlockFile.isIgnored(file.getAbsolutePath())) {
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
        }

        // Concatenate the responseArray with the list of files.
        for (JsonObject object : files) {
            responseArray.add(object);
        }

        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(responseArray.encode());
    }

    /**
     * Get File
     *
     * Description: Responds with the contents of a file.
     * @param routingContext // The vertx routing context.
     * @param root           // The root directory.
     * @param sherlockFile   // The sherlock file.
     */
    public static void getFile(
        final RoutingContext routingContext,
        final String root,
        final SherlockFile sherlockFile) {

        String path = FileUtil.pathFromJSONString(
            root,
            routingContext.request().getParam("path")
        );
        if (path == null || sherlockFile.isIgnored(path)) {
            Handler.sendError(routingContext, Handler.HTTP_NOT_FOUND, "Path not found.");
        } else if (!Files.exists(Paths.get(path))) {
            Handler.sendError(
                routingContext,
                Handler.HTTP_NOT_FOUND,
                "Path not found."
            );
        } else {
            routingContext.response()
                .sendFile(path);
        }
    }

}
