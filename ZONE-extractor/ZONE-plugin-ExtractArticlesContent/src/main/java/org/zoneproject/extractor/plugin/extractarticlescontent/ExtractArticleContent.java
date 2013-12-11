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

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.shared.JenaException;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.InputSource;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.ZoneOntology;

public class ExtractArticleContent {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    private static final String URL_REGEX = "\\(?\\b(http://|www[.]|https://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    private static final String[] notAllowedFormats = {"jpg", "png", "gif", "jpeg","mkv","m4v","mp3","ogg","ogv"};

    public static String getContent(Item item) {
        //if(item.getElements("http://zone-project.org/model/plugins/ExtractArticlesContent#cache").length > 0){
        //    return item.getElements("http://zone-project.org/model/plugins/ExtractArticlesContent#cache")[0];
        //}
        
        if(item.getUri().startsWith("https://twitter.com/") && item.getDescription() != null){
            //try to get links in tweets
            Pattern p = Pattern.compile(URL_REGEX);
            Matcher m = p.matcher(item.getDescription());
            while(m.find()) {
                String urlStr = m.group();
                if (urlStr.startsWith("(") && urlStr.endsWith(")")){
                urlStr = urlStr.substring(1, urlStr.length() - 1);
                }
                item.addElement(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_LINK, urlStr);
            }
        }else{
            item.addElement(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_LINK, item.getUri());
        }
        
        
        String content = "";
        for(String curLink: item.getElements(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_LINK)){
            String curContent = null;
            //if(!curLink.equals(item.getUri())){
            //    logger.info("je cherche dans le cache");
            //    curContent = getInCache(curLink);
            //}
            //if(curContent == null){
                curContent = ExtractArticleContent.getContent(curLink);
            //    storeInCache(curLink, curContent);
            //}

            if(curContent == null)
                continue;
            String title = "abcdefghijklmnopqstuvwxyz";
            if(item.getTitle() != null){
                title = item.getTitle().trim();
            }

            if(item.getDescription() != null){
                String description = item.getDescription().trim().substring(0,Math.min(item.getDescription().trim().length(),20));

                if(description != null && curContent.contains(description)){
                    curContent = curContent.substring(curContent.indexOf(description));
                }
            }

            if(curContent.contains(title)){
                curContent = curContent.substring(curContent.indexOf(title)+title.length());
            }
            curContent = curContent.replace("\n", "<br/>");
            if(!curContent.equals("") && !curContent.equals("<br/>")) {
                content += curContent+"<br/>";
            }
        }
        if(content.equals("")) {
            return null;
        }
        return content;
    }
    public static String getContent(String uri) {
        int timeout = 70000;
        if(uri.startsWith("http://feedproxy.google.com"))
            timeout = 19000;

        try{
            URL url = new URL(java.net.URLDecoder.decode(uri, "UTF-8"));
            HttpURLConnection.setFollowRedirects(true);
            URLConnection conn = url.openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setDefaultUseCaches(true);
            
            //follow redirects
            do{
                url = conn.getURL();
                //check if the url is only an image
                for(String extension: notAllowedFormats){
                    if(url.toString().toLowerCase().endsWith(extension)){
                        return "";
                    }
                }
                conn = (url).openConnection();
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setDefaultUseCaches(true);
                if(!conn.getURL().toString().contains("t.co/")){
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                }
                conn.connect();
                conn.getInputStream();
            }while(!conn.getURL().equals(url));
            
            return ArticleExtractor.INSTANCE.getText(new InputSource(conn.getInputStream())).replace("\u00A0", " ").trim()+"\n";
            
        }catch(java.io.FileNotFoundException ex){
            logger.warn("annotation process because of download error for "+uri);
            return "";
        }catch(java.io.IOException ex){
            logger.warn("annotation process because of download error for "+uri+" "+ ex.getLocalizedMessage());
            return "";
        }catch(BoilerpipeProcessingException ex){
            logger.warn("annotation process because of boilerpipe "+uri+" "+ ex.getLocalizedMessage());
            return null;
            
        }catch(java.lang.IllegalArgumentException ex){
            logger.warn("malformed uri "+uri+" "+ ex.getLocalizedMessage());
            return null;
            
        }catch(java.lang.OutOfMemoryError ex){
            logger.warn("oufOfMemory error uri "+uri+" "+ ex.getLocalizedMessage());
            return null;
        }catch(java.lang.ArrayIndexOutOfBoundsException ex){
            logger.warn("outOfBoundsException error uri "+uri+" "+ ex.getLocalizedMessage());
            return null;
            
        }
    }
    
    public static String getInCache(String uri){
        String request = "SELECT ?content WHERE{ <"+uri+"> <"+ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_CACHE+"> ?content.}LIMIT 1";
        ResultSet results;
        try{
            results = Database.runSPARQLRequest(request,ZoneOntology.GRAPH_EAC);
            
        }catch(JenaException ex){
            return null;
        }
        
        //we store all the results in a HashMap for each item
        QuerySolution result;
        while (results.hasNext()) {
            result = results.nextSolution();
            return result.get("?content").toString();
        }
        return null;
    }
    
    public static void storeInCache(String uri, String content){
        Prop p = new Prop(ZoneOntology.PLUGIN_EXTRACT_ARTICLES_CONTENT_CACHE,content);
        Database.addAnnotation(uri,p,ZoneOntology.GRAPH_EAC);
    }
        
    public static void main(String[] args) throws MalformedURLException, IOException, BoilerpipeProcessingException{
        logger.info("start tests");
        String url = "http://feedproxy.google.com/~r/Websourcingfr-LeBlog/~3/qeyE0kVcy1k/";
        url = "http://feedproxy.google.com/~r/projectshrink/~3/x3ldmHBQY1I/jokers-clowns-7797.html";
        url = "http://feedproxy.google.com/~r/artofphotography/~3/62VRP4YhI8Q/aop-156.m4v";
        //Item item = Database.getOneItemByURI(url);
        //Prop[] pl = {new Prop("http://zone-project.org/model/plugins/ExtractArticlesContent#cache", "?cache"};
       // Item[] list = Database.getItemsNotAnotatedForPluginsWithDepsAndCache("http://test", pl, 1);
        //logger.info(list[0]);
        logger.info(getContent(url));
        /*logger.info(item);
        String cacheValue = getInCache(url);
        if(cacheValue == null){
            String val;
            val = ExtractArticleContent.getContent(url);
            storeInCache(url, val);
        }
        logger.info(getInCache(url));*/
        
    }
}