package org.zoneproject.extractor.plugin.langdetect;

/*
 * #%L
 * ZONE-plugin-LangDetect
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.cybozu.labs.langdetect.LangDetectException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */

public class App 
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_LANG;
    public static int SIM_DOWNLOADS = 100;
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        Item[] items = null;
        String [] deps = {ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT};
        try {
            LangDetect.init("profiles/");
        } catch (LangDetectException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        HashMap<String, ArrayList<Prop>> props; 
        while(true){
            do{
                props = new HashMap<String, ArrayList<Prop>>();
                items = Database.getItemsNotAnotatedForPluginsWithDeps(PLUGIN_URI,deps,SIM_DOWNLOADS);
                if(items == null){
                    continue;
                }
                logger.info("LangDetect has "+items.length+" items to annotate");
                for(Item item : items){
                    String lang = LangDetect.detectLang(item.concat());
                    
                    props.put(item.getUri(), new ArrayList<Prop>());
                    props.get(item.getUri()).add(new Prop(ZoneOntology.PLUGIN_LANG, lang, true,true));
                }
                Database.addAnnotations(props);
            }while(items == null || items.length > 0);
            
            logger.info("done");
            try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
        }
    }
}
