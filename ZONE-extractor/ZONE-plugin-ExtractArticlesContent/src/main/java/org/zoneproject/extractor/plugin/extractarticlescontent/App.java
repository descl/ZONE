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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import static org.zoneproject.extractor.plugin.extractarticlescontent.App.propsPendingSave;
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
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT;
    public static String PLUGIN_RESULT_URI = ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_RES;
    public static int SIM_DOWNLOADS = 500;
    public static int SIM_ANNOTATE = 50;
    public static int LIMIT_TIME_FOR_DOWN = 1000;
    private static final String URL_REGEX = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    
    public static HashMap<String, ArrayList<Prop>> propsPendingSave; 
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        LinkedList<Item> itemsPending = new LinkedList<Item>();
        
        LinkedBlockingQueue<AnnotationThread> annotationThreads;
        HashMap<String, ArrayList<Prop>> propsToSave; 
        propsPendingSave = new HashMap<String, ArrayList<Prop>>();
        while(true){
            annotationThreads = new LinkedBlockingQueue<AnnotationThread>();
            
            while(true){//while we can download items

                Item[] items = Database.getItemsNotAnotatedForOnePlugin(PLUGIN_URI,SIM_DOWNLOADS);
                if(items != null && items.length > 0){
                    //check if the item is in annotation process
                    for(Item i : items){
                        boolean exist = false;
                        for(AnnotationThread a: annotationThreads){
                            if(a.item.getUri().equals(i.getUri())){
                                exist = true;
                            }
                        }
                        if(!exist){
                            itemsPending.add(i);
                        }
                    }
                }
                if(itemsPending.isEmpty()){
                    break;
                }

                while(!itemsPending.isEmpty()){
                    
                    //we add new thread until the limit length is thrown
                    while(annotationThreads.size() < SIM_ANNOTATE && !itemsPending.isEmpty()){
                        AnnotationThread newAnnot = new AnnotationThread(itemsPending.removeFirst());
                        newAnnot.start();
                        annotationThreads.add(newAnnot);
                    }
                    
                    //try{
                        //we try to end some terminated threads
                    //synchronized(annotationThreads){
                        for(AnnotationThread a :annotationThreads){
                            if(!a.isAlive()){
                                annotationThreads.remove(a);
                            }else if(a.getDuration() > LIMIT_TIME_FOR_DOWN){
                                a.interrupt();
                            }else if(a.getDuration() > 10){
                                logger.info("is alive["+a.getDuration()+"]: "+a.item.getUri());
                            }
                            //try{Thread.currentThread().sleep(1000);}catch(Exception ie){}//TODO remove
                        }
                    //}
                    //}catch(java.util.ConcurrentModificationException concurrentAccess){
                    //    logger.warn("concurrent access!");
                    //}
                    
                    
                    if(annotationThreads.size()>= SIM_ANNOTATE){
                        try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
                    }
                }
                
                
                logger.info("start saving");
                synchronized(propsPendingSave){
                    propsToSave = (HashMap<String, ArrayList<Prop>>)propsPendingSave.clone();
                    propsPendingSave.clear();
                }
                Database.addAnnotations(propsToSave);
                logger.info("end saving");

            }
            
            logger.info("no more items to annotate");
            try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
        }
    }
}

class AnnotationThread extends Thread  {
    protected Item item;
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    private Date startedDate;

    public AnnotationThread(Item item) {
      this.item = item;
      this.startedDate = new Date();
    }
    public void run() {
        logger.info("Add ExtractArticlesContent for item: "+item.getUri());
        String content = ExtractArticleContent.getContent(item);
        
        synchronized(propsPendingSave){
            App.propsPendingSave.put(item.getUri(), new ArrayList<Prop>());
            App.propsPendingSave.get(item.getUri()).add(new Prop(App.PLUGIN_URI,"true"));

            if(content != null){
                App.propsPendingSave.get(item.getUri()).add(new Prop(App.PLUGIN_RESULT_URI,content));
            }
        }
    }
    public int getDuration(){
        return (int)( ((new Date()).getTime() - startedDate.getTime())/ (1000) );
    }
}