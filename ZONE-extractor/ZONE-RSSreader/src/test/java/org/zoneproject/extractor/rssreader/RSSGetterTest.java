package org.zoneproject.extractor.rssreader;

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
