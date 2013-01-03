package org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight;

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
import org.zoneproject.extractor.plugin.categorization_svm.model.Corpus;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

public class TF_IDF {

    private static double computeIDF(Mot mot) {
        int nbTextMot = 0;
        for (Text t : Corpus.getCorpus()) {

            for (Mot m : t.mots) {

                if (m.Lemma.equals(mot.Lemma)) {
                    nbTextMot++;
                    break;
                }

            }
        }

        if (nbTextMot == 0) {
            return -1;
        }

        return Math.log((double) Corpus.getCorpus().size() / nbTextMot);


    }

    public static void computeWeight(Text t) {

        for (Mot m : t.mots) {

            double tf = (double) m.nbOccuences / t.nbToTMots;
            m.weight = tf * computeIDF(m);
        }
    }
}
