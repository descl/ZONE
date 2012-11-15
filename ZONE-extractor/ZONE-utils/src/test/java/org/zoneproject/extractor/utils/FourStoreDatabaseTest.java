package org.zoneproject.extractor.utils;

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
public class FourStoreDatabaseTest {
    
    public FourStoreDatabaseTest() {
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
        System.out.println("addItem");
        String uri="http://testURI.com/#MyURI";
        Item item = new Item(uri);
        item.addProp(new Prop("http://purl.org/rss/1.0/title","le titre",true));
        
        /*FourStoreDatabase.addItem(item);
        assertEquals(true,FourStoreDatabase.ItemURIExist(uri));
        FourStoreDatabase.deleteItem(uri);
        
        assertEquals(false,FourStoreDatabase.ItemURIExist(uri));
        FourStoreDatabase.addItem(item);
        assertEquals(true,FourStoreDatabase.ItemURIExist(uri));
        FourStoreDatabase.deleteItem(uri);
        assertEquals(false,FourStoreDatabase.ItemURIExist(uri));*/
    }
}
