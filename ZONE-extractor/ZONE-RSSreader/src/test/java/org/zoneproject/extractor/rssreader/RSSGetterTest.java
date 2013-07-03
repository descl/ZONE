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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class RSSGetterTest {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(RSSGetterTest.class);
    
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
        logger.info("getFlux");
        URI fileURI = getClass().getResource("/europe1.rss").toURI();
        ArrayList result = RSSGetter.getFlux(new File(fileURI));
        logger.info(result.size());
        assertEquals(result.size(), 1);
    }
    
    /**
     * Test of getFlux method, of class RSSGetter.
     */
    @Test
    public void testGetFlux_URI() {
        logger.info("getFlux");
        String fileURI = "http://europe1.fr.feedsportal.com/c/32376/f/546041/index.rss";
        ArrayList result = RSSGetter.getFlux(fileURI);
        logger.info(result.size());
        assertEquals(result.size(), 25);
    }
    
    /**
     * Test of getFlux method, for an offline feed.
     */
    @Test
    public void testGetFluxSourceNotFound_URI() {
        logger.info("getFlux");
        String fileURI = "http://turlututu.com/x.rss";
        try{
            ArrayList result = RSSGetter.getFlux(fileURI);
        }
        catch (java.lang.NoSuchFieldError e){}
        finally{
            Map sourceMap = Database.getMapForURI(fileURI, ZoneOntology.GRAPH_SOURCES);
            assertEquals(sourceMap.get("http://zone-project.org/model/sources#offline"), "true");
        }
    }
}
