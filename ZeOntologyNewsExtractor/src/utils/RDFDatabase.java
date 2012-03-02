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
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
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
                
                //Property P = model.createProperty( newsURI + cle.getType());
                //Property P = model.createProperty( newsURI + cle.getType());
                System.out.println(cle.getType());
                if(cle.isLiteral()){
                    itemNode.addLiteral(cle.getType(), model.createLiteral(cle.getValue()));
                }
                else{
                    itemNode.addProperty(cle.getType(), model.createResource(cle.getValue()));
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
          String queryString = "ASK { <"+uri+"> <http://purl.org/rss/1.0/title> ?z}";
          Query query = QueryFactory.create(queryString) ;
          QueryExecution qexec = QueryExecutionFactory.create(query, RDFDatabase.getModel()) ;
          
          boolean result = qexec.execAsk() ;
          qexec.close() ;
          return result;
      }
      
      
      
      public static void deleteItem(String uri){
          String queryString = "DELETE{ <"+uri+"> ?y ?z}"
                  +"WHERE{"
                  +"SELECT * { <"+uri+"> ?y ?z}"
                  +"}";
          
          UpdateRequest r = UpdateFactory.create(queryString);
          UpdateAction.execute(r, RDFDatabase.getModel());
      }
      
      public static void verifyItemsList(ArrayList<Item> items){
          for (Iterator<Item> iterator = items.iterator(); iterator.hasNext(); ) {
              Item o = iterator.next();
              if (ItemURIExist(o.getUri())) {
                   iterator.remove();
              }
          }
      }
      
      public static void writeRDFFile(){
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("out/output.rdf");
            RDFDatabase.model.write(fout);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RDFDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(RDFDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
}
