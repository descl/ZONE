package utils;

import com.sun.syndication.feed.synd.SyndEntry;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Item {
    public String uri;
    public ArrayList<Prop> values;

    public Item(SyndEntry entry){
        this(entry.getLink(),entry.getTitle(),entry.getDescription().getValue());
    }
    
    public Item(String uri){
       this.uri = uri;
        values = new ArrayList<Prop>();
    }
    
    public Item(String link, String title, String description){
        this.uri = link;
        values = new ArrayList<Prop>();
        values.add(new Prop("link",link));
        values.add(new Prop("title",title));
        values.add(new Prop("description",description));
    }
    
    public void addElement(String key, String content){
        values.add(new Prop(key, content));
    }
    
    public String getElement(String key){
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
            content += "\n\t"+me.getType() + " : " + me.getValue();
        }
        return content;
    }

    public Item(ArrayList<Prop> values) {
        this.values = values;
    }
    
    public String concat(){
        return this.getElement("title")+".\n "+this.getElement("description");
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