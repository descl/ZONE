package org.zoneproject.extractor.plugin.extractarticlescontent;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.VirtuosoDatabase;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Test {
    
    public static void main(String[] args) throws IOException {
        try {
            String u = "http://www.predim.org/spip.php?article4343";
            u = "http://cursus.edu/evenement/18861/malin-pratique-textile-depolluant-est-recompense/";
            u = "http://www.heacademy.ac.uk/events/detail/subjects/bioscience/creativity-and-engagement-in-higher-education-conference";
            u = "http://publigeekaire.com/2012/05/une-affiche-de-pub-tatouee-sur-de-la-vraie-peau/";
            u = "http://www.education.gouv.fr/cid61619/l-acquisition-de-competences-par-les-eleves-de-sixieme-en-mathematiques.html";
            u = "http://feedproxy.google.com/~r/TEDTalks_video/~3/PkxS4n1GYs8/sue_austin_deep_sea_diving_in_a_wheelchair.html";
            u = "http://www.lemonde.fr/idees/article/2013/03/20/l-universite-francaise-doit-etre-une-priorite-nationale_1850842_3232.html";
            u = "http://enseignementsup.blog.lemonde.fr/2013/03/14/loi-sur-lenseignement-superieur-des-avancees-et-des-inquietudes-pour-terra-nova/";
            u = "http://feedproxy.google.com/~r/Edudemic/~3/6Nf5jbjwdyM/";
            Item item = VirtuosoDatabase.getOneItemByURI(u);
            
            URL url = new URL(item.getUri());
            //URL url = new URL(text);
            URLConnection conn = url.openConnection();
            // fake request coming from browser
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
          InputStream inStream = conn.getInputStream();
          InputSource input = new InputSource(inStream);
            
            String content= ArticleExtractor.INSTANCE.getText(input).replace("\u00A0", " ").trim();
            
            System.out.println(content);
            //ArticleExtractor.INSTANCE.ge
            /*String title = item.getTitle().trim();
            String description = item.getDescription().trim().substring(0,20);
            
            //System.out.println("_-_"+description+"__");
            
            //System.out.println("__"+clean(content));
            if(content.contains(description)){
                //System.out.println("rue");
                content = content.substring(content.indexOf(description));
            }
            
            if(content.contains(title)){
                content = content.substring(content.indexOf(title)+title.length());
            }
            //content = content.replace("Ce que les patients changent à la santé ","");
            
            System.out.println("_____________________________________________________________________"+description+"__");
            System.out.println("__"+content.replace("\n", " "));*/
        } catch (BoilerpipeProcessingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static String clean(String clean){
        clean = clean.trim();
        clean = clean.replace("\n", "<br/>");
        return clean;
    }
}
