package org.zoneproject.extractor;

/*
 * #%L
 * ZONE-extractor-start
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

import java.util.Timer;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class App {
    
    public static void main(String[] args)  {
       Timer timer = new Timer();

       String [] apps = new String[5];
       
       apps[0] = "org.zoneproject.extractor.rssreader.App";
       apps[1] = "org.zoneproject.extractor.plugin.extractarticlescontent.App";
       apps[2] = "org.zoneproject.extractor.plugin.opencalais.App";
       apps[3] = "org.zoneproject.extractor.plugin.wikimeta.App";
       apps[4] = "org.zoneproject.extractor.plugin.inseegeo.App";
       for(String app : apps){
           timer.scheduleAtFixedRate( new ThreadApp(app),0, 30000); 
       
       }
    }
}
