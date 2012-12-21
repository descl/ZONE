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

import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.VirtuosoDatabase;
/*
 * Start categorization: mvn exec:java -pl :ZONE-plugin-categorization_SVM
 */
public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/Categorization_SVM";
    public static String PLUGIN_RESULT_URI = "http://www.zone-project.org/plugins/Categorization_SVM#result";
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        //retreive all items not annotated
        
        //set the items number limit
        int limit = 100;
        Item[] itemsNotAnnotated = getNotCategorizeItems(limit);
        System.out.println("Number of items not annotated:"+itemsNotAnnotated.length);
        
        for(Item item : itemsNotAnnotated){
            String itemContent = item.concat();
            //you should run classification on itemContent
            
            boolean result = true;//here replace true with the result of your algo
            Prop newAnnotation;
            if(result == true){
                newAnnotation = new Prop(PLUGIN_RESULT_URI+"/sport", "true",true);
            }
            else{
                newAnnotation = new Prop(PLUGIN_RESULT_URI+"/sport", "false",true);

            }
            System.out.println("the item: "+itemContent+" has new annotation:"+ newAnnotation);
        }
        
    }
    
    public static Item[] getAllItems(){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin("");
    }
    public static Item[] getNotCategorizeItems(int limit){
        return VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI, limit);
    }
}
