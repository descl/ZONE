package org.zoneproject.extractor.plugin.extractarticlescontent;

/*
 * #%L
 * ZONE-plugin-ExtractArticlesContent
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.VirtuosoDatabase;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT;
    public static String PLUGIN_RESULT_URI = ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_RES;
    public static int SIM_DOWNLOADS = 100;
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Item[] items;
        DownloadThread[] th;
        
        do{
            items = VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI,SIM_DOWNLOADS);
            th = new DownloadThread[items.length];
            logger.info("ExtractArticlesContent has "+items.length+" items to annotate");
            for(int curItem = 0; curItem < items.length ; curItem++){
                VirtuosoDatabase.addAnnotation(items[curItem].getUri(), new Prop(App.PLUGIN_URI,"true"));

                if(!items[curItem].uri.startsWith("https://twitter.com/")){
                    th[curItem] = new DownloadThread(items[curItem]);
                    th[curItem].start();
                }
            }
            
            for(int curItem = 0; curItem < items.length ; curItem++){
                try {
                    if(th[curItem] == null)continue;
                    th[curItem].join();
                } catch (InterruptedException ex) {
                logger.warn(ex);
                }
            }
            
        }while(items.length > 0);
    }
    private static void startThread(Item item) {
    }
}
