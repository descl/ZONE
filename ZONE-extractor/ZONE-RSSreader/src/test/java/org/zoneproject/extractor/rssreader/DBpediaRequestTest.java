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

import org.zoneproject.extractor.rssreader.DBpediaRequest;
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
public class DBpediaRequestTest {
    
    public DBpediaRequestTest() {
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
     * Test of getCityNameFromURI method, of class DBpediaRequest.
     */
    @Test
    public void testGetCityNameFromURI_1() {
        System.out.println("getCityNameFromURI");
        String uri = "http://dbpedia.org/page/House_of_Lorraine";
        String expResult = "Maison de Lorraine";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
    @Test
    public void testGetCityNameFromURI_2() {
        System.out.println("getCityNameFromURI");
        String uri = "http://dbpedia.org/page/Petit-Couronne";
        String expResult = "Grand-Couronne";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
    @Test
    public void testGetCityNameFromURI_3() {
        System.out.println("getCityNameFromURI");
        String uri = "http://www.dbpedia.org/resource/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques";
        String expResult = "Pau";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
    @Test
    public void testGetCityNameFromURI_4() {
        System.out.println("getCityNameFromURI");
        String uri = "http://www.dbpedia.org/resource/Pau";
        String expResult = "Pau";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
    @Test
    public void testGetCityNameFromURI_5() {
        System.out.println("getCityNameFromURI");
        String uri = "http://dbpedia.org/page/Pau,_Pyr%C3%A9n%C3%A9es-Atlantiques";
        String expResult = "Pau";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetCityNameFromURI_6() {
        System.out.println("getCityNameFromURI");
        String uri = "http://www.dbpedia.org/resource/Antwerp";
        String expResult = "Anvers";
        String result = DBpediaRequest.getCityNameFromURI(uri);
        assertEquals(expResult, result);
    }
}
