package io.sherlock.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.util.Stack;


public class FileIndexer {

    public static void main(String[] args) throws IOException {
        indexFiles("C://Projects", "C://sherlock/indexes");
    }


    private static void indexFiles(String searchPath, String indexPath) throws IOException {
        final Stack<String> folders = new Stack<>();
        FSDirectory dir = FSDirectory.open(new File(indexPath).toPath());
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(dir, config);

        folders.add(searchPath);

        while (folders.size() > 0) {
            File file = new File(folders.pop());
            if (file.isDirectory()) {
                File[] directoryFiles = file.listFiles();
                if (directoryFiles != null) {
                    for (final File fileEntry : directoryFiles) {
                        folders.add(fileEntry.getAbsolutePath());
                    }
                }
            } else {
                // TODO: Check supported file path
                System.out.println(file.getAbsolutePath());
                Document doc = new Document();
                try (FileReader fr = new FileReader(file)) {
                    doc.add(new TextField("contents", fr));
                    doc.add(new StringField("path", file.getPath(), Field.Store.YES));
                    doc.add(new StringField("filename", file.getName(), Field.Store.YES));
                    writer.addDocument(doc);
                } catch (Exception e) {
                    // TODO: Log exception
                    System.out.println("Could not add: " + file);
                }
            }
        }

        writer.close();
    }
}
