package org.zoneproject.extractor.plugin.categorization_svm.model;

/*
 * #%L
 * ZONE-plugin-categorization_SVM
 * %%
 * Copyright (C) 2012 - 2013 ZONE-project
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Dictionnaire of lemmatizables words
 */
public class LemmeDictionnaire {
	
    private static Map<String,String> lemmaMap;

    private LemmeDictionnaire(){}

    public static String getLematizedWord(String word){
        String lemma=LemmeDictionnaire.getLemmaMap().get(word);
        if(lemma == null){
              return null;
        }
        if (lemma.equals("=")){
              return word;
        }
        return lemma;
    }

    public static Map<String,String> getLemmaMap(){
        if(lemmaMap == null){
            lemmaMap = new HashMap<String,String>();
            LemmeDictionnaire.readFileToMap();
        }

        return lemmaMap;
    }
	
	
    public static void readFileToMap(){
        try{
            LemmeDictionnaire.getLemmaMap();
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream =  new FileInputStream(new File(LemmeDictionnaire.class.getResource("/french_lemmas.txt").toURI()));
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
            String strLine;
            //Read File Line By Line

            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                String[] result = strLine.split("\\t");
                lemmaMap.put(result[0],result[1]);

            }
            //Close the input stream
            in.close();
        }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
        }
    }

}
