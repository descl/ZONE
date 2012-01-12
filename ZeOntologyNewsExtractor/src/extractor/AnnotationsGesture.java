package extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import utils.Item;
import utils.MysqlDatabase;
import utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class AnnotationsGesture {
    private static ArrayList<Prop> getAllAnnotations(Item item){
        ArrayList<Prop> annotations = new ArrayList<Prop>();
        
        //get opencalais annotations
        //String[] cities = openCalaisExtractor.getCitiesResult(item.concat());
        //annotations.addAll(SparqlRequest.getDimensions(cities));
        
        
        //get WikiMeta annotations
        ArrayList<Prop> WikiMetaProps = WikiMetaRequest.getProperties(item);
        annotations.addAll(WikiMetaProps);
        
        //get location annotations using WikiMeta
        ArrayList<Prop> DBpediaProps = DBpediaRequest.getProperties(WikiMetaProps);
        annotations.addAll(DBpediaProps);
        
        //get the grid of locations length using INSEE database
        ArrayList<Prop> InseeProps = InseeSparqlRequest.getProperties(DBpediaRequest.filter(DBpediaProps,"LOC"));
        annotations.addAll(InseeProps);
        return annotations;        
    }
    
    public static void addAnnotations(ArrayList<Item> items){
        Iterator<Item> it = items.iterator();
        while(it.hasNext()){
            Item i = it.next();
            
            System.out.println("get WikiMeta annotations for "+i.getUri());
            ArrayList<Prop> props = AnnotationsGesture.getAllAnnotations(i);
            i.addProps(props);
            System.out.println("\t"+props);
            
            System.out.println("get WikiMeta annotations => done");
        }
    }
    
    
    public static void main(String[] args){
        Item it = new Item("http://test","bienvenue à Antibes","");
        System.out.println(getAllAnnotations(it));
    }
}