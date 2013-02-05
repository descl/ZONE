/**
 * class used to parse rss feed
 */
package org.zoneproject.extractor.rssreader;

/*
 * #%L
 * ZONE-RSSreader
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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
public class RSSGetter {
    /**
     * transform a list of rss feeds links to a list of items
     * @param urls pointing to the rss feeds
     * @return the list of items
     */
    public static ArrayList<Item> getFlux(String[] urls) {
        ArrayList<Item> result = new ArrayList<Item>();
        try {
            for(int i=0; i < urls.length;i++){
                result.addAll(RSSGetter.getFlux(urls[i],new XmlReader(new URL(urls[i]))));
                
            }
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    /**
     * get the list of items contained in a rss feed link
     * @param url
     * @return the list of items
     */
    public static ArrayList<Item> getFlux(String url) {
        try {
            return RSSGetter.getFlux(url,new XmlReader(new URL(url)));
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * get the list of items contained in a rss feed file
     * @param file
     * @return 
     */
    public static ArrayList<Item> getFlux(File file) {
        try {
            return getFlux(file.getAbsoluteFile().toString(),new XmlReader(file));
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static ArrayList<Item> getFlux(String source, XmlReader reader){
        ArrayList<Item> items = new ArrayList<Item>();
        
        SyndFeedInput sfi = new SyndFeedInput();
        try {
            SyndFeed feed = sfi.build(reader);
            List entries = new ArrayList();
            entries = feed.getEntries();

            for (int i = 0; i < entries.size(); i++){
                SyndEntry entry = (SyndEntry)entries.get(i);
                
                //create item
                Item cur = new Item(source, entry);
                
                //add item to list
                items.add(cur);
            }
        }
        catch(IllegalArgumentException e) {
            System.out.println("RSS Feed "+e+" not working");
        }
        catch(FeedException e) {
            System.out.println("RSS Feed "+e+" not working");
        }
        return items;
    }
    
    public static String [] getSources(){
        String query = "SELECT *  WHERE {?uri rdf:type <"+ZoneOntology.SOURCES_TYPE+">.}";
        ResultSet res = Database.runSPARQLRequest(query, ZoneOntology.GRAPH_SOURCES);
        ArrayList<String> sources = new ArrayList<String>();
        while (res.hasNext()) {
            QuerySolution r = res.nextSolution();
            sources.add(r.get("?uri").toString());
        }
        return sources.toArray(new String[sources.size()]);
    }
    
    public static void main(String[] args){
        
        String fileURI = "http://europe1.fr.feedsportal.com/c/32376/f/546041/index.rss";
        ArrayList result = RSSGetter.getFlux(fileURI);
        System.out.println(result);
    }
}
