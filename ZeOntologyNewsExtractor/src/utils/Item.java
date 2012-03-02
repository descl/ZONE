package utils;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RSS;
import com.sun.syndication.feed.synd.SyndEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Item {
    public String uri;
    public ArrayList<Prop> values;

    public Item(SyndEntry entry){
        this(entry.getLink(),entry.getTitle(),entry.getDescription().getValue(),entry.getPublishedDate());
    }
    
    public Item(String uri){
       this.uri = uri;
        values = new ArrayList<Prop>();
    }
    
    public Item(String link, String title, String description, Date datePublication){
        this.uri = link;
        values = new ArrayList<Prop>();
        values.add(new Prop(RSS.link,link,true));
        values.add(new Prop(RSS.title,title));
        values.add(new Prop(RSS.description,description));
        values.add(new Prop(RSS.getURI()+"pubDate",datePublication.toString()));
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
        String content = "Item description:";
        
        Iterator i = values.iterator();

        while(i.hasNext()){
            Prop me = (Prop)i.next();
            String isL = "Uri";
            if(me.isLiteral()) isL = "Lit";
            content += "\n\t"+isL+" "+me.getType() + " : " + me.getValue();
        }
        return content;
    }

    public Item(ArrayList<Prop> values) {
        this.values = values;
    }
    
    public String concat(){
        return this.getElement(RSS.title)+".\n "+this.getElement(RSS.description);
    }

    public String getUri() {
        return uri;
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
}
