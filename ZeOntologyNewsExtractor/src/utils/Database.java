package utils;

/**
 *
 * @author descl
 */
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import java.io.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database extends Object {
      private static String directory = "out/" ;
      private static Model model = null; 
      private static String newsURI  = "http://news.com/News#";
      
      private static Model getModel(){
          if(model == null)model = TDBFactory.createModel(directory);
          return model;
      }
      public static void addItems(Item[] items){
        for(int i=0; i < items.length;i++){
            Database.addItem(items[i]);
        }
      }
      
      public static void addItem(Item item){
            Resource itemNode = Database.getModel().createResource(item.getUri());
            Iterator it = item.values.iterator();
            while (it.hasNext()){
                Prop cle = (Prop)it.next();
                
                //insert item in database
                MysqlDatabase.createItem(cle);
                
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
            Database.getModel().write(new PrintWriter(System.out));
        } catch (Exception ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      }
      
      public static boolean ItemURIExist(String uri){
          String queryString = "ASK { <"+uri+"> <"+newsURI+"title> ?z}";
          Query query = QueryFactory.create(queryString) ;
          QueryExecution qexec = QueryExecutionFactory.create(query, Database.getModel()) ;
          boolean result = qexec.execAsk() ;
          qexec.close() ;
          return result;
      }
}
