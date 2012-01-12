package utils;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import java.io.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RDFDatabase extends Object {
      private static String directory = "out/" ;
      private static Model model = null; 
      private static String newsURI  = "http://news.com/News#";
      
      private static Model getModel(){
          if(model == null)model = TDBFactory.createModel(directory);
          return model;
      }
      
      public static void addItems(ArrayList<Item> items){
          addItems(items.toArray(new Item[items.size()]));
      }
      public static void addItems(Item[] items){
        for(int i=0; i < items.length;i++){
            RDFDatabase.addItem(items[i]);
        }
      }
      
      public static void addItem(Item item){
            Resource itemNode = RDFDatabase.getModel().createResource(item.getUri());
            Iterator it = item.values.iterator();
            while (it.hasNext()){
                Prop cle = (Prop)it.next();
                
                Property P = model.createProperty( newsURI + cle.getType());
                if(cle.isLiteral()){
                    itemNode.addLiteral(P, model.createLiteral(cle.getValue()));
                }
                else{
                    itemNode.addProperty(P, model.createResource(cle.getValue()));
                }
            }
            model.commit();
      }
      
      public static void print(){
        OutputStream out = null;
        try {
            RDFDatabase.getModel().write(new PrintWriter(System.out));
        } catch (Exception ex) {
            Logger.getLogger(RDFDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      }
      
      public static boolean ItemURIExist(String uri){
          String queryString = "ASK { <"+uri+"> <"+newsURI+"title> ?z}";
          Query query = QueryFactory.create(queryString) ;
          QueryExecution qexec = QueryExecutionFactory.create(query, RDFDatabase.getModel()) ;
          boolean result = qexec.execAsk() ;
          qexec.close() ;
          return result;
      }
}
