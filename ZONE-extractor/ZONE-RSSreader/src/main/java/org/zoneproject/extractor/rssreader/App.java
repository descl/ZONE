package org.zoneproject.extractor.rssreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;

/*
 * #%L
 * ZONE-RSSreader
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

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    
    public static int SIM_DOWNLOADS = 100;
    public static int LIMIT_TIME_FOR_DOWN = 60;
    
    public static final ArrayList<Item> itemsPendingSave = new ArrayList<Item>(); 
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        LinkedBlockingQueue<DownloadNewsThread> feedsThreads = new LinkedBlockingQueue<DownloadNewsThread>();
        LinkedList<String> itemsPending = new LinkedList<String>();
        
        DownloadLastsNewsThread lastsThread = new DownloadLastsNewsThread();
        lastsThread.start();
        
        while(true){
            String [] sources = RSSGetter.getSources();
            itemsPending.add(sources[0]);
            itemsPending.addAll(Arrays.asList(sources));
            
            while(!itemsPending.isEmpty()){
                    //we add new thread until the limit length is thrown
                    while(feedsThreads.size() < SIM_DOWNLOADS && !itemsPending.isEmpty()){
                        DownloadNewsThread newAnnot = new DownloadNewsThread(itemsPending.removeFirst());
                        newAnnot.start();
                        feedsThreads.add(newAnnot);
                    }
                    
                    for(DownloadNewsThread a : feedsThreads){
                        if(!a.isAlive()){
                            feedsThreads.remove(a);
                        }else if(a.getDuration() > LIMIT_TIME_FOR_DOWN){
                            a.interrupt();
                        }else if(a.getDuration() > 10){
                            logger.info("is alive["+a.getDuration()+"]: "+a.source);
                        }
                    }
                    if(feedsThreads.size()>= SIM_DOWNLOADS){
                        try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
                    }
                
                logger.info("["+itemsPending.size()+"] feeds to downloaded");
                if(itemsPendingSave.size() > 30){
                    App.saveItems();
                }
            }
            
            App.saveItems();
        }
    }
    public static final void saveItems(){
        synchronized(itemsPendingSave){
            logger.info("start save datas");
            Database.addItems((ArrayList<Item>) itemsPendingSave.clone());
            itemsPendingSave.clear();
            logger.info("end save datas");
        }
    }
}


class DownloadNewsThread extends Thread  {
    protected String source;
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    private Date startedDate;

    public DownloadNewsThread(String source) {
      this.source = source;
      this.startedDate = new Date();
    }
    public void run() {
        //random sleep in order to have a slower process
        long waitTime = Math.round(Math.random()*2000);
        try{Thread.currentThread().sleep(waitTime);}catch(Exception ie){}
        
        //Starting rss downloading
        ArrayList<Item> items = RSSGetter.getFlux(source);

        //Cleaning flow with already analysed items
        Database.verifyItemsList(items);

        //Printing result items
        //for(int i=0; i< items.size();i++)logger.info("\n"+items.get(i));
        
        //saving to 4Store database
        App.itemsPendingSave.addAll(items);
        //Database.addItems(items);     
        //TODO better!
        logger.info("["+items.size()+"] Done for source: "+source);
    }
    public int getDuration(){
        return (int)( ((new Date()).getTime() - startedDate.getTime())/ (1000) );
    }
}


class DownloadLastsNewsThread extends Thread  {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);

    public DownloadLastsNewsThread() {
    }
    public void run() {
        String lastRss = RSSGetter.getLastsSources(1)[0];
        String [] sources;
        while(true){
            sources = RSSGetter.getLastsSources(50);
            for(String cur: sources){
                if(cur.equals(lastRss)){
                    break;
                }
                logger.info("QUICKANNOTATE:"+cur);
                DownloadNewsThread th = new DownloadNewsThread(cur);
                th.start();
                try {
                    th.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(DownloadLastsNewsThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                App.saveItems();
            }
            if(sources.length>0){
                lastRss = sources[0];
            }
            try{Thread.currentThread().sleep(1000);}catch(Exception ie){}
        }
  }
}
