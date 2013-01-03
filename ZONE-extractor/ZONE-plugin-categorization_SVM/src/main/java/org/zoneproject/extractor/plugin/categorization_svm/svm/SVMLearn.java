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

    public SVMLearn() {
        featuredTexts = new ArrayList<Text>();
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

        System.out.println("\nTRAINING SVM-light MODEL ..");
        SVMLightModel model = trainer.trainModel(traindata, tp);
        System.out.println(" DONE.");

        model.writeModelToFile("resources/jni_model.dat");

    }
}
