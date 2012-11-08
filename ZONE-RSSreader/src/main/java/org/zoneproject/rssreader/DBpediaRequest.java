/*
 * This class is used to get informations from dbpedia database
 * It use only rdfs files catch from the website of dbpedia
 */
package org.zoneproject.rssreader;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.zoneproject.utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class DBpediaRequest {
    
    public static ArrayList<Prop> filter(ArrayList <Prop> props, String filter){
        ArrayList <Prop> result = new ArrayList<Prop>();
        for(int i=0; i< props.size();i++){
            if(props.get(i).getType().getURI().startsWith(filter))
                result.add(props.get(i));
        }
        return result;
    }
    public static ArrayList<Prop> getProperties(ArrayList<Prop> uris){
        ArrayList<Prop> result = new ArrayList<Prop>();
        for(int i =0; i < uris.size(); i++){
            Prop cur = uris.get(i);
            String name = getCityNameFromURI(cur.getValue());
            if(name != null)
                result.add(new Prop(cur.getType(), name, true));
        }
        return result;
    }
    
    /**
     * parse a DBpedia uri to a rdf file uri
     * @param uri
     * @return the string uri to the rdf file
     */
    public static String formatURIforRDF(String uri){
        String rdfURI = uri.replaceFirst("page", "data");
        rdfURI = rdfURI.replaceFirst("resource", "data");
        
        //for strange bug of no response if www is present
        rdfURI = rdfURI.replaceFirst("http://www.", "http://");
        
        if(!rdfURI.endsWith(".rdf"))rdfURI+=".rdf";
        return rdfURI;    
    }
    
    /**
     * used to get a well formed result name of a dbpedia resource
     * @param uri to the dbpedia resouce
     * @return the canton name of label pointing to the resource
     */
    public static String getCityNameFromURI(String uri){
        System.out.println("looking for"+uri);
        Model model;
        try {
            model = DBpediaRequest.getRDFSchemaFromRDF_URI(uri);
        } catch (IOException ex) {
            System.out.println("DBpedia resource error for "+uri+"\n Error:"+ex.getMessage());
            return getNameResource(uri);
        }
        
        String canton = getCanton(model);
        if(canton != null)return canton;
       
        String label = getLabel(model);
        if(label != null)return label;
        
        return  getNameResource(uri);
    }
    public static String getNameResource(String uri){
        return uri.substring(uri.lastIndexOf("/")+1);
    }
    
    /**
     * get a label for a model
     * @param model the corresponding model
     * @return if define the french label or the english
     */
    private static String getLabel(Model model){
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
                if(result.getLanguage().equals("fr")){
                    label =  result.getString().replace(" (homonymie)", "");
                    break;
                }
                else
                    label = result.getString();
        }
        qexec.close();
        return label;
    }
    
    /**
     * get if defined a canton for a model
     * @param model
     * @return the canton if defined
     */
    private static String getCanton(Model model){
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
    
    /**
     * get the jena model corresponding to a DBpedia resource
     * @param uri
     * @return the model
     */
    private static Model getRDFSchemaFromRDF_URI(String uri)throws IOException{
        String rdfURI = formatURIforRDF(uri);
        
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        
        InputStream in = FileManager.get().open(rdfURI);
        
        if (in == null) {
        throw new IOException("File: " + rdfURI + " not found");}
        
        // read the RDF/XML file
        try{
            model.read(in, null);
        }catch (Exception ex){
            throw new IOException("File: " + rdfURI + " error on loading (not xml well formed");
        }
        return model;
    }
    
    /**
     * main function for testing
     * @param args 
     */
    public static void main(String[] args){
        System.out.println(DBpediaRequest.getCityNameFromURI("http://www.dbpedia.org/resource/Antwerp"));
        /*System.out.println(DBpediaRequest.getCityNameFromURI("http://dbpedia.org/page/House_of_Lorraine"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://dbpedia.org/page/Petit-Couronne"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://www.dbpedia.org/resource/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://www.dbpedia.org/resource/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://www.dbpedia.org/resource/Pau"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://www.dbpedia.org/resource/Dinaric_Alps"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://dbpedia.org/page/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://dbpedia.org/page/Port-la-Nouvelle"));
        System.out.println(DBpediaRequest.getCityNameFromURI("http://dbpedia.org/data/Quimper.rdf"));*/
    }    
}
