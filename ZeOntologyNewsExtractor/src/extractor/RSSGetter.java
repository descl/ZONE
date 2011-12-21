package extractor;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Item;

/**
 *
 * @author descl
 */
public class RSSGetter {
    
    public static ArrayList<Item> getFlux(String[] urls) {
        ArrayList<Item> result = new ArrayList<Item>();
        try {
            for(int i=0; i < urls.length;i++){
                result.addAll(RSSGetter.getFlux(new XmlReader(new URL(urls[i]))));
            }
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public static ArrayList<Item> getFlux(String url) {
        try {
            return RSSGetter.getFlux(new XmlReader(new URL(url)));
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static ArrayList<Item> getFlux(File file) {
        try {
            return getFlux(new XmlReader(file));
        } catch (Exception ex) {
            Logger.getLogger(RSSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static ArrayList<Item> getFlux(XmlReader reader){
        ArrayList<Item> items = new ArrayList<Item>();
        
        SyndFeedInput sfi = new SyndFeedInput();
        try {
            SyndFeed feed = sfi.build(reader);
            List entries = new ArrayList();
            entries = feed.getEntries();

            for (int i = 0; i < entries.size(); i++){
                SyndEntry entry = (SyndEntry)entries.get(i);
                
                //create item
                Item cur = new Item(entry);
                
                //add item to list
                items.add(cur);
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return items;
    }
}
