import java.awt.Menu;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gov.nih.nlm.uts.webservice.AtomDTO;
import gov.nih.nlm.uts.webservice.Psf;
import gov.nih.nlm.uts.webservice.UiLabelRootSource;
import gov.nih.nlm.uts.webservice.UtsFault_Exception;
import gov.nih.nlm.uts.webservice.UtsWsContentController;
import gov.nih.nlm.uts.webservice.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.UtsWsSecurityControllerImplService;



public class Main {

	private static String ticketGrantingTicket;
	private static UtsWsSecurityController utsSecurityService;
	private static UtsWsMetadataController utsMetadataService;
	private static UtsWsContentController utsContentService;
	private static String cui = "";
	private static List<String> libelleCIM10_fr=new ArrayList<String>();
	private static List<String> libelleCIM10_en=new ArrayList<String>();
	private static List<String> result_ui = new ArrayList<String>();

	public static void main(String[] args) throws UtsFault_Exception, IOException, URISyntaxException {
		try {
			 utsSecurityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();
			 utsMetadataService = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();
			 utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();		 
			} catch (Exception e) {
			 System.out.println("Error!!!" + e.getMessage());
			}
		Desktop d = Desktop.getDesktop();
		d.browse(new URI("http://localhost/dashboard/www/accueil.php"));
		
		
		System.out.println("Launch of the program... Press 'q' to quit at any time");
		System.out.println("Press Enter to continue");
		String done = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		
		while(!s.equals("q")) {
			generateCUI();
			System.out.println("Create an index (1), or using an existing one (2)");
			Scanner scan = new Scanner(System.in);
			int choix = 0;
			choix = scan.nextInt();
			scan.nextLine();
			switch(choix) {
			case 1:
				TextFileIndexer.CreationIndex();
				break;
			case 2:
				System.out.println("Indexes already existing...");
				break;
			}	
			prepareForSearch();	
			Searcher search = new Searcher();
			search.makeSearch(result_ui, libelleCIM10_fr, libelleCIM10_en);		
		}
		System.out.println("Bye bye");
		//Chemin des corpus: C:\Users\orionit\Documents\CorpusRI\English or French
		//Chemin des Index : C:\Users\orionit\eclipse-workspace\IndexEN ou FR		
	}
	
	

	
	public static void generateCUI() {		
		Scanner sc = new Scanner(System.in);
        int x =0;
        String y =" ";
        int langue=0;
        
        String libelleCIM10_en = "";
        String url = "jdbc:mysql://localhost/ri";
        String user = "root";
        String password = "";
        
        System.out.println("Enter a valid patient number:");
        x = sc.nextInt();
        sc.nextLine();
        
       try
       {
              Class.forName("com.mysql.jdbc.Driver").newInstance();
           Connection con = DriverManager.getConnection(url, user, password);
                           
           PreparedStatement pstt = con.prepareStatement("SELECT tab_diagnostic.CodeCIM10, LibelleCIM10 FROM tab_diagnostic INNER JOIN tab_hospitalisation \r\n" +
                   "ON tab_diagnostic.NumHospitalisation=tab_hospitalisation.NumHospitalisation\r\n" +
                   "INNER JOIN tab_patient ON tab_hospitalisation.NumPatient=tab_patient.NumPatient\r\n" +
                   "INNER JOIN ths_cim10 ON tab_diagnostic.CodeCIM10=ths_cim10.CodeCIM10 \r\n" +
                   "WHERE tab_patient.NumPatient= ? ;\r\n" +
                   "");
           pstt.setInt(1,x);
           ResultSet res = pstt.executeQuery();
       
           while(res.next()) {
               System.out.println("Les diagnostics du patient sont les suivants: \n Code: "+res.getString(1)+" / Libellé: "+res.getString(2)+"\n");        
           }            System.out.println("Tapez le code CIM10 qui vous intéresse");
           y=sc.nextLine();           
           PreparedStatement pstt2 = con.prepareStatement("SELECT LibelleCIM10 FROM ths_cim10 WHERE CodeCIM10=? ;");
           pstt2.setString(1,y);
           ResultSet res2 = pstt2.executeQuery();
       
           while(res2.next()) {
               libelleCIM10_fr.add(res2.getString(1));        
           }            
           pstt2.close();
           res2.close();            
           PreparedStatement pstt3 = con.prepareStatement("SELECT cui FROM cui_cim10 WHERE Code=?");
           pstt3.setString(1,y);
           ResultSet res3 = pstt3.executeQuery();    
           while(res3.next()) {
               cui = res3.getString(1);
           }   
           pstt3.close();
           res3.close();    
           PreparedStatement pstt4 = con.prepareStatement("SELECT str FROM cui_cim10 WHERE CODE=? ;");
           pstt4.setString(1,y);
           ResultSet res4 = pstt4.executeQuery();
       
           while(res4.next()) {
               libelleCIM10_en = res4.getString(1);        
           }            pstt4.close();
           res4.close();
           con.close();
       }
           catch (SQLException e) {
                 e.printStackTrace();
               } catch (InstantiationException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (IllegalAccessException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (ClassNotFoundException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }  		
	}
	
	public static void prepareForSearch() throws UtsFault_Exception {
		String username = "deannawung";
		 String password = "m2sitistermino#";		
		 System.out.println("the use of the umls is in progress...");
		 String ticketGrantingTicket = utsSecurityService.getProxyGrantTicket(username, password);
		 //System.out.println(ticketGrantingTicket);
		
		  String serviceName="http://umlsks.nlm.nih.gov";
		  String singleUseTicket = utsSecurityService.getProxyTicket(ticketGrantingTicket, serviceName);
		  //System.out.println(singleUseTicket);
		  String currentUmlsRelease = utsMetadataService.getCurrentUMLSVersion(singleUseTicket);
		  //System.out.println("version"+currentUmlsRelease);		  
		  Psf myPsf = new Psf();	
			myPsf.setIncludeObsolete(false);
			myPsf.setIncludeSuppressible(false);	
			myPsf.getIncludedSources().add("MSH");
			myPsf.getIncludedSources().add("ICD10CM");
			myPsf.getIncludedSources().add("SNOMEDCT_US");
		    myPsf.getIncludedSources().add("OMIM");
					
		String singleUseTicket2 = utsSecurityService.getProxyTicket(ticketGrantingTicket, serviceName);
		 List<AtomDTO> atoms = new ArrayList<AtomDTO>();		
		 System.out.println(cui);
		 atoms = utsContentService.getConceptAtoms(singleUseTicket2, currentUmlsRelease, cui, myPsf);
		 if(atoms.isEmpty() == false) {
				for (AtomDTO atom:atoms) {				
				    String name = atom.getTermString().getName();
			        result_ui.add(0, name);		
			}			
	  }
		 else {
				System.out.println("atoms vide");
			}
		 System.out.println("the use of the umls is done. Results: ");
		 for(int i=0; i<result_ui.size();i++) {
			 //System.out.println(result_ui.get(i));
		 }	
	}
	
}
