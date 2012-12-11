package org.zoneproject.extractor.utils;

/*
 * #%L
 * ZONE-utils
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

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class Config {
    private static Properties configFile = null;
    
    public static Properties getConfigFile(){
        if(configFile == null){
            try {
                configFile = new java.util.Properties();
                java.net.URL url = Config.class.getClassLoader().getResource("zone.properties");
                configFile.load(url.openStream());
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return configFile;
    }
    
    public static String getVar(String var){
        return getConfigFile().getProperty(var);
    }
    
}
