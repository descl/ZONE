package org.zoneproject.extractor.plugin.categorization_svm.model;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.zoneproject.extractor.utils.Item;

public class Text implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public int categorie;
    public transient Item item;
    public boolean isLearning;
    public List<Mot> mots;
    public int nbToTMots;
    public int nbMotsInDictionnaire;

    public Text(Item i) {
        this(i, 0, false);
    }

    public Text(Item i, int categorie, boolean isLearning) {

        this.item = i;
        this.categorie = categorie;
        this.isLearning = isLearning;
        this.nbToTMots = 0;
        this.nbMotsInDictionnaire = 0;
        mots = new ArrayList<Mot>();
    }

    @Override
    public String toString() {
        return "{Text: {categorie=" + this.categorie + ", text=" + item.concat() + ", "
                + "mots=" + mots + "}}";
    }
}
