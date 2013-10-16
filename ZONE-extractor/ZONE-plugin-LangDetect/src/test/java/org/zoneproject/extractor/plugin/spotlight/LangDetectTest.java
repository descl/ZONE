package org.zoneproject.extractor.plugin.spotlight;

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
import org.zoneproject.extractor.plugin.langdetect.LangDetect;
import com.cybozu.labs.langdetect.DetectorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LangDetectTest {
    
    public LangDetectTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        String profileDirectory = "profiles/";
        LangDetect.init(profileDirectory);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
        
    }

    /**
     * Test of init method, of class LangDetect.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        int size = DetectorFactory.getLangList().size();
        assertEquals(54, size);
    }


    /**
     * Test of detectLang method, of class LangDetect.
     */
    @Test
    public void testDetectLang() throws Exception {
        System.out.println("detectLang");
        String text = "I love zone-project which is the best project ever";
        String expResult = "en";
        String result = LangDetect.detectLang(text);
        assertEquals(expResult, result);
    }
    @Test
    public void testDetectLang2() throws Exception {
        System.out.println("detectLang");
        String text = "J'aime le projet zone qui est un magnifique projet";
        String expResult = "fr";
        String result = LangDetect.detectLang(text);
        assertEquals(expResult, result);
    }
}
