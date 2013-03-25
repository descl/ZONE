package org.zoneproject.extractor.plugin.opencalais;

/*
 * #%L
 * ZONE-plugin-OpenCalais
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
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(AppTest.class);
     
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
        logger.info("getCitiesResult");
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
        logger.info("getPersonsResult");
        String content = "Nicolas Sarkozy est en vacances et François Fillon est présent!";
        String[] expResult = {"François Fillon","Nicolas Sarkozy"};
        String[] result = openCalaisExtractor.getPersonsResult(content);
        assertArrayEquals(expResult, result);
    }
}
