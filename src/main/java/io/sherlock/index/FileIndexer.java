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
package io.sherlock.index;

import io.sherlock.common.options.IndexOptions;
import io.sherlock.common.options.InvalidOptionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.util.concurrent.TimeUnit.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.util.concurrent.Executors;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TermQuery;

import org.apache.commons.cli.ParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 * FileIndexer:
 * Automatically indexes files for Sherlock.
 */
public class FileIndexer {

    static Logger logger = Logger.getLogger(FileIndexer.class.getName());

    public static void main(String[] args) {
        final IndexOptions options = new IndexOptions();
        BasicConfigurator.configure();

        try {
            // Check if the help menu needs to be printed.
            if (options.hasHelpOption(args)) {
                options.printHelp();
                System.exit(0);
            }

            // Parse and validate arguments.
            options.parse(args);

            final Path targetPath = options.getTargetPath();
            final Path indexPath  = options.getIndexPath();

            // IndexWriter Configuration.
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            // Index all documents.
            try (IndexWriter writer = new IndexWriter(FSDirectory.open(indexPath), iwc)) {
                logger.info("Indexing files.");

                // Recursively index the documents.
                writer.deleteAll();
                indexDocuments(writer, indexPath, targetPath);

                logger.info("Finished indexing files.");

                // TODO:
                // Start watching for changes.
                // delete
                // ENTRY_CREATE – A directory entry is created. => index
                // ENTRY_DELETE – A directory entry is deleted. => delete index
                // ENTRY_MODIFY – A directory entry is modified. => index

            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        } catch (ParseException e) {
            // Handle cli parse exceptions.
            System.err.println(e.getMessage());
            options.printHelp();
            System.exit(1);
        } catch (InvalidOptionException e) {
            // Handle invalid index options.
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Index Documents
     *
     * Description: Accepts a file or folder path and indexes the file(s).
     * @param {writer}     // The index writer.
     * @param {indexPath}  // The destination path.
     * @param {targetPath} // The target path.
     */
    static void indexDocuments(IndexWriter writer, Path indexPath, Path targetPath) throws IOException {
        if (Files.isDirectory(targetPath)) {
            Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    writeIndex(writer, indexPath, file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            writeIndex(writer, indexPath, targetPath);
        }
        writer.commit();
    }

    /**
     * Write Index
     *
     * Description: Writes a document to a single index file.
     * @param {writer}     // The index writer.
     * @param {indexPath}  // The destination path.
     * @param {targetPath} // The target path.
     */
    static void writeIndex(IndexWriter writer, Path indexPath, Path targetPath) throws IOException {
        Document doc = new Document();
        String relativePath = indexPath.relativize(targetPath).toString();
        doc.add(new StringField("path", relativePath, Store.YES));
        doc.add(new TextField("contents", new String(Files.readAllBytes(targetPath)), Store.YES));
        writer.updateDocument(new Term("path", relativePath), doc);
        logger.info(String.format("Indexed file `%s`", targetPath.toString()));
    }

}
