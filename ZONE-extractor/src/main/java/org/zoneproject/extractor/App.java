package org.zoneproject.extractor;

import java.util.ArrayList;
import org.zoneproject.utils.Config;
import org.zoneproject.utils.FourStoreDatabase;
import org.zoneproject.utils.Item;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ArrayList<Item>  items = new ArrayList<Item>();
        String [] fluxLinks = Config.getVar("rssFeeds").split(",");
        
        System.out.println("========= Starting rss downloading==================");
        ArrayList<Item> it = RSSGetter.getFlux(fluxLinks);
        items.addAll(it);
        
        System.out.println("========= Cleaning flow with already analysed items==================");
        FourStoreDatabase.verifyItemsList(items);
        
        System.out.println("========= Starting annotations adding==================");
        System.out.println("NO! now the plugins do it!");
        //AnnotationsGesture.addAnnotations(items);
        
        System.out.println("========= Printing result items==================");
        for(int i=0; i< items.size();i++)System.out.println("\n"+items.get(i));
        
        System.out.println("========= saving to 4Store database==================");
        FourStoreDatabase.addItems(items);
        
        System.out.println("Done");
    }
}