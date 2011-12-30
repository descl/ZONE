package extractor.WikiMeta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import utils.Item;
import utils.Prop;


/**
 *
 * @author descl => https://github.com/descl/ZONE
 */
public class WikiMetaExtractorJSON {
    
    public enum Format {
	XML("xml"),
	JSON("json");
        private final String value;
	Format(String value) {this.value = value;}
	public String getValue() {return this.value;}
    }
    
    
    
    
    public static String getResult(String apiKey, Format format, String content) {
        return WikiMetaExtractorJSON.getResult(apiKey, format, content, 10, 100, "FR",true);
    }
    
    public static String getResult(String apiKey, Format format, String content, int treshold, int span, String lng, boolean semtag) {    
        
        String result = "";
        try {
            URL url = new URL("http://www.wikimeta.org/perl/semtag.pl");
            HttpURLConnection server = (HttpURLConnection)url.openConnection();
            server.setDoInput(true);
            server.setDoOutput(true);
            server.setRequestMethod("POST");
            server.setRequestProperty("Accept",format.value);
            server.connect();
            
            BufferedWriter bw = new BufferedWriter(
                                new OutputStreamWriter(
                                    server.getOutputStream()));
            String semtagS = "0";
            if(semtag)semtagS = "1";
            
            String request = "treshold="+treshold+"&span="+span+"&lng="+lng+"&semtag="+semtagS+"&api="+apiKey+"&contenu="+content;
            bw.write(request, 0, request.length());
            bw.flush();
            bw.close();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                result += ligne+"\n";
            }
            
            reader.close();
            server.disconnect();
        }
        catch (Exception e)
        {
            Logger.getLogger(WikiMetaExtractorJSON.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }
    
    public static ArrayList<Prop> getProperties(Item item){
        String[] itemsTypesNotAllowed = {"AMOUNT","TIME"};
        ArrayList<String> itemsNotAllowed = new ArrayList<String>();
        itemsNotAllowed.add("AMOUNT");
        itemsNotAllowed.add("TIME");
        
        itemsNotAllowed.add("ORG");
        itemsNotAllowed.add("PERS");
        itemsNotAllowed.add("TIME");
    
        ArrayList<Prop> result = new ArrayList<Prop>();
        //File f = new File("resources/examples/WikiMetaOutput.txt");
        String f = getResult("descl", Format.JSON, item.concat());

        ArrayList<LinkedHashMap> namedEntities = getNamedEntities(f);
        for(int i=0; i< namedEntities.size();i++){
            
            LinkedHashMap cur = namedEntities.get(i);
            //if(cur.get("type").toString().equals("ORG"))continue;
            //if(itemsNotAllowed.contains(cur.get("type").toString()))continue;
            if(cur.containsKey("LINKEDDATA")){
                if(cur.get("LINKEDDATA").equals(""))continue;
                result.add(new Prop(cur.get("type").toString(), cur.get("EN").toString(), true));
                result.add(new Prop(cur.get("type").toString(), cur.get("LINKEDDATA").toString(), false));
            }
        }
        return result;
    }
    
    public static ArrayList<LinkedHashMap> getNamedEntities(String f){
        ObjectMapper mapper = new ObjectMapper();
        try {
            //first need to allow non-standard json
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            Map<String,Object> map = mapper.readValue(f, Map.class);
            
            ArrayList<LinkedHashMap> documentElems = (ArrayList<LinkedHashMap>)map.get("document");
            for(int i=0; i < documentElems.size();i++){
                LinkedHashMap cur = (LinkedHashMap) documentElems.get(i);
                if(cur.containsKey("Named Entities"))
                    return (ArrayList<LinkedHashMap>)cur.values().iterator().next();
            }
        } catch (Exception ex) {
            Logger.getLogger(WikiMetaExtractorJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<LinkedHashMap>();
    }
    
    public static void main(String[] args){
        //String result = getResult("descl",WikiMetaExtractorJSON.Format.JSON,"Bienvenue à Antibes");
        Item item = new Item("http://urlItem");
        item.addElement("title", "");
        item.addElement("description", "Vingt cas de cancers survenus chez des femmes porteuses de prothèses mammaires PIP ont été déclarés à l'Agence des produits de santé (Afssaps), a annoncé vendredi cette dernière.");
        System.out.println(getProperties(item));
        
        //System.out.println(result);
    }
}
