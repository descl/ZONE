package org.zoneproject.extractor.twitterreader;

import java.util.ArrayList;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    public static void main(String args[]){
        ArrayList<Item>  items = new ArrayList<Item>();
        String [] feeds = TwitterApi.getSources();
        System.out.println("========= Starting twitter timeline downloading==================");
        
        ArrayList<Item> it = TwitterApi.getFlux(feeds);
        items.addAll(it);
        
        System.out.println("========= Cleaning flow with already analysed items==================");
          Database.verifyItemsList(items);
        
        System.out.println("========= Printing result items==================");
        for(int i=0; i< items.size();i++)System.out.println("\n"+items.get(i));
        
        System.out.println("========= saving to database==================");
        Database.addItems(items);
        System.out.println("Done");

    System.exit(0);
  }
}
