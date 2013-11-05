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
            String u = "http://www.predim.org/spip.php?article4343";
            u = "http://cursus.edu/evenement/18861/malin-pratique-textile-depolluant-est-recompense/";
            u = "http://www.heacademy.ac.uk/events/detail/subjects/bioscience/creativity-and-engagement-in-higher-education-conference";
            u = "http://publigeekaire.com/2012/05/une-affiche-de-pub-tatouee-sur-de-la-vraie-peau/";
            u = "http://www.education.gouv.fr/cid61619/l-acquisition-de-competences-par-les-eleves-de-sixieme-en-mathematiques.html";
            u = "http://feedproxy.google.com/~r/TEDTalks_video/~3/PkxS4n1GYs8/sue_austin_deep_sea_diving_in_a_wheelchair.html";
            u = "http://www.lemonde.fr/idees/article/2013/03/20/l-universite-francaise-doit-etre-une-priorite-nationale_1850842_3232.html";
            u = "http://enseignementsup.blog.lemonde.fr/2013/03/14/loi-sur-lenseignement-superieur-des-avancees-et-des-inquietudes-pour-terra-nova/";
            u = "http://feedproxy.google.com/~r/Edudemic/~3/6Nf5jbjwdyM/";
            u = "https://twitter.com/KISSmetrics/status/393386394980085761";
            //u = "https://twitter.com/HerveKabla/status/393386536429166593";
            //u = "https://twitter.com/DPG_BizCoach/status/393639588977446912";
            u = "http://www.leparisien.fr/paris-75/paris-une-femme-jetee-dans-la-seine-25-10-2013-3258049.php";
            u = "https://twitter.com/mathildeD_V/status/393612021000048640";
            u = "https://twitter.com/j_nieuviarts/status/393601656170242048";
            u = "https://twitter.com/amnestyfrance/status/393614437065973760";
            u = "https://twitter.com/Naaooommmmiiiii/status/393580760784834560";
            u = "http://raibledesigns.com/rd/entry/javaone_2013";
            Item item = VirtuosoDatabase.getOneItemByURI(u);
            
            //URL url = new URL(item.getUri());
            String content = ExtractArticleContent.getContent(item);
            //String content= ExtractArticleContent.getContent("http://www.bbc.co.uk/news/world-europe-24658047%20sa-ns_mchannel=rss&ns_source=PublicRSS20-sa/");
            
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
      
    }
    public static String clean(String clean){
        clean = clean.trim();
        clean = clean.replace("\n", "<br/>");
        return clean;
    }
}
