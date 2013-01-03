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
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.zoneproject.extractor.utils.Item;

public class TextExtraction {

    public static final int FILE_SIZE = 1000000;
    public Properties props;

    public void setProps(Properties props) {
        this.props = props;
    }

    public Properties getProps() {
        return props;
    }

    private static String readFileAsString(String filePath)
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
    }

    public void extractLemmaFromText(Text file) {
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

        Map<String, Integer> lemmaMap = new HashMap<String, Integer>();

        int nbLemmainText = 0;

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

                String lemma = token.get(LemmaAnnotation.class);
                nbLemmainText++;

                if (lemmaMap.containsKey(lemma)) {
                    int lemmaValue = lemmaMap.get(lemma) + 1;
                    lemmaMap.put(lemma, lemmaValue);
                } else {
                    lemmaMap.put(lemma, 1);
                }

            }


        }

        file.nbToTMots = nbLemmainText;
        Set<String> keys = lemmaMap.keySet();
        for (String iter : keys) {
            Mot mot = new Mot();
            mot.Lemma = iter;
            mot.nbOccuences = lemmaMap.get(iter);
            mot.text = file;
            file.mots.add(mot);
        }
    }

    @Override
    public String toString() {
        return props.toString();
    }
}
