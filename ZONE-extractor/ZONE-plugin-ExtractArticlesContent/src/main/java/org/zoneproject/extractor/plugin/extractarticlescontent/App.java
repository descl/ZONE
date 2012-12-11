package org.zoneproject.extractor.plugin.extractarticlescontent;

/*
 * #%L
 * ZONE-plugin-ExtractArticlesContent
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

import com.hp.hpl.jena.vocabulary.RSS;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.VirtuosoDatabase;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/ExtractArticlesContent";
    public static String PLUGIN_RESULT_URI = "http://www.zone-project.org/plugins/ExtractArticlesContent#result";
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Item[] items = VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI);
        System.out.println("ExtractArticlesContent has "+items.length+" items to annotate");
        for(Item item : items){
            try {
                System.out.println("Add ExtractArticlesContent for item: "+item);
                URL url = new URL(item.getUri());
                String content= ArticleExtractor.INSTANCE.getText(url);
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_RESULT_URI,content));
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
            } catch (BoilerpipeProcessingException ex) {
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_RESULT_URI,item.getElement(RSS.description)));
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
