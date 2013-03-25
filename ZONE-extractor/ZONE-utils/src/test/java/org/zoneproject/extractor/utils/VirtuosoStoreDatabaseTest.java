package org.zoneproject.extractor.utils;

/*
 * #%L
 * ZONE-utils
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

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class VirtuosoStoreDatabaseTest {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    
    public VirtuosoStoreDatabaseTest() {
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
     * Test of getStore method, of class FourStoreDatabase.
     */
    @Test
    public void testAddItem() throws IOException {
        logger.info("addItem");
        String uri="http://testURI.com/#MyURI";
        Item item = new Item(uri);
        item.addProp(new Prop("http://purl.org/rss/1.0/title","le titre",true));
        
        
        VirtuosoDatabase.addItem(item);
        assertEquals(true,VirtuosoDatabase.ItemURIExist(uri));
        VirtuosoDatabase.deleteItem(uri);
        
        assertEquals(false,VirtuosoDatabase.ItemURIExist(uri));
        VirtuosoDatabase.addItem(item);
        assertEquals(true,VirtuosoDatabase.ItemURIExist(uri));
        VirtuosoDatabase.deleteItem(uri);
        assertEquals(false,VirtuosoDatabase.ItemURIExist(uri));
    }
}
