package org.zoneproject.extractor.plugin.categorization_svm.svm;

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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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

    public void readModel(){
        try {
            model = SVMLightModel.readSVMLightModelFromURL(SVMClassify.class.getResource("/svm_model_"+categorie+".dat").toURI().toURL());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e){
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
