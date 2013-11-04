package org.zoneproject.extractor.plugin.extractarticlescontent;

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
public class ExtractArticleContent {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    private static final String URL_REGEX = "\\(?\\b(http://|www[.]|https://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

    public static String getContent(Item item) throws MalformedURLException, IOException, BoilerpipeProcessingException{
        if(item.getUri().startsWith("https://twitter.com/")){
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
            
            String curContent = getInCache(curLink);
            if(curContent == null){
                String val;
                curContent = ExtractArticleContent.getContent(curLink);
                storeInCache(curLink, curContent);
            }

            String title = "abcdefghijklmnopqstuvwxyz";
            if(item.getTitle() != null){
                title = item.getTitle().trim();
            }

            if(item.getDescription() != null){
                String description = item.getDescription().trim().substring(0,Math.min(item.getDescription().trim().length(),20));

                if(curContent.contains(description)){
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
    public static String getContent(String uri) throws MalformedURLException, IOException, BoilerpipeProcessingException{
        try{
            URL url = new URL(java.net.URLDecoder.decode(uri, "UTF-8"));
            HttpURLConnection.setFollowRedirects(true);
            URLConnection conn = url.openConnection();
                conn.setConnectTimeout(70000);
                conn.setReadTimeout(70000);
            
            //follow redirects
            do{
                url = conn.getURL();
                conn = (url).openConnection();
                conn.setConnectTimeout(70000);
                conn.setReadTimeout(70000);
                if(!conn.getURL().toString().contains("t.co/")){
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                }
                conn.connect();
                conn.getInputStream();
            }while(!conn.getURL().equals(url));
            
            String urlString = conn.getURL().toString().toLowerCase();
            
            //check if the url is only an image
            if(urlString.endsWith("png")|| urlString.endsWith("jpg") || urlString.endsWith("gif") || urlString.endsWith("jpeg") )
                return "";

            return ArticleExtractor.INSTANCE.getText(new InputSource(conn.getInputStream())).replace("\u00A0", " ").trim()+"\n";
        
        }catch(java.io.FileNotFoundException ex){
            logger.warn("annotation process because of download error for "+uri);
            return "";
        }catch(java.io.IOException ex){
            logger.warn("annotation process because of download error for "+uri+" "+ ex.getLocalizedMessage());
            return "";
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
        String url = "http://www.leparisien.fr/faits-divers/paris-le-domicile-de-sebastien-bazin-l-ancien-president-du-ps-cambriole-04-11-2013-3285531.php";
        String cacheValue = getInCache(url);
        if(cacheValue == null){
            String val;
            val = ExtractArticleContent.getContent(url);
            storeInCache(url, val);
            
        }
            
        logger.info(getInCache(url));
        
    }
}