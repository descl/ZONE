/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extractor;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class DbpediaImportInformations {
    
    public static String formatURIforRDF(String uri){
        
        String rdfURI = uri;
        try {rdfURI = URLDecoder.decode(uri,"UTF-8");} 
        catch (UnsupportedEncodingException ex) {}
        
        rdfURI = rdfURI.replaceFirst("page", "data");
        rdfURI = rdfURI.replaceFirst("resource", "data");
        rdfURI = rdfURI.replaceFirst("http://www.", "http://");//for strange bug of no response if www is present
        if(!rdfURI.endsWith(".rdf"))rdfURI+=".rdf";
        return rdfURI;    
    }
    
    public static String getCityNameFromURI(String uri){
        System.out.println("looking for"+uri);
        String rdfURI = formatURIforRDF(uri);
        Model model = DbpediaImportInformations.getRDFSchemaFROMRDFURI(rdfURI);
        
        
        String canton = getCanton(model);
        if(canton != null)return canton;
        
        return  getLabel(model);
    }
    
    public static String getLabel(Model model){
        String query = "SELECT ?resource WHERE{ "
                  + "\n ?x <http://www.w3.org/2000/01/rdf-schema#label> ?resource"
                  + "\n FILTER(lang(?resource) = 'fr' || lang(?resource)= 'en')}";
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        ResultSet rs = qexec.execSelect() ;
        String label = null;
        for ( ; rs.hasNext() ; ){
                QuerySolution rb = rs.nextSolution() ;
                RDFNode y = rb.get("y");
                Literal result = rb.get("resource").asLiteral();
                if(result.getLanguage().equals("fr"))return result.getString();
                label = result.getString();
        }
        qexec.close();
        return label;
    }
    
    public static String getCanton(Model model){
        String query = "SELECT ?resource WHERE{ "
                  + "\n ?x <http://dbpedia.org/property/canton> ?resource}";
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        ResultSet rs = qexec.execSelect() ;
        
        for ( ; rs.hasNext() ; ){
                QuerySolution rb = rs.nextSolution() ;
                RDFNode y = rb.get("y");
                String result = rb.get("resource").asLiteral().getString();
                qexec.close();
                return result;
        }
        qexec.close();
        return null;
    }
    
    public static Model getRDFSchemaFROMRDFURI(String uri){
        System.out.println(uri);
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(uri);
        if (in == null) {
        throw new IllegalArgumentException(
                                     "File: " + uri + " not found");
        }

        // read the RDF/XML file
        model.read(in, null);
        
        model.write(System.out);
        return model;
    }
    
    public static void main(String[] args){
  /*      System.out.println(DbpediaImportInformations.getCityNameFromURI("http://dbpedia.org/page/House_of_Lorraine"));
        System.out.println(DbpediaImportInformations.getCityNameFromURI("http://dbpedia.org/page/Petit-Couronne"));
    */    System.out.println(DbpediaImportInformations.getCityNameFromURI("http://www.dbpedia.org/resource/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        //System.out.println(DbpediaImportInformations.getCityNameFromURI("http://www.dbpedia.org/resource/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        /*System.out.println(DbpediaImportInformations.getCityNameFromURI("http://www.dbpedia.org/resource/Pau"));
        System.out.println(DbpediaImportInformations.getCityNameFromURI("http://www.dbpedia.org/resource/Dinaric_Alps"));
        System.out.println(DbpediaImportInformations.getCityNameFromURI("http://dbpedia.org/page/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        System.out.println(DbpediaImportInformations.getCityNameFromURI("http://dbpedia.org/page/Port-la-Nouvelle"));
        System.out.println(DbpediaImportInformations.getCityNameFromURI("http://dbpedia.org/data/Quimper.rdf"));
*/
    
    
    
    
    }    
}
