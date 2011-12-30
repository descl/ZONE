package extractor.WikiMeta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import utils.Item;
import utils.Prop;


/**
 *
 * @author descl => https://github.com/descl/ZONE
 */
public class WikiMetaExtractorXML_OLD {
    
    public enum Format {
	XML("xml"),
	JSON("json");
        private final String value;
	Format(String value) {this.value = value;}
	public String getValue() {return this.value;}
    }
    
    
    
    public static String getResult(String apiKey, Format format, String content) {
        return WikiMetaExtractorXML_OLD.getResult(apiKey, format, content, 10, 100, "FR",true);
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
            Logger.getLogger(WikiMetaExtractorXML_OLD.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }
    
    public static ArrayList<Prop> getProperties(Item item){
        
        ArrayList<Prop> result = new ArrayList<Prop>();
        //String anno = getResult("descl", Format.XML, item.concat());
        //StringReader r = new StringReader(anno);
        //System.out.println(anno);
        File r = new File("resources/examples/WikiMetaOutput.xml");
        
        
        try {
            System.out.println(r);
            
            //analyse the xml result
            SAXBuilder sxb = new SAXBuilder();
            Document document = null;
            document = sxb.build(r);
            Element racine = document.getRootElement();
            List entities = racine.getChildren("format");
            Iterator i = entities.iterator();
            while(i.hasNext()){
                Element cur = (Element)i.next();
                if(cur.getChild("extraction") == null)continue;
                cur = cur.getChild("extraction");
                
                if(cur.getChildText("LINKEDDATA") != null && !cur.getChildText("LINKEDDATA").equals("")){
                    result.add(new Prop(cur.getChildText("type"),cur.getChildText("LINKEDDATA"),false));
                    result.add(new Prop(cur.getChildText("type"),cur.getChildText("NE"),true));
                }
           }
        } catch (Exception ex) {
            Logger.getLogger(WikiMetaExtractorXML_OLD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static void main(String[] args){
        String result = getResult("descl",WikiMetaExtractorXML_OLD.Format.JSON,"Bienvenue à Antibes");
        System.out.println(result);
    }
}
