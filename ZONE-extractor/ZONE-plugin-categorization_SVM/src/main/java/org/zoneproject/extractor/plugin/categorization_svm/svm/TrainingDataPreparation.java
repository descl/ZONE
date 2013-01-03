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
import java.util.Map;
import java.util.Map.Entry;

import org.zoneproject.extractor.plugin.categorization_svm.model.Dictionnaire;
import org.zoneproject.extractor.plugin.categorization_svm.model.Mot;
import org.zoneproject.extractor.plugin.categorization_svm.model.Text;

public class TrainingDataPreparation {

    public static void prepareFeatureVector(Text t) {


        Map<Integer, String> dic = Dictionnaire.getDictionnaire();


        for (Mot iter : t.mots) {
            if (dic.containsValue(iter.Lemma)) {

                for (Entry<Integer, String> dicEntry : dic.entrySet()) {
                    if (dicEntry.getValue().equals(iter.Lemma)) {

                        iter.rankInDic = dicEntry.getKey();
                        t.nbMotsInDictionnaire++;
                        break;
                    }
                }



            } else if (t.isLearning) {
                dic.put(dic.size(), iter.Lemma);
                iter.rankInDic = dic.size() - 1;
            }
        }


    }
}
