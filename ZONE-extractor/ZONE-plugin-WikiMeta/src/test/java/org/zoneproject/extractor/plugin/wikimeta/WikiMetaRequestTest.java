package org.zoneproject.extractor.plugin.wikimeta;

/*
 * #%L
 * ZONE-plugin-WikiMeta
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

import org.zoneproject.extractor.plugin.wikimeta.WikiMetaRequest;
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
import org.zoneproject.extractor.utils.Prop;

/**
 *
 * @author cdesclau
 */
public class WikiMetaRequestTest {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(App.class);
    
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
        logger.info("getProperties");
        String texte = "Bienvenue à Antibes";
        ArrayList<Prop> result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(1,result.size());
        Prop p = new Prop("http://www.wikimeta.org/Entities#loc.admi","http://www.dbpedia.org/resource/Antibes",false);
        assertEquals(p,result.get(0));
    }

    /**
     * Test of getProperties method, of class WikiMetaRequest.
     */
    @Test
    public void testGetProperties_File() throws URISyntaxException {
        logger.info("getProperties");
        URI fileURI = getClass().getResource("/WikiMetaOutput_pip.json").toURI();
        ArrayList result = WikiMetaRequest.getProperties(new File(fileURI));
        assertEquals(4,result.size());
    }
    
    /**
     * Test of getProperties method, of class WikiMetaRequest.
     */
    @Test
    public void testGetProperties_File_2() throws URISyntaxException {
        logger.info("getProperties");
        URI fileURI = getClass().getResource("/WikiMetaOutput_mars.txt").toURI();
        ArrayList result = WikiMetaRequest.getProperties(new File(fileURI));
        ArrayList<Prop> expRes = new ArrayList<Prop>();
        expRes.add(new Prop("http://www.wikimeta.org/Entities#LOC","http://www.dbpedia.org/resource/Marseille",false));
        expRes.add(new Prop("http://www.wikimeta.org/Entities#ORG","http://www.dbpedia.org/resource/Le_Figaro",false));
        expRes.add(new Prop("http://www.wikimeta.org/Entities#LOC","Bouches-du-Rhône",true));

        assertEquals(expRes.size(), result.size());
        for(Prop p: expRes){
            assertTrue(result.contains(p));
        }
        
    }
    
    
    @Test
    public void testGetProperties_String_cleaningResult() {
        logger.info("getProperties");
        String texte = "Arnaud Montebourg est Arnaud Montebourg";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(1,result.size());
    }
    
    @Test
    public void testGetProperties_String_NullStrangeError(){
        logger.info("getProperties");
        String texte = " Sur la Toile, il se faisait appeler Jack Morg";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        ArrayList<Prop> expRes = new ArrayList<Prop>();
        expRes.add(new Prop("http://www.wikimeta.org/Entities#LOC","Toile",true));
        expRes.add(new Prop("http://www.wikimeta.org/Entities#PERS","Jack Morg",true));
        assertEquals(expRes.size(), result.size());
        for(Prop p: expRes){
            assertTrue(result.contains(p));
        }
        
    }
    
    @Test
    public void testGetnamedEntities_String_JSONError(){
        logger.info("getNamedEntities");
        String texte = "L'horloge de Windows 8 n'a pas toujours la bonne heure";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        ArrayList<Prop> expRes = new ArrayList<Prop>();
        expRes.add(new Prop("http://www.wikimeta.org/Entities#prod","http://www.dbpedia.org/resource/Windows_8",false));
        assertEquals(expRes.size(), result.size());
        for(Prop p: expRes){
            assertTrue(result.contains(p));
        }
    }
        
    @Test
    public void testGetnamedEntities_String_JSONError_2(){
        logger.info("getNamedEntities");
        String texte = "Online Learning Gets Massive, Open.  by Garry Kranz, Workforce Recruiting company Aquent is using a new twist on online learning to help its clients hire next-generation Web developers. Faced with job requests from companies that it could not fill, the Boston-based specialized recruiter for ad agencies in 2012 launched a massive open online course, or MOOC, on skills related to HTML5, [...]. by Garry Kranz, WorkforceRecruiting company Aquent is using a new twist on online learning to help its clients hire next-generation Web developers. Faced with job requests from companies that it could not fill, the Boston-based specialized recruiter for ad agencies in 2012 launched a massive open online course, or MOOC, on skills related to HTML5, the latest version of the markup language that defines how Internet content gets structured. Ad agencies need Web developers well-versed in mobile technologies such as HTML5, yet many code writers seem to lack the necessary skills to compete for available jobs, said Alison Farmer, Aquents vice president of learning and development. Even though unemployment was high, companies were telling us that most candidates werent qualified, Farmer said. We wondered: How do we take candidates that may have been competitive a year ago and help them acquire emerging skills?";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(16, result.size());
    }
    
    @Test
    public void testGetnamedEntities_String_JSONError_3(){
        logger.info("getNamedEntities");
        String texte = "l";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testGetnamedEntities_String_JSONError_4(){
        logger.info("getNamedEntities");
        String texte = "Meuh.\n Parce";//rbelle.com Are farm-themed cookies on your to-do list? Then this cute. Parce que je suis normande et fière de l'êtreAre farm-themed cookies on your to-do list? Then this cute little cow cookie is just what you need. ";//Believe it0 commentaire(s)Votre nom (prénom ou pseudo)Votre emailMerci de recopier le texte de l'image ci-dessusRechercher :Le principe du siteLe site \"Un tour en cuisine\"       a pour vocation de présenter aux internautes des recettes       de cuisine venues de tous horizons. Deux thématiques sont abordées.Une fois par jour, sur le blog ,        une recette de cuisine est présentée.       Il s'agit d'une jolie recette qui comble autant les yeux que        les estomacs. On peut alors parler d'art culinaire.En parallèle du blog, des tours sont organisés pour favoriser le partage       entre blogueurs et la découverte de nouveaux blogs culinaires.       ";//Ces tours favorisent la découverte de recettes de tous les jours,       sans aucune obligation d'esthétique.Zoom sur le blogLe monde regorge de gens de talents.Chaque jour, à 21h, la photo d'une recette d'un autre blog est publiée       avec un lien vers l'article d'origine.        Il s'agit de découvrir le travail de quelqu'un        qui a concocté une recette originale et esthétique, une recette        à réaliser en famille, sous l\u0019oeil fasciné de vos enfants.Cela peut être des plantes fabriquées à partir de bonbons,        comme des animaux à partir de légumes.        Le mot clé de ce site est d'étonner et de donner envie de suivre        l'exemple de ces fabuleux cuisiniers dans la cuisine de Monsieur Madame        Tout Le Monde.Proposer votre recette !";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(1, result.size());
    }
        
    @Test
    public void testGetnamedEntities_String_JSONError_5(){
        logger.info("getNamedEntities");
        String texte = "'Grinder' can listen to music player by wearing a loose wire coil around his neck Don't offer Rich Lee a pair of headphones to listen to music: he's already got a pair, even though you can't see them. They're implanted in his ears – a procedure carried out by a \"body modification\" expert. Now, by connecting his music player to a loose wire coil around his neck (which he can tuck under his shirt), Lee can listen to music without blocking out the outside world. The tiny magnets implanted invisibly in his outer ears pick up the signal and generate sound. But that's only the beginning. Lee, 34, who works as a salesman, intends to hook it up to an ultrasonic rangefinder – effectively giving himself bat-like echolocation. And he would like to have X-ray vision, super-strength, and anything else that gene therapy or technology might be able to offer. While thousands of people around the world having had their bodies \"augmented\" through standard medical procedures – such as artificial hips, cochlear implants, pacemakers, heart valves, and of course breast implants – the idea that you might want to have headphone implants might not seem odd. Lee describes his aim as \"self-augmentation and enhancement\": \"If I see a way to eliminate the need for sleep I will never sleep again,\" he told the Guardian. \"If I can have x-ray vision through some cybernetic technology I will have it, even if it requires an ocular prosthetic that leaves me looking like a monster. If I discover a gene therapy that will give me super strength, I will augment my very DNA to do so. If this gives me an advantage over someone else in the workforce, so be it. I do not abide by the restraints imposed by ethics committees who attempt to regulate human enhancement. Their arguments will be obsolete 10 years from now.\" He says that he refuses to be \"caged by my DNA\". Lee, of St George in Utah, is one of the growing subculture of \"grinders\" – a group of a few hundred people around interested in adding technology to their bodies to improve them. Yet while procedures such as breast implants have become routine, he and other \"grinders\" often have difficulty locating skilled people who will carry out the procedures they want. \"My procedure was done by body modification master, Steve Haworth. Doctors don't touch this sort of thing. Plastic surgeons might be convinced to, but it would be at an outrageous price I'm sure,\" said Lee. That has meant going to others – including Haworth, regarded by other grinders as \"the godfather of body modification\", who has been experimenting with additions to the body since 1998. Yet Lee isn't simply motivated by the desire to enhance himself. He lost a significant amount of the vision in his right eye for unexplained reasons; and his doctor has warned him that the left eye might follow suit, leaving him legally blind. \"A cornea transplant will be my only option and a bit out of my budget at the moment,\" he remarked. \"So I figure learning to navigate with echolocation is a good thing to develop now. Not that I've resigned myself to blindness or anything.\" Charles Arthur guardian.co.uk © 2013 Guardian News and Media Limited or its affiliated companies. All rights reserved. | Use of this content is subject to our Terms & Conditions | More Feeds     ";//rbelle.com Are farm-themed cookies on your to-do list? Then this cute. Parce que je suis normande et fière de l'êtreAre farm-themed cookies on your to-do list? Then this cute little cow cookie is just what you need. ";//Believe it0 commentaire(s)Votre nom (prénom ou pseudo)Votre emailMerci de recopier le texte de l'image ci-dessusRechercher :Le principe du siteLe site \"Un tour en cuisine\"       a pour vocation de présenter aux internautes des recettes       de cuisine venues de tous horizons. Deux thématiques sont abordées.Une fois par jour, sur le blog ,        une recette de cuisine est présentée.       Il s'agit d'une jolie recette qui comble autant les yeux que        les estomacs. On peut alors parler d'art culinaire.En parallèle du blog, des tours sont organisés pour favoriser le partage       entre blogueurs et la découverte de nouveaux blogs culinaires.       ";//Ces tours favorisent la découverte de recettes de tous les jours,       sans aucune obligation d'esthétique.Zoom sur le blogLe monde regorge de gens de talents.Chaque jour, à 21h, la photo d'une recette d'un autre blog est publiée       avec un lien vers l'article d'origine.        Il s'agit de découvrir le travail de quelqu'un        qui a concocté une recette originale et esthétique, une recette        à réaliser en famille, sous l\u0019oeil fasciné de vos enfants.Cela peut être des plantes fabriquées à partir de bonbons,        comme des animaux à partir de légumes.        Le mot clé de ce site est d'étonner et de donner envie de suivre        l'exemple de ces fabuleux cuisiniers dans la cuisine de Monsieur Madame        Tout Le Monde.Proposer votre recette !";
        ArrayList result = WikiMetaRequest.getProperties(texte);
        logger.info(result);
        assertEquals(32, result.size());
    }
}
