package org.zoneproject.extractor.plugin.categorization_svm.preprocessing.weight;

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
