package org.zoneproject.extractor.plugin.inseegeo;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
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
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.Database;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class InseeSparqlRequest {
    
    
    public static String request(String queryString){
        String res = null;
        //String res = Database.runSPARQLRequest(queryString, Store.OutputFormat.JSON);
        return res;
    }
    
    public static ArrayList<Prop> getDimensions(String[] cities){
        ArrayList<Prop> result = new ArrayList<Prop>();
        for(int i=0; i< cities.length;i++){
            result.addAll(getDimensions(cities[i]));
        }
        return result;
    }
    
    public static ArrayList<Prop> getDimensions(String city){
        String query="PREFIX geo: <http://rdf.insee.fr/geo/>"
          + "\nSELECT  DISTINCT ?nom ?nomEntite ?entite ?type WHERE{"
          + "\n    ?ville geo:nom '"+city+"'@fr"
          + "\n    ?entite geo:subdivision* ?ville"
          + "\n    ?ville geo:nom ?nom"
          + "\n    ?entite rdf:type ?type"
          + "\n    ?entite geo:nom ?nomEntite"
          + "\n    FILTER(?type != geo:Pays_ou_Territoire)}";
        System.out.println(query);
        
        String res = InseeSparqlRequest.request(query);
        ArrayList<Prop> dims = new ArrayList<Prop>();
        
        
        
        
        
        /*for (Enumeration<IResult> en = res.getResults(); en.hasMoreElements();) {
            IResult r = en.nextElement();
            //dims.add(new Prop(r.getStringValue("?type"), r.getStringValue("?nomEntite"),true));
            dims.add(new Prop(r.getStringValue("?type"), r.getStringValue("?entite"),false));
        }
        
        return dims;*
        */ return null;
    }
    
    public static ArrayList<Prop> getDimensionsTooLong(String[] cities){
        String query="PREFIX geo: <http://rdf.insee.fr/geo/>"
                  + "\nSELECT  DISTINCT ?nom ?entite ?type WHERE{"
                  + "\n    ?entite geo:nom ?nom"
                  + "\n    ?entite geo:subdivision* ?ville"
                  + "\n    ?ville geo:nom ?nomVille"
                  + "\n    ?entite rdf:type ?type"
                  + "\n    FILTER(?type != geo:Pays_ou_Territoire && (FALSE";
                  for(int i=0; i < cities.length;i++){
                      query += "|| regex(str(?nomVille), '"+cities[i]+"', 'i') ";
                  }
        query += "))\n}";
        
        /*System.out.println(query);
        
        IResults res = InseeSparqlRequest.request(query);
        
        ArrayList<Prop> dims = new ArrayList<Prop>();
        for (Enumeration<IResult> en = res.getResults(); en.hasMoreElements();) {
            IResult r = en.nextElement();
            //dims.add(new Prop(r.getStringValue("?type"), r.getStringValue("?nom"),true));
            dims.add(new Prop(r.getStringValue("?type"), r.getStringValue("?entite"),false));
        }
        
        return dims;*/
        return null;
    }
    
    public static ArrayList<Prop> getProperties(ArrayList<Prop> locations){
        
        ArrayList<String> arrElements = new ArrayList<String>();
        for(int i =0; i < locations.size(); i++){
            Prop cur = locations.get(i);
            arrElements.add(cur.getValue());
        }
        return getDimensions(arrElements.toArray(new String[arrElements.size()]));
    }
    
    public static void main(String[] args){
        System.out.println((new Date()).toString());
        //IEngine engine = getEngine();
        //System.out.println((new Date()).toString());
        System.out.println(getDimensions("Antibes"));
        //System.out.println(getDimensions("La Seyne sur mer"));
        //System.out.println(getDimensions("Toulon"));
        System.out.println((new Date()).toString());
        System.out.println("Done");
    }
}