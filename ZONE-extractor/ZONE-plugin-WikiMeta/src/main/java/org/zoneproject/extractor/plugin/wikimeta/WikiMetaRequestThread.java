package org.zoneproject.extractor.plugin.wikimeta;

import java.util.ArrayList;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;

/*
 * #%L
 * ZONE-RSSreader
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
public class WikiMetaRequestThread extends Thread  {
    private Item item;
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);

    public WikiMetaRequestThread(Item item) {
      this.item = item;
    }
    public void run() {
        logger.info("Add WikiMeta for item: "+item);

        ArrayList<Prop> content= WikiMetaRequest.getProperties(item.concat());
        Database.addAnnotations(item.getUri(), content);
        Database.addAnnotation(item.getUri(), new Prop(App.PLUGIN_URI,"true"));
  }
}
