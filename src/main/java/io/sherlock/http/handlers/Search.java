package io.sherlock.http.handlers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
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

import java.io.File;
import java.io.IOException;

public class Search {

    public static void searchFiles(RoutingContext routingContext) {
        FSDirectory directory;
        JsonArray arr = new JsonArray();
        try {
            directory = FSDirectory.open(new File("C://sherlock/indexes").toPath());
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
        response.putHeader("content-type", "application/json").end(arr.encodePrettily());
    }

}
