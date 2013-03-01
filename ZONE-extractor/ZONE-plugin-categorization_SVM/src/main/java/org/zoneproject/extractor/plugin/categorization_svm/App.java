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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.zoneproject.extractor.plugin.categorization_svm.model.Corpus;
import org.zoneproject.extractor.plugin.categorization_svm.model.Dictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.LemmeDictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.StopWords;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.TextExtraction;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight.TF_IDF;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMClassify;
import org.zoneproject.extractor.plugin.categorization_svm.svm.TrainingDataPreparation;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.VirtuosoDatabase;
import org.zoneproject.extractor.utils.ZoneOntology;
/*
 * Start categorization: mvn exec:java -pl ZONE-plugin-categorization_SVM
 */
public class App 
{
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_SVM;
    public static String PLUGIN_RESULT_URI = ZoneOntology.PLUGIN_SVM_RES;
    
    public static final int informatique = 1;
    public static final int sport = 2;
    public static final int medecine = 3;
    public static final int economie = 4;
    public static final int science = 5;
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    
    public static void main(String[] args) {
    	
        
        //init the SVM
    	Map<Integer, SVMClassify> classiferMap = new HashMap<Integer, SVMClassify>();
    	classiferMap.put(informatique, new SVMClassify("informatique"));
    	classiferMap.put(sport, new SVMClassify("sport"));
    	classiferMap.put(medecine, new SVMClassify("medecine"));
    	classiferMap.put(economie, new SVMClassify("economie"));
    	classiferMap.put(science, new SVMClassify("science"));
    	
    	        
    	for (Entry<Integer, SVMClassify> cm : classiferMap.entrySet()){
    		cm.getValue().readModel();    		
    	}
        
        Dictionnaire.readDictionnaireFromFile();
        Corpus.readCorpusFromFile();
        //prepare dictionnaire des lemmes
        LemmeDictionnaire.readFileToMap();
        //preparer les mot d'arrêts
        StopWords.readFileToList();

        TextExtraction Te = new TextExtraction();
   
        
        //retreive all items not annotated
        //set the items number limit
        //int limit = 100;
        //Item[] itemsNotAnnotated = getNotCategorizeItems(limit);
        Item[] itemsNotAnnotated = Database.getItemsFromSource("http://www.leparisien.fr/actualites-a-la-une.rss.xml");
        System.out.println("Number of items not annotated:"+itemsNotAnnotated.length);
        
        for(Item item : itemsNotAnnotated){
            String itemContent = item.concat();
            //you should run classification on itemContent
            Text t = new Text(item);
            Te.extractLemmaFromText(t);
            TF_IDF.computeWeight(t);
            TrainingDataPreparation.prepareFeatureVector(t);

            double d = -100;
            for (Entry<Integer, SVMClassify> cm : classiferMap.entrySet()){
            	double newd = cm.getValue().classifyText(t);
            	if( d < newd){
            		d = newd;
            		t.categorie = cm.getKey();
            	}
            }
            boolean result = true;//here replace true with the result of your algo
            Prop newAnnotation;
            if(result == true){
                newAnnotation = new Prop(PLUGIN_RESULT_URI+"/sport", "true",true);
            }
            else{
                newAnnotation = new Prop(PLUGIN_RESULT_URI+"/sport", "false",true);

            }
            System.out.println("the item: "+itemContent+"\n "+t+"\n "+ newAnnotation+"\n");
        }
        
    }
    
    public static Item[] getAllItems(){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin("");
    }
    public static Item[] getNotCategorizeItems(int limit){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI, limit);
    }
}
