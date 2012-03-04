/**
 * requests for WikiMeta
 */
package extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import utils.Config;
import utils.Item;
import utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class WikiMetaRequest {
    public static String EntitiesURI = "http://www.wikimeta.org/Entities#";
    
    public static ArrayList<Prop> getProperties(Item item){
        return getProperties(item.concat());
    }
    
    public static ArrayList<Prop> getProperties(String texte){
        String f = WikiMetaRequest_API.getResult(Config.getVar("wikiMeta-key"), WikiMetaRequest_API.Format.JSON, texte);
        return analyseWikiMetaResult(f);
    }
    
    
    public static ArrayList<Prop> getProperties(File file){
        
        String content = "";
        try{
            FileInputStream fstream = new FileInputStream(file.getPath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                content += "\n"+strLine;
            }
            in.close();
        }catch (Exception e){
            Logger.getLogger(WikiMetaRequest.class.getName()).log(Level.SEVERE, null, e);
        }
        return analyseWikiMetaResult(content);
    }
    
    /**
     * 
     * @param content the json result content
     * @return the list of propertyes
     */
    private static ArrayList<Prop> analyseWikiMetaResult(String JSONcontent){
        ArrayList<Prop> result = new ArrayList<Prop>();
        
        ArrayList<LinkedHashMap> namedEntities = WikiMetaRequest_API.getNamedEntities(JSONcontent);
        for(int i=0; i< namedEntities.size();i++){
            
            LinkedHashMap cur = namedEntities.get(i);
            if(cur.containsKey("LINKEDDATA")){
                if(cur.get("LINKEDDATA").equals(""))
                    continue;
                Prop p = new Prop(WikiMetaRequest.EntitiesURI+""+cur.get("type").toString(), cur.get("LINKEDDATA").toString(), false);
                if(!result.contains(p))
                    result.add(p);
            }
        }
        return result;
    }
    
    public static void main(String[] args){
        //String result = getResult("descl",WikiMetaExtractorJSON.Format.JSON,"Bienvenue à Antibes");
        //System.out.println(result);
        
        System.out.println(getProperties("Reuters se site dans la ville de Paris"));
        //System.out.println(getProperties(new File("resources/examples/WikiMetaOutput_pip.json")));
        //System.out.println(getProperties(new File("resources/examples/WikiMetaOutput_mars.json")));
    }
}
