package org.zoneproject.extractor.plugin.inseegeo;

/*
 * #%L
 * ZONE-plugin-INSEEGeo
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class InseeSparqlRequest {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(InseeSparqlRequest.class);
    
    public static ArrayList<Prop> getDimensions(String[] cities){
        ArrayList<Prop> result = new ArrayList<Prop>();
        for(int i=0; i< cities.length;i++){
            result.addAll(getDimensions(cities[i]));
        }
        return result;
    }
    
    public static ArrayList<Prop> getDimensions(String city){
          String query="PREFIX geo: <http://rdf.insee.fr/geo/>" +
                "\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                  
                "SELECT DISTINCT ?zone ?type WHERE\n" +
                "{\n" +
                "  ?ville geo:nom ?nom.\n"+
                "  ?zone rdf:type ?type.\n"+
                "  {\n" +
                "    SELECT ?ville ?zone WHERE{\n" +
                "      {?zone geo:subdivision ?ville}\n" +
                "    }\n" +
                "  } OPTION ( transitive,\n" +
                "            t_distinct,\n" +
                "            t_in(?ville),\n" +
                "            t_out(?zone),\n" +
                "            t_min(1)) .\n" +
                "  FILTER ( ?nom = \""+city+"\"@fr)\n" +
                "}";
        logger.info(query);
        
        //ResultSet res = Database.runSPARQLRequest(query,"http://rdf.insee.fr/geo/2011/");
        ResultSet res = Database.runSPARQLRequest(query);
        //System.out.println(res.next());
        ArrayList<Prop> dims = new ArrayList<Prop>();
        while (res.hasNext()) {
            QuerySolution r = res.nextSolution();
            System.out.println(r.toString());
            dims.add(new Prop(r.get("?type").toString(), r.get("?zone").toString(),false,true));

        }
        
        return dims;
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
        logger.info((new Date()).toString());
        logger.info(getDimensions("Toulon"));
        logger.info((new Date()).toString());
        logger.info("Done");
    }
}
