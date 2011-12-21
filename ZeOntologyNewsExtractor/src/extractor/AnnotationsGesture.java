package extractor;

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
        
        //Add cities annotations
        //String[] cities = openCalaisExtractor.getCitiesResult(item.concat());
        String [] cities = {"Toulon"};
        annotations.addAll(SparqlRequest.getDimensions(cities));
        
        //Add persons annotations
        //String[] persons = openCalaisExtractor.getPersonsResult(item.concat());
        //for(String curP : persons){
        //    annotations.add(new Prop("person", curP));
        //}
        
        
        return annotations;        
    }
    
    public static void addAnnotations(ArrayList<Item> items){
        Iterator<Item> it = items.iterator();
        while(it.hasNext()){
            Item i = it.next();
            i.addProps(AnnotationsGesture.getAllAnnotations(i));
        }
    }
}