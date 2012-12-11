package org.zoneproject.extractor.rssreader;

/*
 * #%L
 * ZONE-RSSreader
 * %%
 * Copyright (C) 2012 ZONE-project
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import org.zoneproject.extractor.utils.Config;
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
    public static void main( String[] args )
    {
        ArrayList<Item>  items = new ArrayList<Item>();
        String [] fluxLinks = Config.getVar("rssFeeds").split(",");
        
        System.out.println("========= Starting rss downloading==================");
        ArrayList<Item> it = RSSGetter.getFlux(fluxLinks);
        items.addAll(it);
        
        System.out.println("========= Cleaning flow with already analysed items==================");
        Database.verifyItemsList(items);
        
        System.out.println("========= Printing result items==================");
        for(int i=0; i< items.size();i++)System.out.println("\n"+items.get(i));
        
        System.out.println("========= saving to 4Store database==================");
        Database.addItems(items);
        
        System.out.println("Done");
    }
}