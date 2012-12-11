/**
 * This file is a generic WikiMeta request
 * see WikiMetaRequest.java for my use
 */
package org.zoneproject.extractor.plugin.wikimeta;

/*
 * #%L
 * ZONE-plugin-WikiMeta
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class WikiMetaRequest_API {
    
    
    public enum Format {
	XML("xml"),
	JSON("json");
        private final String value;
	Format(String value) {this.value = value;}
	public String getValue() {return this.value;}
    }
    
    public static String getResult(String apiKey, Format format, String content) {
        return WikiMetaRequest_API.getResult(apiKey, format, content, 10, 100, "FR",true);
    }
    
    public static String getResult(String apiKey, Format format, String content, int treshold, int span, String lng, boolean semtag) {    
        
        String result = "";
        try {
            URL url = new URL("http://www.wikimeta.com/wapi/service");
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
            Logger.getLogger(WikiMetaRequest_API.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(WikiMetaRequest_API.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<LinkedHashMap>();
    }
}
