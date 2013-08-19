package org.zoneproject.extractor.plugin.extractarticlescontent;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zoneproject.extractor.utils.Item;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.VirtuosoDatabase;

/*
 * #%L
 * ZONE-plugin-ExtractArticlesContent
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
public class DownloadThread extends Thread  {
  private Item item;
  private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);

  public DownloadThread(Item item) {
    this.item = item;
  }
  public void run() {
      run(0);
  }
  public void run(int restartLevel) {
      if(restartLevel > 5) {
          logger.warn("annotation process imposible for "+item.getUri());
          return;
      }
      if(restartLevel>0)
          try {Thread.currentThread().sleep(5000);} catch (InterruptedException ex1) {}
      try {
          logger.info("Add ExtractArticlesContent for item: "+item);

          URL url = new URL(item.getUri());
          String content= ArticleExtractor.INSTANCE.getText(url).replace("\u00A0", " ").trim();


          String title = item.getTitle().trim();

          if(item.getDescription() != null){
              String description = item.getDescription().trim().substring(0,Math.min(item.getDescription().trim().length(),20));

              if(content.contains(description)){
                  content = content.substring(content.indexOf(description));
              }
          }

          if(content.contains(title)){
              content = content.substring(content.indexOf(title)+title.length());
          }
          content = content.replace("\n", "<br/>");
          VirtuosoDatabase.addAnnotation(item.getUri(), new Prop(App.PLUGIN_RESULT_URI,content));
      } catch (BoilerpipeProcessingException ex) {
          logger.warn("annotation process because of download error for "+item.getUri());
          run(restartLevel+1);
      } catch (MalformedURLException ex) {
          logger.warn("annotation process because of malformed Uri for "+item.getUri());
      } catch (java.io.IOException ex) {
          logger.warn("annotation process because of download error for "+item.getUri());
          run(restartLevel+1);
      }catch (com.hp.hpl.jena.shared.JenaException ex){
          logger.warn("annotation process because of virtuoso partial error "+item.getUri());
          run(restartLevel+1);
      }
  }
}
