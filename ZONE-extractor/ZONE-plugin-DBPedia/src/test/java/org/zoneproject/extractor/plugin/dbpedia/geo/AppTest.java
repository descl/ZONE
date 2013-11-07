package org.zoneproject.extractor.plugin.dbpedia.geo;

/*
 * #%L
 * ZONE-plugin-INSEEGeo
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

import org.zoneproject.extractor.plugin.dbpedia.DbpediaSparqlRequest;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.zoneproject.extractor.utils.Prop;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class AppTest 
    extends TestCase
{
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(AppTest.class);
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


    @org.junit.Test
    public void testApp1() {
        logger.info("getCitiesResult");
        String entity = "http://fr.dbpedia.org/resource/Frank_Berton";
        ArrayList<Prop> expResult = new ArrayList<Prop>();
        expResult.add(new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://dbpedia.org/ontology/Person",false));
        expResult.add(new Prop("http://www.w3.org/2000/01/rdf-schema#label","Frank Berton",true));
        expResult.add(new Prop("http://dbpedia.org/ontology/#abstract","Frank Berton, né en 1962 à Amiens, est un avocat français spécialisé dans le droit pénal et le droit de la presse. Il fait partie du cabinet ADNS basé à Lille. Il a participé à la défense et procès de plusieurs affaires médiatisées comme l'affaire d'Outreau (2004-2005), l'affaire Cottrez (2010-2011), l'affaire Ponthieux (2010), la ville de Roubaix contre un restaurant Quick proposant du halal (2010) et la défense des familles des otages français tués au Niger par Al-Qaida au Maghreb islamique (2011). Sur intervention de Nicolas Sarkozy, il est désigné en 2008 par la justice française pour suivre l'affaire Florence Cassez.",true));
        ArrayList<Prop> result = DbpediaSparqlRequest.getInfos(entity);
        logger.info(result);

        assertEquals(expResult.size(), result.size());
        for(Prop p: expResult){
            assertTrue(result.contains(p));
        }
    }
}
