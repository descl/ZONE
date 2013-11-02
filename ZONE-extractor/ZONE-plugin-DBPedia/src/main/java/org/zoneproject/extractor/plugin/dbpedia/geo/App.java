package org.zoneproject.extractor.plugin.dbpedia.geo;

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
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App 
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    public static String PLUGIN_URI = ZoneOntology.PLUGIN_DBPEDIA;
    public static int SIM_DOWNLOADS = 500;
        
    public App(){
        String [] tmp = {};
        App.main(tmp);
    }
    
    public static void main(String[] args) {
        
        String[] items = null;
        while(true){
            do {
                items = DbpediaSparqlRequest.getEmptyAnnotations(SIM_DOWNLOADS);
                
                logger.info("DBpedia has "+items.length+" items to annotate");
                for(String item : items){
                    logger.info("Add DBpedia for item: "+item);
                    ArrayList<Prop> props;
                    props = DbpediaSparqlRequest.getInfos(item);
                    Database.addAnnotations(item, props,ZoneOntology.GRAPH_TAGS);
                    Database.addAnnotation(item, new Prop(PLUGIN_URI,"true"),ZoneOntology.GRAPH_TAGS);
                }
            }while(items == null || items.length > 0);
            
            try{Thread.sleep(60*60*1000);}catch(Exception ie){}
        }
    }
}
