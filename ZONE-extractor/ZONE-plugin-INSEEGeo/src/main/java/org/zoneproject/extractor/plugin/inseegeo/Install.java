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

import com.hp.hpl.jena.query.ResultSet;
import java.io.File;
import org.zoneproject.extractor.utils.Database;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Install {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(Install.class);
    public static void main(String[] args) {
        logger.info("Loading RDF Files for the INSEE Database");
        ResultSet r = Database.runSPARQLRequest(" SELECT ?p WHERE { <http://rdf.insee.fr/geo/2011/COM_10378> ?p ?i } LIMIT 10","http://rdf.insee.fr/geo/2011/");
        if(!r.hasNext()){
            Database.loadFolder("http://rdf.insee.fr/geo/2011/",args[0]);
        }
    }
    
}
