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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.zoneproject.extractor.utils.Item;
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
    public static int SIM_DOWNLOADS = 50;
    
    private static final String URL_REGEX = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    
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
        
        while(true){
            do{
                items = VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI,SIM_DOWNLOADS);
                th = new DownloadThread[items.length];
                if(items == null){
                    continue;
                }
                logger.info("ExtractArticlesContent has "+items.length+" items to annotate");
                Item curItem;
                for(int curItemId = 0; curItemId < items.length ; curItemId++){
                    curItem = items[curItemId];

                    if(curItem.getUri().startsWith("https://twitter.com/")){
                        //try to get links in tweets
                        Pattern p = Pattern.compile(URL_REGEX);
                        Matcher m = p.matcher(curItem.getDescription());
                        String url;
                        while(m.find()) {
                            String urlStr = m.group();
                            if (urlStr.startsWith("(") && urlStr.endsWith(")")){
                            urlStr = urlStr.substring(1, urlStr.length() - 1);
                            }
                            url= urlStr;
                            curItem.addElement(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_LINK, urlStr);
                        }
                    }else{
                        curItem.addElement(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_LINK, curItem.getUri());
                    }


                    th[curItemId] = new DownloadThread(curItem);
                    th[curItemId].start();
                }

                for(int curItemId = 0; curItemId < items.length ; curItemId++){
                    try {
                        if(th[curItemId] == null)continue;
                        th[curItemId].join();
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