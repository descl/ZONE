package org.zoneproject.extractor.plugin.categorization_svm.svm;

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

import jnisvmlight.FeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

public class SVMClassify {

	
	public static void classifyText(Text text) throws Exception{
		try{

	

		    // Sort all feature vectors in ascedending order of feature dimensions
		    // before training the model
		    SVMLightInterface.SORT_INPUT_VECTORS = true;
		    
		      // Store dimension/value pairs in new LabeledFeatureVector object

	    	int[] dim= new int[text.mots.size()];
	    	double[] value= new double[text.mots.size()];
	    	int j=0;
	    	for(Mot f : text.mots){
	    		dim[j] = f.rankInDic;
	    		value[j] = f.weight;
	    		j++;
	
	    	}
	    
	    	FeatureVector data = new FeatureVector(dim, value);
	        // Use cosine similarities (LinearKernel with L2-normalized input vectors)
	    	data.normalizeL2();
	    	
	    	
	    	SVMLightModel model = SVMLightModel.readSVMLightModelFromURL(new java.io.File("svm/jni_model.dat").toURI().toURL());
	    	
		    //SVMLightInterface classifier = new SVMLightInterface();
	    	//double d = classifier.classifyNative(data);
	    	double d = model.classify(data);
	    	
	    	if (d > 0 ){
	    		text.categorie = 1;
	    	}
	    	else{
	    		text.categorie = -1;
	    	}

			
		}catch(Exception e)
		{
			throw e;
		}

		
	}
}
	

