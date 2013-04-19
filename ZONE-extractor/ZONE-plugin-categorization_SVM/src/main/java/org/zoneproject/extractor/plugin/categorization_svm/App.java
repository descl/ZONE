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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
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
        for( String cat : App.getCategories()){
            classiferMap.put(cat, new SVMClassify(cat));
        }

    	for (Entry<String, SVMClassify> cm : classiferMap.entrySet()){
    		cm.getValue().readModel();    		
    	}

        TextExtraction Te = new TextExtraction();

        //retreive all items not annotated
        ArrayList<Item> itemsNotAnnotated = new ArrayList<Item>();
        String request = "SELECT ?uri WHERE{"
                + "?uri <http://purl.org/rss/1.0/title> ?title. "
                + "?uri <"+ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT+"> ?dependance. "
                + "?uri <http://purl.org/rss/1.0/source> ?source. "
                + "GRAPH <"+ZoneOntology.GRAPH_SOURCES+"> {?source <"+ZoneOntology.SOURCES_THEME+"> ?theme. }"
                + "OPTIONAL {?uri <"+PLUGIN_URI+"> ?pluginDefined.  "
                + "} FILTER (!bound(?pluginDefined) && str(?theme) = \"\") }";
        logger.info(request);
        ResultSet results = Database.runSPARQLRequest(request);

        while (results.hasNext()) {
            QuerySolution result = results.nextSolution();
            itemsNotAnnotated.add(Database.getOneItemByURI(result.get("?uri").toString()));
        }
        
        logger.info("Number of items not annotated:"+itemsNotAnnotated.size());

        for(Item item : itemsNotAnnotated){
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
                    newAnnotation = new Prop(ZoneOntology.PLUGIN_SVM_RES,cm.getKey(),true,true);
                }
            }
            if(d != -100){
                Database.addAnnotation(item.getUri(), newAnnotation);
            }
            Database.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
        }
    }
    
    public static Item[] getAllItems(){
        return Database.getItemsNotAnotatedForOnePlugin("");
    }
    public static Item[] getNotCategorizeItems(int limit){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI, limit);
    }
    private static String[] getCategories() {
        FilenameFilter filefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat") && name.startsWith("svm_model");
            }
        };
        File installDir = new File(Install.class.getResource("/").getFile());
        ArrayList <String> categories= new ArrayList<String>();
        for (String fileName : installDir.list(filefilter)) {
            categories.add(fileName.substring("svm_model".length()+1,fileName.length() -  ".dat".length()));
        }
        return categories.toArray(new String[categories.size()]);
    }
}
