/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.extractor;

import java.util.Timer;

/**
 *
 * @author cdesclau
 */
public class App {
    
    public static void main(String[] args)  {
       Timer timer = new Timer();

       String [] apps = new String[4];
       
       apps[0] = "org.zoneproject.extractor.rssreader.App";
       apps[1] = "org.zoneproject.extractor.plugin.extractarticlescontent.App";
       apps[2] = "org.zoneproject.extractor.plugin.opencalais.App";
       apps[3] = "org.zoneproject.extractor.plugin.wikimeta.App";
       for(String app : apps){
           System.out.println("Starting annotation process for "+app);
           timer.scheduleAtFixedRate( new ThreadApp(app),0, 3000); 
       
       }
    }
}