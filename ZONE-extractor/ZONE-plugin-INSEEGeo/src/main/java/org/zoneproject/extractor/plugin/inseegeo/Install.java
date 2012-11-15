/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.extractor.plugin.inseegeo;

import com.hp.hpl.jena.query.ResultSet;
import java.io.File;
import org.zoneproject.extractor.utils.Database;

/**
 *
 * @author cdesclau
 */
public class Install {
    public static void main(String[] args) {
        System.out.println("Loading RDF Files for the INSEE Database");
        ResultSet r = Database.runSPARQLRequest(" SELECT ?p WHERE { <http://rdf.insee.fr/geo/2011/COM_10378> ?p ?i } LIMIT 10");
        if(!r.hasNext()){
            Database.loadFolder("http://rdf.insee.fr/geo/2011/",args[0]);
        }
    }
    
}
