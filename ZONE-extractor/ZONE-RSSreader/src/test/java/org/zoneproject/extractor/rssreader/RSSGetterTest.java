package org.zoneproject.extractor.rssreader;

/*
 * #%L
 * ZONE-RSSreader
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

import org.zoneproject.extractor.rssreader.RSSGetter;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class RSSGetterTest {
    
    public RSSGetterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFlux method, of class RSSGetter.
     */
    @Test
    public void testGetFlux_File() throws URISyntaxException {
        System.out.println("getFlux");
        URI fileURI = getClass().getResource("/europe1.rss").toURI();
        ArrayList result = RSSGetter.getFlux(new File(fileURI));
        System.out.println(result.size());
        assertEquals(result.size(), 1);
    }
    
    /**
     * Test of getFlux method, of class RSSGetter.
     */
    @Test
    public void testGetFlux_URI() {
        System.out.println("getFlux");
        String fileURI = "http://europe1.fr.feedsportal.com/c/32376/f/546041/index.rss";
        ArrayList result = RSSGetter.getFlux(fileURI);
        System.out.println(result.size());
        assertEquals(result.size(), 25);
    }
}
