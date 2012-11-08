/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.rssreader;

import org.zoneproject.rssreader.RSSGetter;
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
