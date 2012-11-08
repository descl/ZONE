package org.zoneproject.extractor.plugin.opencalais;

import org.zoneproject.extractor.plugin.opencalais.openCalaisExtractor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Unit test for simple App.
 */
public class AppTest{
     
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
     * Test of getCitiesResult method, of class openCalaisExtractor.
     */
    @org.junit.Test
    public void testGetCitiesResult() {
        System.out.println("getCitiesResult");
        String content = "Reuters se site dans la ville de Paris";
        String[] expResult = {"Paris"};
        String[] result = openCalaisExtractor.getCitiesResult(content);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getPersonsResult method, of class openCalaisExtractor.
     */
    @org.junit.Test
    public void testGetPersonsResult() {
        System.out.println("getPersonsResult");
        String content = "Nicolas Sarkozy est en vacances";
        String[] expResult = {"Nicolas Sarkozy"};
        String[] result = openCalaisExtractor.getPersonsResult(content);
        assertArrayEquals(expResult, result);
    }
}
