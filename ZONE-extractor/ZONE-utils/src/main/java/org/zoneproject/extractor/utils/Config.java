package org.zoneproject.extractor.utils;

/*
 * #%L
 * ZONE-utils
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
*
* @author Desclaux Christophe <christophe@zouig.org>
*/
public class Config {
    private static Configuration configFile = null;
    
    public static Configuration getConfigFile(){
        if(configFile == null){
            try {
                java.net.URL url = Config.class.getClassLoader().getResource("zone.properties");
                configFile = new PropertiesConfiguration(url);
            } catch (ConfigurationException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return configFile;
    }
    
    public static String getVar(String var){
        return getConfigFile().getString(var);
    }
    
    public static String[] getArrayVar(String var){
        return getConfigFile().getStringArray(var);
    }
}
