package zeontologynewsextractor;

import extractor.AnnotationsGesture;
import utils.RSSGetter;
import extractor.InseeSparqlRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import utils.RDFDatabase;
import utils.Item;
import utils.MysqlDatabase;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class ZeOntologyNewsExtractor {

    /**
     * @param args the command line arguments 
     */
    public static void main(String[] args) throws MalformedURLException  {
        ArrayList<Item>  items = new ArrayList<Item>();
        String [] fluxLinks = {
                         //"resources/examples/europe1.rss",
                         "http://europe1.fr.feedsportal.com/c/32376/f/546041/index.rss"
                    //     ,"http://actualite.portail.free.fr/france/rss.xml"
                    //     ,"http://www.tv5.org/TV5Site/rss/actualites.php?rub=3"
        };
        items.addAll(RSSGetter.getFlux(fluxLinks));
        
        AnnotationsGesture.addAnnotations(items);
        
        for(int i=0; i< items.size();i++){
            System.out.println("\n"+items.get(i));
        }
        

        
        
        MysqlDatabase.createItemFromItems(items);
        RDFDatabase.addItems(items);
        MysqlDatabase.close();
        
        System.out.println("Done");
    }
}
