package org.zoneproject.extractor.plugin.spotlight;

/*
 * #%L
 * ZONE-plugin-Spotlight
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
import java.util.ArrayList;
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
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_SPOTLIGHT;
    public static int SIM_DOWNLOADS = 10;
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        Item[] items = null;
        Prop [] deps = {new Prop(ZoneOntology.PLUGIN_LANG, "\"fr\"")};
        
        AnnotationThread[] th;
        while(true){
            do{
                items = Database.getItemsNotAnotatedForPluginsWithDeps(PLUGIN_URI,deps,SIM_DOWNLOADS);
                if(items == null){
                    continue;
                }
                th = new AnnotationThread[items.length];

                logger.info("Spotlight has "+items.length+" items to annotate");
                for(int curItem = 0; curItem < items.length ; curItem++){
                    th[curItem] = new AnnotationThread(items[curItem]);
                    th[curItem].start();
                }
                for(int curItem = 0; curItem < items.length ; curItem++){
                    try {
                        if(th[curItem] == null)continue;
                        th[curItem].join();
                    } catch (InterruptedException ex) {
                    logger.warn(ex);
                    }
                }

            }while(items == null || items.length > 0);
            logger.info("done");
            try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
            
        }
    }
}


class AnnotationThread extends Thread  {
    private Item item;
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);

    public AnnotationThread(Item item) {
        this.item = item;
    }
    public void run() {
        logger.info("[-] Start for item: "+item.getUri());
        //try {Thread.currentThread().sleep(5000);} catch (InterruptedException ex1) {}
        
        //Starting annotations downloading
        ArrayList<Prop> content= SpotlightRequest.getProperties(item);

        if(content != null){
            Database.addAnnotation(item.getUri(), new Prop(App.PLUGIN_URI,"true"));
            Database.addAnnotations(item.getUri(), content);
        }else{
            logger.warn("Error while annotating" + item.getUri());
        }
    }
}