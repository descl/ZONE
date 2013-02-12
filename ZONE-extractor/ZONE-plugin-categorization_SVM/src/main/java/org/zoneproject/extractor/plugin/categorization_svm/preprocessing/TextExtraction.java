package org.zoneproject.extractor.plugin.categorization_svm.preprocessing;

/*
 * #%L
 * ZONE-plugin-categorization_SVM
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.zoneproject.extractor.plugin.categorization_svm.model.LemmeDictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.StopWords;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TextExtraction {

    //public static final int FILE_SIZE = 1000000;
     /*private static String readFileAsString(String filePath)
            throws java.io.IOException {

        StringBuffer fileData = new StringBuffer(FILE_SIZE);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }*/

	private String getLemmaFromMot(String mot){
		mot=mot.toLowerCase();
  	  if(mot.length()>2 && mot.charAt(1)== '\''){
  		  mot=mot.substring(2);
  	  }
  	  
  	  String lemma=LemmeDictionnaire.getLemmaMap().get(mot);
  	  if(lemma == null){
  		  return null;
  	  }
  	  if (lemma.equals("=")){
  		  lemma = mot;
  	  }
  	  
  	  return lemma;
	}
	
	
	public boolean isStopWorld(String lemma) throws IOException{

		
		if(StopWords.getStopWordList().contains(lemma)){			
			return true;
		}
		   
		else
		{
			return false;
		}
		
		
	}
	
    public void extractLemmaFromText(Text file) {
    	try{
        	Properties props = new Properties();
    		props.put("pos.model", "resources/french.tagger");
    		props.put("annotators", "tokenize, ssplit, pos");
    		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    		
    		// read some text in the text variable
            String text = file.item.concat(); // Add your text here!

            // create an empty Annotation just with the given text
            Annotation document = new Annotation(text);

            // run all Annotators on this text
            pipeline.annotate(document);


            // these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);

            Map<String, Integer> textLemmaMap = new HashMap<String, Integer>();

            int nbLemmainText = 0;

            for (CoreMap sentence : sentences) {
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

                	String mot = token.get(ValueAnnotation.class);
                    String lemma = getLemmaFromMot(mot);
                    if(lemma == null){
                    	continue;
                    }
                    
                    if(isStopWorld(lemma) == true){
                       	continue;
                    }
                    nbLemmainText++;

                    if (textLemmaMap.containsKey(lemma)) {
                        int lemmaValue = textLemmaMap.get(lemma) + 1;
                        textLemmaMap.put(lemma, lemmaValue);
                    } else {
                    	textLemmaMap.put(lemma, 1);
                    }

                }


            }

            file.nbToTMots = nbLemmainText;
            Set<String> keys = textLemmaMap.keySet();
            for (String iter : keys) {
                Mot mot = new Mot();
                mot.Lemma = iter;
                mot.nbOccuences = textLemmaMap.get(iter);
                mot.text = file;
                file.mots.add(mot);
            }
        }
    	catch (IOException e){
    		
    	}
    	
    }

}
