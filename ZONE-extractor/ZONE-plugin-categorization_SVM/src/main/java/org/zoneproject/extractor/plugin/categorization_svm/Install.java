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

import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

/*
 * RUN the installer:  mvn install -pl :ZONE-plugin-categorization_SVM
 */
public class Install {
    public static void main(String[] args) {
        System.out.println("Training the SVM");
        
        //get all items corresponding to the sport category
        String source = "http://fr.news.yahoo.com/rss/sports";
        Item[] items = Database.getItemsFromSource(source);
        
        //retreive the news content for SVM
        for(Item i: items)
            System.out.println("Article text:"+i.concat());
        
        System.out.println(items.length+" corresponding to sport category");
        
        //get all item corresponding to an other category
        Item[] otherItems = Database.getItemsFromSource("http://fr.news.yahoo.com/monde/?format=rss");
        
        System.out.println("Number of items for others:"+otherItems.length);
    }
}