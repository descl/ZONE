package org.zoneproject.extractor.plugin.extractarticlescontent;

import com.hp.hpl.jena.vocabulary.RSS;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.VirtuosoDatabase;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String PLUGIN_URI = "http://www.zone-project.org/plugins/ExtractArticlesContent";
    public static String PLUGIN_RESULT_URI = "http://www.zone-project.org/plugins/ExtractArticlesContent#result";
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Item[] items = VirtuosoDatabase.getItemsNotAnotatedForOnePlugin(PLUGIN_URI);
        System.out.println("ExtractArticlesContent has "+items.length+" items to annotate");
        for(Item item : items){
            try {
                System.out.println("Add ExtractArticlesContent for item: "+item);
                URL url = new URL(item.getUri());
                String content= ArticleExtractor.INSTANCE.getText(url);
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_RESULT_URI,content));
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
            } catch (BoilerpipeProcessingException ex) {
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
                VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(PLUGIN_RESULT_URI,item.getElement(RSS.description)));
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
