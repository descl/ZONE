package org.zoneproject.extractor.plugin.categorization_svm;

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
    	Map<String, SVMClassify> classiferMap = new HashMap<String, SVMClassify>();
    	classiferMap.put("informatique", new SVMClassify("informatique"));
    	classiferMap.put("sport", new SVMClassify("sport"));
    	classiferMap.put("sante", new SVMClassify("medecine"));
    	classiferMap.put("economie", new SVMClassify("economie"));
    	classiferMap.put("science", new SVMClassify("science"));
    	
    	        
    	for (Entry<String, SVMClassify> cm : classiferMap.entrySet()){
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
        Item[] itemsNotAnnotated = Database.getItemsFromSource("http://www.france24.com/fr/la_une/rss");
        System.out.println("Number of items not annotated:"+itemsNotAnnotated.length);
        
        for(Item item : itemsNotAnnotated){
            String itemContent = item.concat();
            //you should run classification on itemContent
            Text t = new Text(item);
            Te.extractLemmaFromText(t);
            TF_IDF.computeWeight(t);
            TrainingDataPreparation.prepareFeatureVector(t);

            double d = -100;
            Prop newAnnotation = null;
            for (Entry<String, SVMClassify> cm : classiferMap.entrySet()){
                double newd = cm.getValue().classifyText(t);
                if( d < newd){
                    d = newd;
                    newAnnotation = new Prop(ZoneOntology.PLUGIN_SVM_RES,cm.getKey(),true);
                }
            }
            if(d != -100)
                VirtuosoDatabase.addAnnotation(item.getUri(), newAnnotation);
            
            //System.out.println("the item: "+itemContent+"\n "+t+"\n "+ newAnnotation+"\n");
        }
        
    }
    
    public static Item[] getAllItems(){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin("");
    }
    public static Item[] getNotCategorizeItems(int limit){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI, limit);
    }
}
