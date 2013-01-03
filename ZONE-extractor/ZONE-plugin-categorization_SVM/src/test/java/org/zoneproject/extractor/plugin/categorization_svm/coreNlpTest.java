/*
 * Copyright 2012 ZONE-project.
 *
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
 */
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

import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.zoneproject.extractor.utils.Prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.zoneproject.extractor.plugin.categorization_svm.model.Corpus;
import org.zoneproject.extractor.plugin.categorization_svm.model.Dictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.TextExtraction;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight.TF;
import org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight.TF_IDF;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMClassify;
import org.zoneproject.extractor.plugin.categorization_svm.svm.SVMLearn;
import org.zoneproject.extractor.plugin.categorization_svm.svm.TrainingDataPreparation;
import org.zoneproject.extractor.plugin.categorization_svm.svm.Verification;
import org.zoneproject.extractor.utils.Item;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class coreNlpTest
    extends TestCase{
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public coreNlpTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


    @org.junit.Test
    public void testApp1() throws Exception {
        try{	     
			SVMClassify.readModel();
			Dictionnaire.readDictionnaireFromFile();
			Corpus.readCorpusFromFile();
			
		    Properties props = new Properties();
			//    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
			    props.put("annotators", "tokenize, ssplit, pos, lemma");
			    TextExtraction Te = new TextExtraction();
			    Te.setProps(props);
				 
			    //effacer les fichiers dans le dossier classed
			    File dirClassedCocoa = new File("svm/classed/money-fx");
			    File [] fileClassedCocoa = dirClassedCocoa.listFiles();
			    for(File f : fileClassedCocoa){
			    	f.delete();
			    }
			    
			    File dirClassedcotton = new File("svm/classed/notInclude");
			    File [] fileClassedcotton = dirClassedcotton.listFiles();
			    for(File f : fileClassedcotton){
			    	f.delete();
			    }
			  
			    //classification
			    		    
			    File dirToClass = new File("svm/toClass");
			    File [] filesToClass = dirToClass.listFiles();
			    for(File f : filesToClass){
			    	
			    	Text t = new Text(new Item(f.getAbsolutePath()));
			    	Te.extractLemmaFromText(t);
			    	TF_IDF.computeWeight(t);
			    	TrainingDataPreparation.prepareFeatureVector(t);
			   
			    	SVMClassify.classifyText(t);
			    	
			    	FileInputStream filesource = new FileInputStream(f.getAbsolutePath());
			    	FileOutputStream  fileDestination = null;
			    	
			    	if(t.categorie == 1){
			      		 fileDestination = new FileOutputStream("svm/classed/money-fx/" + f.getName());
			    	}
			    	else{
			    		fileDestination= new FileOutputStream("svm/classed/notInclude/" + f.getName());
			    	}
		         
			    	byte buffer[]=new byte[512*1024];
		          
			    	int nblecture;
		            while((nblecture=filesource.read(buffer))!=-1){
		               fileDestination.write(buffer,0,nblecture);
		           }
			    }
			    File dirReferencedCocoa = new File("svm/References/money-fx");
			    int x= Verification.countTextInDirectory(dirReferencedCocoa);
				System.out.println("ref "+ x);
				int z= Verification.countTextInDirectory(dirClassedCocoa);
				System.out.println("classed "+ z);
				int y=Verification.countTextCorrectInDirectory(dirReferencedCocoa, dirClassedCocoa);
				System.out.println("correct"+ y);
				double precision = Verification.computePrecision(y,z);
				double recall = Verification.computeRecall(y, x);
				System.out.println("precision=" + precision);
				System.out.println("recall=" + recall);
		   
        }catch(Exception e){
                throw e;
        }        
    }
}
