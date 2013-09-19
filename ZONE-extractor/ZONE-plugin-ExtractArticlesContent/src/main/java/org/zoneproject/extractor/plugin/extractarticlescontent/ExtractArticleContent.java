package org.zoneproject.extractor.plugin.extractarticlescontent;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.InputSource;
import org.zoneproject.extractor.utils.Item;

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
public class ExtractArticleContent {

    public static String getContent(Item item) throws MalformedURLException, IOException, BoilerpipeProcessingException{
        URL url = new URL(item.getUri());
        URLConnection conn = url.openConnection();
        // fake request coming from browser
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
        String content= ArticleExtractor.INSTANCE.getText(new InputSource(conn.getInputStream())).replace("\u00A0", " ").trim();

        String title = "abcdefghijklmnopqstuvwxyz";
        if(item.getTitle() != null){
            title = item.getTitle().trim();
        }

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
        return content;
    }
}
