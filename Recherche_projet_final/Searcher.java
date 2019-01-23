import java.io.BufferedReader; 
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
   
	static String indexLocation = null;
    private static EnglishAnalyzer analyzer;
    private static FrenchAnalyzer analyzer_french;
    static IndexReader reader ;
    static  IndexSearcher searcher =null;
    static TopScoreDocCollector collector;    


    public static void makeSearch(List<String> result_ui, List<String> libelleCIM10_fr, List<String> libelleCIM10_en) {
    	
    	System.out.println("We are now running the search...");
        System.out.println("Choose a preferred language: English=0 or French=1");
        String s = "";
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        try {
			s=br1.readLine();
			while (!((s.equals("0"))  || (s.equals("1")))){
		            System.out.println("Enter a valid choice");
		            s=br1.readLine();
		        }
		} catch (IOException e1) {
			System.out.println("error with the value.."+s);
			e1.printStackTrace();
		}
        
        System.out.println("Enter the path where the index will be read");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String path_corpus="";
    		try{
                 path_corpus = br.readLine();
                 indexLocation=path_corpus;
                 Path path = FileSystems.getDefault().getPath(path_corpus);
                 while(!Files.exists(path)) {
                	 System.out.println("Enter a valid path location");
                	 path_corpus = br.readLine();    
                 }
                
            } catch (Exception ex) {
                System.out.println("Cannot read index..." + ex.getMessage());
                System.exit(-1);
              }
    		int s2 = Integer.parseInt(s);
    		
    		
			//Choice of Analyzer depending the language
    		switch(s2) {
    		case 0:
    			System.out.println("English");
    			analyzer = new EnglishAnalyzer(Version.LUCENE_40);
    			System.out.println("Would you prefer to do a simple (1) request or an extended one (2)");
    			Scanner sc = new Scanner(System.in);
    			int str = sc.nextInt();
    			if(str == 1) {
    				try{
            			System.out.println("ok1");
                        reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
                        searcher= new IndexSearcher(reader);   
                        System.out.println("ok2");
                            for(int j=0; j<result_ui.size();j++) {
                            	Query q = new QueryParser(Version.LUCENE_40, "contents", analyzer).parse(libelleCIM10_en.get(j));
                                collector  = TopScoreDocCollector.create(5, true);
                                searcher.search(q, collector);
                                ScoreDoc[] hits = collector.topDocs().scoreDocs;               
                                // 4. display results               
                                System.out.println("Found " + hits.length + " hits.");                    
                                for(int i=0;i<hits.length;++i) {
                                    int docId = hits[i].doc;
                                    Document d = searcher.doc(docId);
                                    System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
                                }
                            }                    
                          } 
                        catch (Exception e) {            
                            System.out.println("Error searching " + s + " : " + e.getMessage());                
                           }			
    			}
    			if(str == 2) {
    				try{
            			System.out.println("ok1");
                        reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
                        searcher= new IndexSearcher(reader);   
                        System.out.println("ok2");
                            for(int j=0; j<result_ui.size();j++) {
                            	Query q = new QueryParser(Version.LUCENE_40, "contents", analyzer).parse(result_ui.get(j));
                                collector  = TopScoreDocCollector.create(5, true);
                                searcher.search(q, collector);
                                ScoreDoc[] hits = collector.topDocs().scoreDocs;               
                                // 4. display results               
                                System.out.println("Found " + hits.length + " hits.");                    
                                for(int i=0;i<hits.length;++i) {
                                    int docId = hits[i].doc;
                                    Document d = searcher.doc(docId);
                                    System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
                                }
                            }                    
                          } 
                        catch (Exception e) {            
                            System.out.println("Error searching " + s + " : " + e.getMessage());                
                           }				
    			}
    			
        		
        		break;
    		case 1:
    			System.out.println("French");
    			System.out.println("Would you prefer to do a simple (1) request or an extended one (2)");
    			Scanner sc1 = new Scanner(System.in);
    			int str1 = sc1.nextInt();
    			analyzer_french = new FrenchAnalyzer(Version.LUCENE_40);
    			if(str1==1) {
    				try{
                        reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
                        searcher= new IndexSearcher(reader);   
                            for(int j=0; j<result_ui.size();j++) {
                            	Query q = new QueryParser(Version.LUCENE_40, "contents", analyzer_french).parse(libelleCIM10_fr.get(j));
                                collector  = TopScoreDocCollector.create(5, true);
                                searcher.search(q, collector);
                                ScoreDoc[] hits = collector.topDocs().scoreDocs;               
                                // 4. display results               
                                System.out.println("Found " + hits.length + " hits.");                    
                                for(int i=0;i<hits.length;++i) {
                                    int docId = hits[i].doc;
                                    Document d = searcher.doc(docId);
                                    System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
                                }
                            }                    
                          } 
                        catch (Exception e) {            
                            System.out.println("Error searching " + s + " : " + e.getMessage());                
                           }
    				
    			}
    			if (str1==2) {
    				try{
                        reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
                        searcher= new IndexSearcher(reader);   
                            for(int j=0; j<result_ui.size();j++) {
                            	Query q = new QueryParser(Version.LUCENE_40, "contents", analyzer_french).parse(result_ui.get(j));
                                collector  = TopScoreDocCollector.create(5, true);
                                searcher.search(q, collector);
                                ScoreDoc[] hits = collector.topDocs().scoreDocs;               
                                // 4. display results               
                                System.out.println("Found " + hits.length + " hits.");                    
                                for(int i=0;i<hits.length;++i) {
                                    int docId = hits[i].doc;
                                    Document d = searcher.doc(docId);
                                    System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
                                }
                            }                    
                          } 
                        catch (Exception e) {            
                            System.out.println("Error searching " + s + " : " + e.getMessage());                
                           }
    			}
        		
        		break;
    		}
    		
  	
    		System.out.println("Searcher is done");
    	}
    
    
  

}

