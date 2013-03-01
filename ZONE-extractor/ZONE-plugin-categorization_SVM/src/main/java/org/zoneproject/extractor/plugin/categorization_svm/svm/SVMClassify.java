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
import java.net.MalformedURLException;
import java.text.ParseException;

import jnisvmlight.FeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

public class SVMClassify {
	
	public SVMClassify(String iCat){
		categorie = iCat;
	}

    private String categorie;
	private SVMLightModel model;

    public void readModel() {
        try {
            model = SVMLightModel.readSVMLightModelFromURL(new java.io.File("resources/jni_model_"+categorie+".dat").toURI().toURL());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double classifyText(Text text) {
        // Sort all feature vectors in ascedending order of feature dimensions
        // before training the model
        SVMLightInterface.SORT_INPUT_VECTORS = true;

        // Store dimension/value pairs in new LabeledFeatureVector object

        int[] dim = new int[text.nbMotsInDictionnaire];
        double[] value = new double[text.nbMotsInDictionnaire];
        int j = 0;
        for (Mot f : text.mots) {
            if (f.rankInDic != 0) {
            	dim[j] = f.rankInDic;
                value[j] = f.weight;
                j++;
            }
        }


        FeatureVector data = new FeatureVector(dim, value);
        // Use cosine similarities (LinearKernel with L2-normalized input vectors)
        data.normalizeL2();




        //SVMLightInterface classifier = new SVMLightInterface();
        //double d = classifier.classifyNative(data);
        double d = model.classify(data);
       /* if (d > 0) {
            text.categorie = 1;
        } else {
            text.categorie = -1;
        }*/
        
        return d;
    }
}
