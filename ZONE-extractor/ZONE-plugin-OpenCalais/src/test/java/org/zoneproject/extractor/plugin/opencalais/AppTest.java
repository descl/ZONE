package org.zoneproject.extractor.plugin.opencalais;

/*
 * #%L
 * ZONE-plugin-OpenCalais
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
        String content = "Nicolas Sarkozy est en vacances et François Fillon est présent!";
        String[] expResult = {"François Fillon","Nicolas Sarkozy"};
        String[] result = openCalaisExtractor.getPersonsResult(content);
        assertArrayEquals(expResult, result);
    }
}
