package org.zoneproject.extractor.plugin.categorization_svm.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LemmeDictionnaire {
	
	private static Map<String,String> lemmaMap;
	
	private LemmeDictionnaire(){
		
	}
	public static Map<String,String> getLemmaMap(){
		if(lemmaMap == null){
			lemmaMap = new HashMap<String,String>();
		}
		
		return lemmaMap;
	}
	
	
public static void readFileToMap(){
		
		
		try{
			LemmeDictionnaire.getLemmaMap();
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("resources/OLDlexique.txt");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
			  String strLine;
			  //Read File Line By Line
			 
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
				  String[] result = strLine.split("\\t");
				  lemmaMap.put(result[0],result[1]);
				  //System.out.println (result[0]+"=>"+result[1]);
				  
			  }
			  //Close the input stream
			  in.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
					  
	
		
	}

}
