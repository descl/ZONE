package extractor;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import utils.Prop;


/**
 *
 * @author descl
 */
public class SparqlRequestWithJena_OLD {
    private static String directorySource = "geoDB";
    public static Model model = null;
    
    public static Model createDB(){
        String input = "resources/geographie/";
        File directory = new File(input);
        String[] files = directory.list();
        if ( files == null )
            System.out.println("The directory doesn't exist!");
        else if ( files.length == 0 )
            System.out.println("The directory is empty.");
        else {
            for (int i = 0, j = files.length; i < j; i++ ) {
                if(files[i].equals(".svn"))continue;
                System.out.println( files[i] + "\t is being processed.");
                try{
                    FileManager.get().readModel( model, input + files[i]);
                }catch(NoSuchElementException e){
                    System.out.println("\t already done");
                }
            }
        }
        //model.commit();
        System.out.println("Loading done");
        return model;
    }
    
    public static Model getModel(){
        if(model == null){
            //model = TDBFactory.createModel(directorySource);
            model = ModelFactory.createDefaultModel();
            
            SparqlRequestWithJena_OLD.createDB();
            
        }
        
        /*if(model.isEmpty()){
            SparqlRequest.createDB();
        }*/
        System.out.println("geographic DB loaded");
        return model;
    }
    public static ResultSet request(String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, SparqlRequestWithJena_OLD.getModel());
        ResultSet res = qe.execSelect();
        //qe.close();
        return res;
    }
    
    public static ArrayList<Prop> getDimensions(String city){
        String[] list = {city};
        return SparqlRequestWithJena_OLD.getDimensions(list);
    }
    public static ArrayList<Prop> getDimensions(String[] cities){
         String queryString=""
                 + "PREFIX geo: <http://rdf.insee.fr/geo/>\n"
                 + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                 + "SELECT  DISTINCT ?nom ?entite ?type WHERE{\n"
                    + "?entite geo:nom ?nom .\n"
                    + "?entite rdf:type ?type .\n"
                    + "{ "
                        + "?ville geo:nom ?nomVille . "
                        + "?entite geo:subdivision* ?ville . "
                        + " FILTER(FALSE ";
                        for(int i=0; i < cities.length;i++){
                            queryString += "|| regex(str(?nomVille), '"+cities[i]+"', 'i') ";
                        }
                        queryString += ") "
                    + "}"
                    + "FILTER(?type != geo:Pays_ou_Territoire) "
                 + "}";
        //queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n SELECT ?c { <http://rdf.insee.fr/geo/2011/COM_72137> rdf:type ?c }";
        System.out.println("start query");
        
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, SparqlRequestWithJena_OLD.getModel());
        ResultSet res = qe.execSelect();
        
        //ResultSet res = SparqlRequest.request(query);
        System.out.println("stop query");
        ArrayList<Prop> dims = new ArrayList<Prop>();
        System.out.println("request done "+ res.getRowNumber());
        for(int i=0; i < res.getResultVars().size();i++){
            System.out.println(res.getResultVars().get(i));
        }
        
        
        while(res.hasNext()){
            QuerySolution q = res.nextSolution();
            System.out.println("has next"+q.get("nom").toString());
            dims.add(new Prop(q.get("type").toString(), q.get("nom").toString(),true));
            dims.add(new Prop(q.get("type").toString(), q.get("entite").toString(),false));
            
        }
        System.out.println("no more");
        qe.close();
        
        return dims;
    }
}
