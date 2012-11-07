/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zoneproject.plugin.wikimeta;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cdesclau
 */
public class WikiMetaRequestTest {
    
    public WikiMetaRequestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of getProperties method, of class WikiMetaRequest.
     */
    @Test
    public void testGetProperties_String() {
        System.out.println("getProperties");
        String texte = "Bienvenue à Antibes";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        System.out.println(result);
        assertEquals(1,result.size());
    }

    /**
     * Test of getProperties method, of class WikiMetaRequest.
     */
    @Test
    public void testGetProperties_File() throws URISyntaxException {
        System.out.println("getProperties");
        URI fileURI = getClass().getResource("/WikiMetaOutput_pip.json").toURI();
        ArrayList result = WikiMetaRequest.getProperties(new File(fileURI));
        assertEquals(result.size(), 0);
    }
    
    /**
     * Test of getProperties method, of class WikiMetaRequest.
     */
    @Test
    public void testGetProperties_File_2() throws URISyntaxException {
        System.out.println("getProperties");
        URI fileURI = getClass().getResource("/WikiMetaOutput_mars.json").toURI();
        ArrayList result = WikiMetaRequest.getProperties(new File(fileURI));
        System.out.println(result);
        assertEquals(2,result.size());
    }
    
    
    @Test
    public void testGetProperties_String_cleaningResult() {
        System.out.println("getProperties");
        String texte = "Arnaud Montebourg est Arnaud Montebourg";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        System.out.println(result);
        assertEquals(1,result.size());
    }
}
