package org.zoneproject.extractor.plugin.inseegeo;

import com.hp.hpl.jena.query.ResultSet;
import org.zoneproject.extractor.utils.Database;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Uninstall {
    public static void main(String[] args) {
        System.out.println("Remove the INSEE Database");
        String q= "DELETE { ?a ?b ?c }WHERE  { ?a ?b ?c. FILTER ( regex(?a, 'rdf.insee.fr') )  }";
        Database.runSPARQLRequest(q,"http://rdf.insee.fr/geo/2011/");
        Database.runSPARQLRequest(q);
    }
}
