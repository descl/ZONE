package org.zoneproject.extractor.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RSS;
import com.sun.syndication.feed.synd.SyndEntry;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
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

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Item {
    public String uri;
    public ArrayList<Prop> values;

    public Item(){
        this("");
    }
    
    public Item(SyndEntry entry){
        this(entry.getLink(),entry.getTitle(),entry.getDescription().getValue(),entry.getPublishedDate());
    }
    
    public Item(String uri){
        this.uri = uri;
        values = new ArrayList<Prop>();
    }
    
    public Item(String uri, String sparqlResultInfos){
        this.uri = uri;
        values = new ArrayList<Prop>();
       DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc;
            InputSource is = new InputSource(new StringReader(sparqlResultInfos));
            NodeList allElements = builder.parse(is).getElementsByTagName("result");
            for(int i=0; i <allElements.getLength();i++){
                NodeList cur = allElements.item(i).getChildNodes();
                String relation = cur.item(1).getTextContent();
                String value = cur.item(3).getTextContent();
                values.add(new Prop(relation,value));
            }
            
        } catch (ParserConfigurationException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
            Logger.getLogger(FourStoreDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Item(String link, String title, String description, Date datePublication){
        this.uri = link;
        values = new ArrayList<Prop>();
        values.add(new Prop(RSS.link,link,true));
        values.add(new Prop(RSS.title,title));
        values.add(new Prop(RSS.description,description));
        values.add(new Prop(RSS.getURI()+"pubDate",datePublication.toString()));
        values.add(new Prop(RSS.getURI()+"pubDateTime",Long.toString(datePublication.getTime())));
    }
    
    public void addElement(String key, String content){
        values.add(new Prop(key, content));
    }
    
    public String getElement(Property key){
        for(int i=0; i < values.size();i++){
            if(values.get(i).getType().equals(key))
                return values.get(i).getValue();
        }
        return null;
    }
    
    @Override
    public String toString(){
        String content = "Item description:"+this.getUri();
        
        Iterator i = values.iterator();

        while(i.hasNext()){
            Prop me = (Prop)i.next();
            String isL = "Uri";
            if(me.isLiteral()) isL = "Lit";
            content += "\n\t"+isL+" "+me.getType() + " : ";
            if (me.getValue().length() > 100){
                content+= me.getValue().substring(0,100)+"...";
            }else{
                content+= me.getValue();
            }
        }
        return content;
    }

    public Item(ArrayList<Prop> values) {
        this.values = values;
    }
    
    public String concat(){
        String result = this.getElement(RSS.title)+".\n "+this.getElement(RSS.description);
        return result.replaceAll("<[^>]*>", "");
    }

    public String getUri() {
        return uri;
    }
    
    public ArrayList<Prop> getElements(){
        return values;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public void addProp(Prop prop){
        this.values.add(prop);
    }
    
    public void addProps(ArrayList<Prop> props){
        this.values.addAll(props);
    }
    
    public Model getModel(){
        Model model = ModelFactory.createDefaultModel();
        Resource itemNode = model.createResource(uri);
        for(Prop prop : values){
            if(prop.isLiteral()){
                itemNode.addLiteral(prop.getType(), model.createLiteral(prop.getValue()));
            }
            else{
                itemNode.addProperty(prop.getType(), model.createResource(prop.getValue()));
            }
        }
        return model;
    }
}
