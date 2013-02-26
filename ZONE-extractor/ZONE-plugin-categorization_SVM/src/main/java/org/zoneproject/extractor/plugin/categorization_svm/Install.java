package org.zoneproject.extractor.plugin.categorization_svm;

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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.plugin.categorization_svm.model.Corpus;
import org.zoneproject.extractor.plugin.categorization_svm.model.Dictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.LemmeDictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.StopWords;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.TextExtraction;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight.TF_IDF;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMLearn;
import org.zoneproject.extractor.plugin.categorization_svm.svm.TrainingDataPreparation;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

/*
 * RUN the installer:  mvn install -pl ZONE-plugin-categorization_SVM
 */
public class Install {
    public static void main(String[] args) {
        System.out.println("Training the SVM");
        
        Map<String, String> UrlCategories = new HashMap<String, String>();
        UrlCategories.put("informatique", "http://rss.feedsportal.com/c/629/f/502199/index.rss");
        UrlCategories.put("sport", "http://fr.news.yahoo.com/rss/sports");
        UrlCategories.put("medecine", "http://www.tv5.org/TV5Site/rss/actualites.php?rub=15");
        UrlCategories.put("economie", "http://www.france24.com/fr/economie/rss");
        UrlCategories.put("science", "http://www.tv5.org/TV5Site/rss/actualites.php?rub=14");
       // UrlCategories.put("science", "http://fr.news.yahoo.com/monde/?format=rss");
        
        		
       
        
        //prepare dictionnaire des lemmes
        LemmeDictionnaire.readFileToMap();
        
        //preparer les mot d'arrêts
        StopWords.readFileToList();
        
        //we start the learning process
        TextExtraction Te = new TextExtraction();

        Map<String, List<Text>> CatTextMap = new HashMap<String, List<Text>>();
        
        //extraction des données
        for (Entry<String, String> urlCat : UrlCategories.entrySet()){
        	 Item[] itemsCat = Database.getItemsFromSource(urlCat.getValue());
        	 System.out.println(itemsCat.length+" corresponding to "+ urlCat.getKey() +" category");

             //retreive the news content for SVM
        	 List<Text> texts = new ArrayList<Text>();
             for(Item i: itemsCat) {
                 Text t = new Text(i,1,true);
                 Corpus.getCorpus().add(t);
                 try {
                     Te.extractLemmaFromText(t);
                 } catch (Exception ex) {
                     Logger.getLogger(Install.class.getName()).log(Level.WARNING, null, ex);
                 }
                 texts.add(t);
             }
             
        	 CatTextMap.put(urlCat.getKey(), texts);
        }
        
        //get the weights for each words 
        for (Text t: Corpus.getCorpus()){
            TF_IDF.computeWeight(t);
            TrainingDataPreparation.prepareFeatureVector(t);
        }
        
        //save the corpus and dictionary
        try {
            Dictionnaire.writeDictionnaireIntoFile();
            Corpus.writeCorpusIntoFile();
        } catch (IOException ex) {
            Logger.getLogger(Install.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        for (Entry<String, List<Text>> catTexts: CatTextMap.entrySet()){
        
        	SVMLearn svmL = new SVMLearn(catTexts.getKey());
        	
        	for (Entry<String, List<Text>> catTextsInternal: CatTextMap.entrySet()){
            	
        		for (Text t : catTextsInternal.getValue()){
            		
        			if (catTextsInternal.getKey() == catTexts.getKey()){
            			t.categorie = 1;
            		}
            		else{
            			t.categorie = -1;
            		}
        			
            		svmL.addFeaturedText(t);
            	}
        	}
        	svmL.learn();

        }
        
        
   /*     //get all items corresponding to the sport category
        Item[] itemsSport = Database.getItemsFromSource("http://fr.news.yahoo.com/rss/sports");
        System.out.println(itemsSport.length+" corresponding to sport category");
        
        //get all item corresponding to an other category
        Item[] otherItems = Database.getItemsFromSource("http://fr.news.yahoo.com/monde/?format=rss");
        System.out.println("Number of items for others:"+otherItems.length);
        


        SVMLearn svmL = new SVMLearn();

        //retreive the news content for SVM
        for(Item i: itemsSport) {
            Text t = new Text(i,1,true);
            Corpus.getCorpus().add(t);
            try {
                Te.extractLemmaFromText(t);
            } catch (Exception ex) {
                Logger.getLogger(Install.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        for(Item i: otherItems) {
            Text t = new Text(i,-1,true);
            Corpus.getCorpus().add(t);
            try {
                Te.extractLemmaFromText(t);
            } catch (Exception ex) {
                Logger.getLogger(Install.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        
        //get the weights for each words 
        for (Text t: Corpus.getCorpus()){
            TF_IDF.computeWeight(t);
            TrainingDataPreparation.prepareFeatureVector(t);
            svmL.addFeaturedText(t);
        }
        
        //save the corpus and dictionary
        try {
            Dictionnaire.writeDictionnaireIntoFile();
            Corpus.writeCorpusIntoFile();
        } catch (IOException ex) {
            Logger.getLogger(Install.class.getName()).log(Level.SEVERE, null, ex);
        }

        //start svm model generation
        svmL.learn();*/
    }
}