package org.zoneproject.extractor.utils;

/*
 * #%L
 * ZONE-utils
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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zoneproject.extractor.utils.FourStore.Store;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public abstract class FourStoreDatabase{
    private static Store st = null;
    private static String uri = Config.getVar("FourStore-server");
    public static String ZONE_URI = ZoneOntology.GRAPH_NEWS;
    
    public static Store getStore(){
        try {
            if(st == null){
                
                    st = new Store(uri);
            }
            return st;
        } catch (MalformedURLException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void addItems(ArrayList<Item> items){
        addItems(items.toArray(new Item[items.size()]));
    }
    
    public static void addItems(Item[] items){
        for(int i=0; i < items.length;i++){
            addItem(items[i]);
        }
    }

    public static void addItem(Item item){
        Iterator it = item.values.iterator();
        while (it.hasNext()){
            addAnnotation(item.getUri(), (Prop)it.next());
        }
    }
    
    public static void addAnnotations(String itemUri, ArrayList<Prop> prop){
        for(int i=0; i< prop.size();i++){
            FourStoreDatabase.addAnnotation(itemUri, prop.get(i));
        }
    }
    
    public static void addAnnotation(String itemUri, Prop prop){
        Model model = ModelFactory.createDefaultModel();
        Resource itemNode = model.createResource(itemUri);
        if(prop.isLiteral()){
            itemNode.addLiteral(prop.getType(), model.createLiteral(prop.getValue()));
        }
        else{
            itemNode.addProperty(prop.getType(), model.createResource(prop.getValue()));
        }

        String response="";
        try {
            getStore().insertModel(model, ZONE_URI);
        } catch (IOException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Run a SPARQL request on the EndPoint
     * @param queryString the SPARQL request
     * @return the xml result as a String
     */
    public static String runSPARQLRequest(String queryString){
        return runSPARQLRequest(queryString, Store.OutputFormat.SPARQL_XML);
    }
    
    public static String runSPARQLRequest(String queryString, Store.OutputFormat format){
        String response="";
        try {
            return getStore().query(queryString, format);
        } catch ( IOException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    /**
     * get all items which has not been annotated for a plugin
     * @param pluginURI the plugin URI
     * @return the items
     */
    public static Item[] getItemsNotAnotatedForOnePlugin(String pluginURI){
        ArrayList<Item> items = new ArrayList<Item>();
        String request = "SELECT ?uri WHERE{  ?uri <http://purl.org/rss/1.0/title> ?title  OPTIONAL {?uri <"+pluginURI+"> ?pluginDefined} FILTER (!bound(?pluginDefined)) }";
        String result = runSPARQLRequest(request);
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc;
            InputSource is = new InputSource(new StringReader(result));
            doc = builder.parse(is);
            NodeList allElements = doc.getElementsByTagName("uri");
            for(int i=0; i < doc.getElementsByTagName("uri").getLength(); i++){
                Node cur = allElements.item(i);
                items.add(getOneItemByURI(cur.getTextContent()));
            }
            
        } catch (ParserConfigurationException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return items.toArray(new Item[items.size()]);
    }
    public static void main(String[] args) throws FileNotFoundException, IOException{
        loadFile("","/home/cdesclau/Work/v2/ZONE-extractor/ZONE-plugin-INSEEGeo/target/resources/arrondissements-06-2011.rdf");
    }
    
    /**
     * Get an Item from the Database
     * @param uri
     * @return 
     */
    public static Item getOneItemByURI(String uri){
        String request = "SELECT ?relation ?value WHERE{  <"+uri+"> ?relation ?value}";
        String result = runSPARQLRequest(request);
        return new Item(uri,result);
    }
    
    public static boolean ItemURIExist(String uri){
        String queryString = "ASK { <"+uri+"> <http://purl.org/rss/1.0/title> ?z}";
        String response1="";
        try {
            response1 = getStore().query(queryString);
            return response1.contains("<boolean>true</boolean>");
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ProtocolException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static void verifyItemsList(ArrayList<Item> items){
        for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
            Item o = iterator.next();
            if (ItemURIExist(o.getUri())) {
                iterator.remove();
            }
        }
    }
    
    public static void deleteItem(String uri) throws IOException{
        Item item = getOneItemByURI(uri);
        getStore().deleteModel(item.getModel(), ZONE_URI);
    }
    
    public static void loadFolder(String graphURI,String dir){
        File file = new File(dir);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() == true) {
                    try {
                        loadFile(graphURI,files[i].getAbsolutePath());
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.WARNING, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.WARNING, null, ex);
                    }
                } else if(files[i].isDirectory() == true){
                    loadFolder(graphURI,files[i].getAbsolutePath());
                }
            }
        }
    }
    
    public static void loadFile(String graphURI,String uri) throws FileNotFoundException, IOException{

        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(uri));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        getStore().add(graphURI, 
                fileData.toString(), 
                Store.InputFormat.XML);
    }

}
