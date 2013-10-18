package org.zoneproject.extractor.plugin.langdetect;

/*
 * #%L
 * ZONE-plugin-LangDetect
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
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;


/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class LangDetect {
    public static void init(String profileDirectory) throws LangDetectException, IOException {
        Enumeration<URL> en = Detector.class.getClassLoader().getResources(profileDirectory);
        List<String> profiles = new ArrayList<String>();
        if(en.hasMoreElements()) {
            URL url = en.nextElement();
            JarURLConnection urlcon = (JarURLConnection) url.openConnection();
            JarFile jar = urlcon.getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement().getName();
                if (entry.startsWith(profileDirectory)) {
                    InputStream in = Detector.class.getClassLoader().getResourceAsStream(entry);
                    profiles.add(IOUtils.toString(in));
                }
            }
        }
        DetectorFactory.loadProfile(profiles);

    }
    
    public static ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }
    
    public static String detectLang(String text) {
        if(text.length() < 15)
            return "en";
        
        try {
            double better = 0;
            String betterLang = "en";
            for(Language cur : LangDetect.detectLangs(text)){
                if(better < cur.prob){
                    better = cur.prob;
                    betterLang = cur.lang;
                }
            }
            return betterLang;
        } catch (LangDetectException ex) {
            return "en";
        }
    }
    
    public static void main(String[] args) throws LangDetectException, IOException {
        LangDetect.init("profiles/");
        String text = "Hello my name is descl, i come from ingland. Ce sera un événement musical.";
        System.out.println(LangDetect.detectLang(text));
    }
}
