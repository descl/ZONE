package org.zoneproject.extractor.plugin.categorization_svm.preprocessing;

/*
 * #%L
 * ZONE-plugin-categorization_SVM
 * %%
 * Copyright (C) 2012 ZONE-project
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMLearn;

public class TextExtraction {

        private static StanfordCoreNLP pipeline;
        static { 
        	Properties props = new Properties();
    		//props.put("pos.model", "/resources/french.tagger");
                props.put("pos.model", TextExtraction.class.getResource("/")+"../../resources/french.tagger");
    		props.put("annotators", "tokenize, ssplit, pos");
    		pipeline = new StanfordCoreNLP(props);
        }
        
	private String getLemmaFromMot(String mot){
            mot=mot.toLowerCase();
            if(mot.length()>2 && mot.charAt(1)== '\''){
                mot=mot.substring(2);
            }
            
            //search for the lemmatized word
            return LemmeDictionnaire.getLematizedWord(mot);
        }

	public boolean isStopWorld(String lemma) throws IOException{

		
		if(StopWords.getStopWordList().contains(lemma)){			
			return true;
		}
		   
		else{
			return false;
		}
		
		
	}
	
    public void extractLemmaFromText(Text file) {
    	try{
            // read some text in the text variable
            String text = file.item.concat();
            String[] textElements = file.item.getElements("http://zone-project.org/model/plugins/ExtractArticlesContent#result");
            if(textElements.length > 0) {
                text += "\n"+textElements[0];
            }

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
    	catch ( IOException e){
    		Logger.getLogger(SVMLearn.class.getName()).log(Level.SEVERE, null, e);
    	}
    	
    }

}
