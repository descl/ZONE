package org.zoneproject.extractor.plugin.categorization_svm.model;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class StopWords {

	private static List<String> StopWordList;
	
	private StopWords(){
		
	}
	
	public static List<String> getStopWordList(){
		if(StopWordList == null){
			StopWordList = new ArrayList<String>();
		}
		
		return StopWordList;
	}
	
	
public static void readFileToList(){
		
		
		try{
			StopWords.getStopWordList();
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream("resources/FR_StopWords");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			 
			  while ((strLine = br.readLine()) != null)   {
			
				   String stw = strLine.trim();
				   stw = stw.split(" ")[0];	
				  StopWordList.add(stw);
				
				  
			  }
			  //Close the input stream
			  in.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
					  
	
		
	}

}
