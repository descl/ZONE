package extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author descl
 */
public class WikiMetaExtractor {
    
    public enum Format {
	XML("xml"),
	JSON("json");
        private final String value;
	Format(String value) {this.value = value;}
	public String getValue() {return this.value;}
    }
    
    
    
    public static String getResult(String apiKey, Format format, String content) {
        return WikiMetaExtractor.getResult(apiKey, format, content, 10, 100, "FR",true);
    }
    
    public static String getResult(String apiKey, Format format, String content, int treshold, int span, String lng, boolean semtag) {    
        String result = "";
        try {
            URL url = new URL("http://www.wikimeta.org/perl/semtag.pl");
            HttpURLConnection server = (HttpURLConnection)url.openConnection();
            server.setDoInput(true);
            server.setDoOutput(true);
            server.setRequestMethod("POST");
            server.setRequestProperty("Accept","xml");
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
            Logger.getLogger(WikiMetaExtractor.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }
    
    public static void main(String[] args){
        String result = getResult("descl",WikiMetaExtractor.Format.JSON,"Bienvenue à Antibes");
        System.out.println(result);
    }
}
