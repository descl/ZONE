package org.zoneproject.extractor.plugin.inseegeo;

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

import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.zoneproject.extractor.plugin.opencalais.openCalaisExtractor;
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
        String city = "Toulon";
        ArrayList<Prop> expResult = new ArrayList<Prop>();
        expResult.add(new Prop("http://rdf.insee.fr/geo/Arrondissement","http://rdf.insee.fr/geo/2011/ARR_832",false));
        expResult.add(new Prop("http://rdf.insee.fr/geo/Pseudo-canton","http://rdf.insee.fr/geo/2011/CAN_8399",false));
        expResult.add(new Prop("http://rdf.insee.fr/geo/Departement","http://rdf.insee.fr/geo/2011/DEP_83",false));
        expResult.add(new Prop("http://rdf.insee.fr/geo/Region","http://rdf.insee.fr/geo/2011/REG_93",false));
        expResult.add(new Prop("http://rdf.insee.fr/geo/Pays","http://rdf.insee.fr/geo/2011/PAYS_FR",false));
        ArrayList<Prop> result = InseeSparqlRequest.getDimensions(city);

        assertEquals(expResult.size(), result.size());
        for(Prop p: expResult){
            assertTrue(result.contains(p));
        }
    }

    /* dont work yet on apostrofs...
    @org.junit.Test
    public void testApp2() {
        logger.info("getCitiesResult");
        String city = "Clermont-L'Hérault";
        ArrayList<Prop> result = InseeSparqlRequest.getDimensions(city);

        assertEquals(0, result.size());
    }*/
}
