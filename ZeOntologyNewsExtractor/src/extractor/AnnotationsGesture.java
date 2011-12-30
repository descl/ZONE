package extractor;

import extractor.WikiMeta.WikiMetaExtractorXML_OLD;
import extractor.WikiMeta.WikiMetaExtractorJSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import utils.Item;
import utils.Prop;

/**
 *
 * @author descl
 */
public class AnnotationsGesture {
    private static ArrayList<Prop> getAllAnnotations(Item item){
        ArrayList<Prop> annotations = new ArrayList<Prop>();
        
        //get opencalais annotations
        //String[] cities = openCalaisExtractor.getCitiesResult(item.concat());
        //annotations.addAll(SparqlRequest.getDimensions(cities));
        
        
        //get WikiMeta annotations
        ArrayList<Prop> props = WikiMetaExtractorJSON.getProperties(item);
        annotations.addAll(props);
        
        //get location annotations using WikiMeta and INSEE database
        
        
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
}