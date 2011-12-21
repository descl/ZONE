package extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;
import mx.bigdata.jcalais.rest.CalaisRestClient;

/**
 *
 * @author descl
 */
public class openCalaisExtractor {
    public static CalaisResponse getResult(java.lang.String content) {
        String license = "p8zhr726g3mzj9g7fs7hg7cj ";
        
        CalaisClient client = new CalaisRestClient(license);
        CalaisResponse response = null;
        try {
            response = client.analyze(content);
        }
        catch (mx.bigdata.jcalais.CalaisException ex){
            return openCalaisExtractor.getResult(content);
        }
        catch (IOException ex) {
            System.out.println(ex.getClass().toString());
            Logger.getLogger(openCalaisExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  response;
    }
    public static String[] getCitiesResult(java.lang.String content) {
        CalaisResponse response = openCalaisExtractor.getResult(content);
        
        ArrayList<String> list = new ArrayList<String>();
        for (CalaisObject entity : response.getEntities()) {
            if(!entity.getField("_type").equals("City"))continue;
            list.add(entity.getField("name"));
        }
        return list.toArray(new String[list.size()]);
    }
    
    public static String[] getPersonsResult(java.lang.String content) {
        CalaisResponse response = openCalaisExtractor.getResult(content);
        
        ArrayList<String> list = new ArrayList<String>();
        for (CalaisObject entity : response.getEntities()) {
            if(!entity.getField("_type").equals("Person"))continue;
            list.add(entity.getField("name"));
        }
        return list.toArray(new String[list.size()]);
    }   
}