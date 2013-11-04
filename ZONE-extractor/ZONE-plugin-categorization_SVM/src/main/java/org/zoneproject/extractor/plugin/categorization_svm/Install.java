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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.plugin.categorization_svm.model.Corpus;
import org.zoneproject.extractor.plugin.categorization_svm.model.Dictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.StopWords;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.TextExtraction;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight.TF_IDF;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMLearn;
import org.zoneproject.extractor.plugin.categorization_svm.svm.TrainingDataPreparation;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

/*
    * RUN the installer:  mvn install -pl ZONE-plugin-categorization_SVM
 */
public class Install {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    public static void main(String[] args) throws IOException {
        Map<String, ArrayList> urlCategories = new HashMap<String, ArrayList>();
        String request =
                " Select DISTINCT ?source ?theme { " +
                "    ?source rdf:type  <http://zone-project.org/model/sources#Source>.  " +
                "    ?source <http://zone-project.org/model/sources#theme> ?theme. " +
                "    FILTER(str(?theme) != \"\") " +
                "}";
        ResultSet sources = Database.runSPARQLRequest(request, ZoneOntology.GRAPH_SOURCES);
        while (sources.hasNext()) {
            QuerySolution result = sources.nextSolution();
            String theme = result.get("?theme").toString();
            String uri = result.get("?source").toString();
            if(!urlCategories.containsKey(theme)){
                urlCategories.put(theme, new ArrayList<String>());
            }
            urlCategories.get(theme).add(uri);
        }
        
        //we clean the datas comming from previous learning phase
        Install.supressInstallfiles();
        
        //we start the learning process
        TextExtraction Te = new TextExtraction();

        Map<String, List<Text>> CatTextMap = new HashMap<String, List<Text>>();
        
        //extraction des données
        for (Entry<String, ArrayList> urlCat : urlCategories.entrySet()){
            ArrayList<Item> itemsCat = new ArrayList<Item>();
            for(Object source : urlCat.getValue()){
                ArrayList<Item> aa = Database.getItemsFromSource(source.toString());
                itemsCat.addAll(aa);
            }
            
            logger.info(itemsCat.size()+" corresponding to "+ urlCat.getKey() +" category");

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
            		
        		if (catTextsInternal.getKey().equals(catTexts.getKey())){
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
    }

    private static void supressInstallfiles() {
        FilenameFilter filefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".dat");
            }
        };
            File installDir = new File(Install.class.getResource("/").getFile());

            for (String fileName : installDir.list(filefilter)) {
                Logger.getLogger(Install.class.getName()).log(Level.INFO, null, "delete datas file "+installDir+"/"+fileName);
                new File(installDir+"/"+fileName).delete();
            }
    }
}
