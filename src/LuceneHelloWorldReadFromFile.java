
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import javax.security.auth.login.Configuration;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneHelloWorldReadFromFile {

	public static void main(String[] args) throws IOException, ParseException {

		String textDir = "/home/arj/Documents/CSQ/MRT/Assignment5/text";
		String indexDir = "/home/arj/Documents/CSQ/MRT/Assignment5/textindex";
//		textFilesIndexer(textDir,	indexDir);
		textQuerySearcher(indexDir, "hello");
		
		
		
	}

	public static void textQuerySearcher(String indexDir, String queryText) {
		 // Now let's try to search for Hello 
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
		
		Directory directory = null;
		try {
			directory = FSDirectory.open(Paths.get(indexDir));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(directory);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		 IndexSearcher searcher = new IndexSearcher(reader); 
		 QueryParser parser = new QueryParser("contents", standardAnalyzer); 
		 Query query = null; 
		 TopDocs results = null;
		 
		 // search for a value not indexed 
		 try { query = parser.parse("network"); }
		 catch (org.apache.lucene.queryparser.classic.ParseException e) { 
			 // TODO 		 Auto-generated  			 catch block
			 e.printStackTrace(); 
			 } 
		 try {
			results = searcher.search(query, 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 System.out.println("Hits for Hi there -->" + results.totalHits);
		 System.out.println("Hits for Hi there scoredocs-->" + results.scoreDocs.length);
		 System.out.println("Hits for Hi there scoredocs-->" + results.scoreDocs[0]);
			try {
				System.out.println("Hits for Hi there scoredocs-->" + searcher.doc( results.scoreDocs[0].doc).get("filename"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		


	}
	
	
	public static void textFilesIndexer(String inputDir, String outputDir) throws FileNotFoundException {
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer();

		File folder = new File(inputDir);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {

			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".txt")) {

				IndexWriter writer = null;
				Directory directory = null;
				try {
					directory = FSDirectory.open(Paths.get(outputDir));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
				config.setOpenMode(OpenMode.CREATE);
				// Create a writer
				try {
					writer = new IndexWriter(directory, config);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Document document = new Document();
				System.out.println("Reading file: " + file.getAbsolutePath());

				try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {

					
					  document.add(
						      new TextField("contents", br));
						    document.add(
						      new StringField("path", file.getPath(), Field.Store.YES));
						    document.add(
						      new StringField("filename", file.getName(), Field.Store.YES));
						    
						    
					writer.addDocument(document);
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}
}