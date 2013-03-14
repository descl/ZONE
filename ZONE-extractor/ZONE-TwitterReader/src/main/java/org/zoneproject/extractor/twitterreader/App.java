package org.zoneproject.extractor.twitterreader;

/*
 * #%L
 * ZONE-TwitterReader
 * %%
 * Copyright (C) 2012 - 2013 ZONE-project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
