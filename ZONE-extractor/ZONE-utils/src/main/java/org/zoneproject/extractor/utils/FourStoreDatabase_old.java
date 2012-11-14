/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.extractor.utils;

import org.zoneproject.extractor.utils.FourStore.Store;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author cdesclau
 */
public class FourStoreDatabase {
    private static Store st = null;
    private static String uri = Config.getVar("FourStore-server");
    public static String ZONE_URI = "http://demo.zone-project.org/data";
    
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
        String response="";
        try {
            return getStore().query(queryString);
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
    public static void main(String[] args){
        System.out.println(getItemsNotAnotatedForOnePlugin("http://www.wikimeta.org/Entities#LOC"));
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

}
