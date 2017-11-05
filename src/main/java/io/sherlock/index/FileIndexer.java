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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.commons.cli.ParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

import java.util.HashMap;
import java.util.Map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/**
 * FileIndexer:
 * Automatically indexes files for Sherlock.
 */
public final class FileIndexer {

    private static Logger logger = Logger.getLogger(FileIndexer.class.getName());

    /**
     * Hidden Constructor.
     */
    private FileIndexer() { }

    /**
     * Entry point.
     *
     * @param args // The command line arguments.
     */
    public static void main(final String[] args) {
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

            Path targetPath  = Paths.get(options.getTargetPath());
            Path indexPath   = Paths.get(options.getIndexPath());

            // IndexWriter Configuration.
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            // Handle documents.
            try (
                Directory indexDirectory = FSDirectory.open(indexPath);
                IndexWriter writer = new IndexWriter(indexDirectory, iwc)
            ) {
                Boolean indexExists = DirectoryReader.indexExists(indexDirectory);

                if (options.hasDeleteFlag()) {
                    // Ensure that indexes exist.
                    if (!indexExists) {
                        logger.info(String.format("No indexes found in `%s`", indexPath.toString()));
                    } else {
                        // Delete all indexes.
                        deleteDocuments(writer, indexPath, targetPath);
                        logger.info("Deleted all documents from index path.");
                    }
                }

                if (!indexExists || options.hasForceFlag()) {
                    // Recursively index all documents.
                    logger.info("Indexing all files in target path.");
                    indexDocuments(writer, indexPath, targetPath);
                    logger.info("Finished indexing files.");
                }

                if (options.hasWatchFlag()) {
                    // Watch for directory changes indefinitely.
                    logger.info("Watching for file changes...");
                    watchDirectory(writer, indexPath, targetPath);
                }

            } catch (IOException | InterruptedException e) {
                logger.error(e.getMessage());
            }

        } catch (ParseException | InvalidOptionException e) {
            // Handle cli exceptions.
            System.err.println(String.format(
                "Failed to process args: %s", e.getMessage())
            );
            options.printHelp();
            System.exit(1);
        }
    }

    /**
     * Watch Directory
     *
     * Description: Watches a directory for files changes in order to automatically update indexes.
     * @param writer                // The index writer.
     * @param indexPath             // The destination path.
     * @param targetPath            // The target path.
     * @throws IOException          // Failure to wathc or open files/directories.
     * @throws InterruptedException // Watching directories was interuppted.
     */
    public static void watchDirectory(final IndexWriter writer, final Path indexPath, final Path targetPath)
        throws IOException, InterruptedException {

        WatchEvent.Kind<?> kind;
        Path keyDirectory;
        WatchKey key;

        // Register the file watcher recursively.
        final WatchService watcher = targetPath.getFileSystem().newWatchService();
        final Map<WatchKey, Path> keys = new HashMap<>();

        Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
                throws IOException {

                keys.put(
                    dir.register(watcher,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    ), dir);
                return FileVisitResult.CONTINUE;
            }
        });

        // Poll watcher events.
        while (true) {
            key = watcher.take();
            keyDirectory = keys.get(key);

            try {
                for (WatchEvent<?> event: key.pollEvents()) {
                    kind = event.kind();

                    // Ignore discared events.
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    // Get the resolved file/folder name.
                    @SuppressWarnings("unchecked")
                    Path changedFile = keyDirectory.resolve(((WatchEvent<Path>) event).context());

                    // Handle created, modified, or deleted files.
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // (Re-)index the file.
                        if (Files.isRegularFile(changedFile)) {
                            writeToIndex(writer, indexPath, changedFile);
                            logger.info(String.format("Updated index for file `%s`", changedFile.toString()));
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        // Delete any matching documents in index files.
                        deleteFromIndex(writer, indexPath, changedFile);
                        logger.info(String.format("Deleted index for file `%s`", changedFile.toString()));
                    }
                    writer.commit();
                }
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }

            // Reset the watch key. If the key is no longer valid, the directory is inaccessible.
            if (!key.reset()) {
                logger.error("Failed to reset WatchKey.");
                return;
            }
        }
    }

    /**
     * Index Documents
     *
     * Description: Accepts a file or folder path and indexes the file(s).
     * @param writer       // The index writer.
     * @param indexPath    // The destination path.
     * @param targetPath   // The target path.
     * @throws IOException // Failure to index documents.
     */
    static void indexDocuments(final IndexWriter writer, final Path indexPath, final Path targetPath)
        throws IOException {

        if (Files.isDirectory(targetPath)) {
            Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    writeToIndex(writer, indexPath, file);
                    logger.info(String.format("Indexed file `%s`", file.toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            writeToIndex(writer, indexPath, targetPath);
            logger.info(String.format("Indexed file `%s`", targetPath.toString()));
        }
    }

    /**
     * Delete Documents
     *
     * Description: Accepts a file or folder path and deletes all documents from the indexes.
     * @param writer       // The index writer.
     * @param indexPath    // The destination path.
     * @param targetPath   // The target path.
     * @throws IOException // Failure to delete documents.
     */
    static void deleteDocuments(final IndexWriter writer, final Path indexPath, final Path targetPath)
        throws IOException {

        if (Files.isDirectory(targetPath)) {
            Files.walkFileTree(targetPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    deleteFromIndex(writer, indexPath, file);
                    logger.info(String.format("Deleted index for file `%s`", file.toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            deleteFromIndex(writer, indexPath, targetPath);
            logger.info(String.format("Deleted index for file `%s`", targetPath.toString()));
        }
    }

    /**
     * Write Index
     *
     * Description: Writes a document to a single index file.
     * @param writer       // The index writer.
     * @param indexPath    // The destination path.
     * @param targetPath   // The target path.
     * @throws IOException // Failure to write the index.
     */
    static void writeToIndex(final IndexWriter writer, final Path indexPath, final Path targetPath)
        throws IOException {

        String path = targetPath.toString();
        File file = new File(path);
        String line = null;
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            deleteFromIndex(writer, indexPath, targetPath);
            while ((line = br.readLine()) != null) {
                Document doc = new Document();
                doc.add(new StringField("path", path, Store.YES));
                doc.add(new TextField("text", line, Store.YES));
                doc.add(new StoredField("line", ++i));
                writer.addDocument(doc);
            }
        }
    }

    /**
     * Delete Index
     *
     * Description: Deletes documents from the indexes.
     * @param writer       // The index writer.
     * @param indexPath    // The destination path.
     * @param targetPath   // The target path.
     * @throws IOException // Failure to delete the index.
     */
    static void deleteFromIndex(final IndexWriter writer, final Path indexPath, final Path targetPath)
        throws IOException {

        String path = targetPath.toString();
        writer.deleteDocuments(new Term("path", path));
    }
}
