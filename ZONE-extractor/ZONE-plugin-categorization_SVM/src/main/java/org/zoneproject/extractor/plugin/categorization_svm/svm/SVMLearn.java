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
import java.util.ArrayList;
import java.util.List;

import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

public class SVMLearn {

    private List<Text> featuredTexts;
    private String categorie;

    public SVMLearn(String iCat) {
        featuredTexts = new ArrayList<Text>();
        categorie = iCat;
    }

    public List<Text> getFeaturedTexts() {
        return featuredTexts;
    }

    public void setFeaturedTexts(List<Text> featuredTexts) {
        this.featuredTexts = featuredTexts;
    }

    public void addFeaturedText(Text Tx) {
        this.featuredTexts.add(Tx);
    }

    public void learn() {

        // The trainer interface with the native communication to the SVM-light 
        // shared libraries
        SVMLightInterface trainer = new SVMLightInterface();

        // The training data
        LabeledFeatureVector[] traindata = new LabeledFeatureVector[featuredTexts.size()];

        // Sort all feature vectors in ascedending order of feature dimensions
        // before training the model
        SVMLightInterface.SORT_INPUT_VECTORS = true;

        // Store dimension/value pairs in new LabeledFeatureVector object
        int i = 0;
        for (Text iter : featuredTexts) {
            int[] dim = new int[iter.mots.size()];
            double[] value = new double[iter.mots.size()];
            int j = 0;
            for (Mot iterMot : iter.mots) {
                dim[j] = iterMot.rankInDic;
                value[j] = iterMot.weight;
                j++;

            }
            traindata[i] = new LabeledFeatureVector(iter.categorie, dim, value);
            // Use cosine similarities (LinearKernel with L2-normalized input vectors)
            traindata[i].normalizeL2();
            i++;
        }


        // Initialize a new TrainingParamteres object with the default SVM-light
        // values
        TrainingParameters tp = new TrainingParameters();

        // Switch on some debugging output
        tp.getLearningParameters().verbosity = 1;
        tp.getLearningParameters().svm_costratio = 10;

        System.out.println("\nTRAINING SVM-light MODEL ..");
        SVMLightModel model = trainer.trainModel(traindata, tp);
        System.out.println(" DONE.");

        model.writeModelToFile("resources/jni_model_"+categorie+".dat");

    }
}
