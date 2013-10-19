package org.zoneproject.extractor.plugin.inseegeo;

/*
 * #%L
 * ZONE-plugin-INSEEGeo
 * %%
 * Copyright (C) 2012 ZONE-project
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
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_INSEEGEO;
    
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        String openCalaisPrefix = "http://www.opencalais.org/Entities";
        String [] deps = {openCalaisPrefix+"#LOC"};
        Item[] items = null;
        do {
            items = Database.getItemsNotAnotatedForPluginsWithDeps(PLUGIN_URI,deps);
            if(items == null){
                continue;
            }
            logger.info("INSEEGeo has "+items.length+" items to annotate");
            for(Item item : items){
                logger.info("Add INSEEGeo for item: "+item);
                ArrayList<Prop> props;
                props = InseeSparqlRequest.getDimensions(item.getElements(openCalaisPrefix+"#LOC"));
                Database.addAnnotations(item.getUri(), props);
                Database.addAnnotation(item.getUri(), new Prop(PLUGIN_URI,"true"));
            }
        }
        while(items == null || items.length > 0);
    }
}
