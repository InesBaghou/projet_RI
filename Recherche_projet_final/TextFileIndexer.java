

import org.apache.lucene.analysis.en.EnglishAnalyzer; 
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This terminal application creates an Apache Lucene index in a folder and adds files into this index
 * based on the input of the user.
 */
public class TextFileIndexer {
  public static EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_40, EnglishAnalyzer.getDefaultStopSet());
  public static FrenchAnalyzer frAnalyzer = new FrenchAnalyzer(Version.LUCENE_40, FrenchAnalyzer.getDefaultStopSet());
  private IndexWriter writer;
  private ArrayList<File> queue = new ArrayList<File>();
  
  

  /**
   * Constructor
   * @param indexDir the name of the folder in which the index should be created
   * @throws java.io.IOException when exception creating index.
   */
  TextFileIndexer(String indexDir) throws IOException {
    // the boolean true parameter means to create a new index everytime, 
    // potentially overwriting any existing files there.
    FSDirectory dir = FSDirectory.open(new File(indexDir));
    System.out.println("Choose a prefered language : english (0) or french (1) ? ");
    Scanner sc1 = new Scanner(System.in);
    String input = sc1.nextLine();
    if(input.equals("0")) {
    	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        writer = new IndexWriter(dir, config);	
    }
    if(input.equals("1")) {
    	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, frAnalyzer);
        writer = new IndexWriter(dir, config);
    	
    }
    
  }
  
  
  

  /**
   * Indexes a file or directory
   * @param fileName the name of a text file or a folder we wish to add to the index
   * @throws java.io.IOException when exception
   */
  public void indexFileOrDirectory(String fileName) throws IOException {
    //===================================================
    //gets the list of files in a folder (if user has submitted
    //the name of a folder) or gets a single file name (is user
    //has submitted only the file name) 
    //===================================================
    addFiles(new File(fileName));
    
    int originalNumDocs = writer.numDocs();
    for (File f : queue) {
      FileReader fr = null;
      try {
        Document doc = new Document();

        //===================================================
        // add contents of file
        //===================================================
        fr = new FileReader(f);
        doc.add(new TextField("contents", fr));
        doc.add(new StringField("path", f.getPath(), Field.Store.YES));
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));

        writer.addDocument(doc);
        System.out.println("Added: " + f);
      } catch (Exception e) {
        System.out.println("Could not add: " + f);
      } finally {
        fr.close();
      }
    }
    
    int newNumDocs = writer.numDocs();
    System.out.println("");
    System.out.println("************************");
    System.out.println((newNumDocs - originalNumDocs) + " documents added.");
    System.out.println("************************");
    queue.clear();
  }
  
  

  private void addFiles(File file) {

    if (!file.exists()) {
      System.out.println(file + " does not exist.");
    }
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        addFiles(f);
      }
    } else {
      String filename = file.getName().toLowerCase();
      //===================================================
      // Only index text files
      //===================================================
      if (filename.endsWith(".htm") || filename.endsWith(".html") || 
              filename.endsWith(".xml") || filename.endsWith(".txt")) {
        queue.add(file);
      } else {
        System.out.println("Skipped " + filename);
      }
    }
  }

  /**
   * Close the index.
   * @throws java.io.IOException when exception closing
   */
 
  public void closeIndex() throws IOException {
    writer.close();
  }
  
  
  public static void CreationIndex()throws IOException {
	  	//System.out.println("Combien d'index voulez-vous créer ?");
		//Scanner sc = new Scanner(System.in);
		//String input = sc.nextLine();
		//int nb = Integer.parseInt(input);
	  int nb = 2;
	  System.out.println("The program will create 2 indexes");
		int i=0;
		while(i < nb) {
			i++;			
			 System.out.println("Entrer the path index '"+i+"' will be created: (e.g. /tmp/index or c:\\temp\\index)");
			    String indexLocation = null;
			    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    String s = br.readLine();
			    
			    TextFileIndexer indexer = null;
			    try {
			      indexLocation = s;
			      indexer = new TextFileIndexer(s);
			    } catch (Exception ex) {
			      System.out.println("Cannot create index..." + ex.getMessage());
			      System.exit(-1);
			    }

			    while (!s.equalsIgnoreCase("q")) {
			      try {
			        System.out.println("Entrer the corpus path : (e.g. /home/ron/mydir or c:\\Users\\ron\\mydir)");
			        System.out.println("[Acceptable file types: .xml, .html, .html, .txt]");
			        s = br.readLine();
			        if (s.equalsIgnoreCase("q")) {
			          break;
			        }

			        //try to add file into the index
			        indexer.indexFileOrDirectory(s);
			      } catch (Exception e) {
			        System.out.println("Error indexing " + s + " : " + e.getMessage());
			      }
			    }
			    indexer.closeIndex();	
		}
		System.out.println("Indexing done");
  }
  /**
  public static void UpdateIndex()throws IOException {
	  System.out.println("Enter the path of the index to update: (e.g. /tmp/index or c:\\temp\\index)");
	    String indexLocation = null;
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String s = br.readLine();
	    TextFileIndexer indexer = null;
	    try {
	      indexLocation = s;
	      indexer = new TextFileIndexer(s);
	    } 
	    catch (Exception ex) {
	      System.out.println("Cannot create index..." + ex.getMessage());
	      System.exit(-1);
	    }
	    while (!s.equalsIgnoreCase("q")) {
	      try {
	        System.out.println("Enter the full path to add into the index (q=quit): (e.g. /home/ron/mydir or c:\\Users\\ron\\mydir)");
	        System.out.println("[Acceptable file types: .xml, .html, .html, .txt]");
	        s = br.readLine();
	        if (s.equalsIgnoreCase("q")) {
	          break;
	        }
	        //try to add file into the index
	        indexer.indexFileOrDirectory(s);
	      } catch (Exception e) {
	        System.out.println("Error indexing " + s + " : " + e.getMessage());
	      }
	    }
	    indexer.closeIndex();
	    System.out.println("Mise à jour effectuée");
  } **/
	  
  
}
