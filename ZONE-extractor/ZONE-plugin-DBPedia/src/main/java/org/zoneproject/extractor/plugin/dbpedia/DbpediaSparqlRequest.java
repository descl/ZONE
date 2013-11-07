package org.zoneproject.extractor.plugin.dbpedia;

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
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.sparql.lang.Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.zoneproject.extractor.utils.Database;
import org.zoneproject.extractor.utils.Prop;
import org.zoneproject.extractor.utils.ZoneOntology;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class DbpediaSparqlRequest {
    private static final org.apache.log4j.Logger  logger = org.apache.log4j.Logger.getLogger(DbpediaSparqlRequest.class);
    
    public static String[] getEmptyAnnotations(int limit){
        String request = "SELECT DISTINCT(?propURI) WHERE{ ?uri <http://zone-project.org/model/plugins/Spotlight#entities> ?propURI. OPTIONAL {?propURI <"+ZoneOntology.PLUGIN_DBPEDIA+"> ?pluginDbpedia.  } FILTER (!bound(?pluginDbpedia)) } LIMIT "+limit;
        logger.info(request);
        ResultSet results;
        try{
            results = Database.runSPARQLRequest(request);
        }catch(JenaException ex){
            if(ex.getMessage().contains("timeout") || ex.getMessage().contains("Problem during serialization") || ex.getMessage().contains("Connection failed") ){
                logger.warn(ex);
                logger.warn("connection lost with server (wait 5 secondes)");
                Database.initStore();
                try{Thread.currentThread().sleep(5000);}catch(InterruptedException ie){}
                return getEmptyAnnotations(limit);
            }else{
                logger.warn(ex);
                logger.warn("Encoding error in some uri's request:"+request);
                return null;
            }
        }
        
        
        //we store all the results in a HashMap for each item
        QuerySolution result;
        ArrayList<String> items = new ArrayList<String>();
        while (results.hasNext()) {
            result = results.nextSolution();
            items.add(result.get("?propURI").toString());
        }
        return items.toArray(new String[items.size()]);
    }
    
    
    /*public static ArrayList<Prop> getDimensions(String[] cities){
        ArrayList<Prop> result = new ArrayList<Prop>();
        for(int i=0; i< cities.length;i++){
            result.addAll(getDimensions(cities[i]));
        }
        return result;
    }
    
    public static ArrayList<Prop> getDimensions(String city){
          String query="PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +

                "SELECT * WHERE" +
                "{" +
                "  <http://dbpedia.org/resource/Nice> dbpedia-owl:country ?c." +
                "  <http://dbpedia.org/resource/Nice> dbpprop:region ?r." +
                "  <http://dbpedia.org/resource/Nice> dbpedia-owl:department ?d." +
                "}";
        logger.info(query);
        
        //ResultSet res = Database.runSPARQLRequest(query,"http://rdf.insee.fr/geo/2011/");
        ResultSet res = Database.runSPARQLRequest(query);
        //System.out.println(res.next());
        ArrayList<Prop> dims = new ArrayList<Prop>();
        while (res.hasNext()) {
            QuerySolution r = res.nextSolution();
            dims.add(new Prop(r.get("?type").toString(), r.get("?c").toString(),false,true));
            dims.add(new Prop(r.get("?type").toString(), r.get("?r").toString(),false,true));
            dims.add(new Prop(r.get("?type").toString(), r.get("?d").toString(),false,true));

        }
        
        return dims;
    }
    
    public static ArrayList<Prop> getProperties(ArrayList<Prop> locations){
        
        ArrayList<String> arrElements = new ArrayList<String>();
        for(int i =0; i < locations.size(); i++){
            Prop cur = locations.get(i);
            arrElements.add(cur.getValue());
        }
        return getDimensions(arrElements.toArray(new String[arrElements.size()]));
    }
    
    public static void main(String[] args){
        logger.info((new Date()).toString());
        logger.info(getDimensions("http://www.dbpedia.org/resource/Toulon"));
        logger.info((new Date()).toString());
        logger.info("Done");
    }*/
    
    public static ArrayList<Prop> getInfos(String itemURI){
        HashMap<String,Prop> result = new HashMap<String,Prop>();
        
        
        
        String lang = "en";
        String endpoint = "http://dbpedia.org/sparql";
        if(itemURI.startsWith("http://fr.dbpedia")){
            lang="fr";
            endpoint = "http://fr.dbpedia.org/sparql";
        }
        String url = itemURI;
        String queryString = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
            " select ?label ?abstract ?thumb ?type where {" +
            "  {"+
            "  <"+url+"> <http://www.w3.org/2000/01/rdf-schema#label> ?label." +
            "  OPTIONAL{ <"+url+"> dbpedia-owl:abstract ?abstract.}" +
            "  OPTIONAL{ <"+url+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type.}" +
            "  OPTIONAL{ <"+url+"> dbpedia-owl:thumbnail ?thumb}." +
            "  Filter((!bound(?abstract) || lang(?abstract) = \""+lang+"\") && (lang(?label) = \""+lang+"\"))}" +
            "} LIMIT 100";
        logger.info(queryString);
        QueryExecution qexec;
        if(lang.equals("fr")){
            if(url.contains(" ")) {
                return null;
            }
            //fix dbpedia fr bug on uri whith accents...
            Query query = new Query() ;
            query.setSyntax(Syntax.defaultSyntax);
            Parser parser = Parser.createParser(Syntax.defaultSyntax) ;
            parser.parse(query, queryString) ;
            qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        }else{
            qexec = QueryExecutionFactory.sparqlService(endpoint, queryString);
        }
        try {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution curSol = results.nextSolution();
                if(curSol.get("?label") != null){
                result.put("http://www.w3.org/2000/01/rdf-schema#label", new Prop("http://www.w3.org/2000/01/rdf-schema#label",curSol.get("?label").asLiteral().getString()));
                }
                if(curSol.get("?abstract") != null){
                    result.put("http://dbpedia.org/ontology/#abstract", new Prop("http://dbpedia.org/ontology/#abstract",curSol.get("?abstract").asLiteral().getString()));
                }
                if(curSol.get("?thumb") != null){
                    result.put("http://dbpedia.org/ontology/#thumbnail", new Prop("http://dbpedia.org/ontology/#thumbnail",curSol.get("?thumb").toString(),false));
                }
                
                if( curSol.contains("?type")){
                    
                    //entreprises
                    if( curSol.get("?type").toString().equals("http://schema.org/Organization") ||
                        curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Organization") ||
                        curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Business") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TheHersheyCompanyBrands") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Fast-foodChainsOfTheUnitedStates") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BainCapitalCompanies") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/YagoLegalActor") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/RestaurantChainsInTheUnitedStates") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MultinationalFoodCompanies") ||
                        curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/McDonald'sFoods")
                            ){
                        result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://schema.org/Organization",false));
                    }
                    //informatique
                    else if(
                            curSol.get("?type").toString().equals("http://dbpedia.org/ontology/Work") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FundamentalPhysicsConcepts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ProgrammingConstructs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BigTableImplementations") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerFileFormats") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ConfigurationFiles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Higher-orderFunctions") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Subroutines") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SoftwareDesignPatterns") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/WebApplications") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/GUIWidgets") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ApplicationsOfCryptography") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FunctionsAndMappings") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ISOStandards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ElectronicDocuments") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/WorldWideWebConsortiumStandards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LogicalExpressions") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ElectronicDocuments") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CascadingStyleSheets") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AnimationTechniques") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/URISchemes") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DirectedGraphs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DatabaseIndexTechniques") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PaymentSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ProgrammingPrinciples") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Web-relatedConferences") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpenData") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpenStandards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpenMethodologies") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/HTMLTags") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/XML-basedStandards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ProgrammingParadigms") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FormalMethods") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FormalLanguages") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/RuleEngines") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TheoriesOfHistory") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/GraphicsFileFormats") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/EngineeringConcepts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Systems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LinearFilters") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpenHardwareVehicles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AlgorithmDescriptionLanguages") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ETLTools") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/SoftwareObject") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerConferences") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/GrotesqueSans-serifTypefaces") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LinkProtocols") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/EmbeddedSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UserSpaceFileSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SoftwareAnomalies") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TuringTests") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AuthenticationMethods") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SoftwareBugs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ArchiveNetworks") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DistributedFileSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/NamingConventions") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Typewriters") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/NetworkAnalyzers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerFileSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Service-orientedArchitectureRelatedProducts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Blogs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MediaContentRatingsSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerData") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/StandardizedTests") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ApplicationsOfBayesianInference") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SpreadsheetFileFormats") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerLanguages") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Relationship113780719") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AppleInc.Conferences") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Web2.0Neologisms") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Networks") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/InternetSearchEngines") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/GraphicalUserInterfaceElements") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Exponentials") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/StatisticalChartsAndDiagrams") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CommunicationCircuits") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PhysicalEntity100001930") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CloudPlatforms") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Object100002684") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UserInterfaces") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/HTTPHeaders") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PersonalDigitalAssistants") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TelephonySignals") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerStorageDevices") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MobilePhoneStandards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Measure100033615") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpticalDiodes") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AuthenticationMethods") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/RichInternetApplications") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/JavaSpecificationRequests") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AdobeSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerKeys") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/GraphicalConceptsInSetTheory") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerKeyboards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/InternetForums") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AlertMeasurementSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SonyProducts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FunctionalDataStructures") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Fault-tolerantComputerSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UserGroups") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SoftwareVersionHistories") ||
                            curSol.get("?type").toString().contains("software") ||
                            curSol.get("?type").toString().contains("Software") ||
                            curSol.get("?type").toString().contains("Hardware") ||
                            curSol.get("?type").toString().contains("Programming") ||
                            curSol.get("?type").toString().contains("Communication") ||
                            curSol.get("?type").toString().contains("internet") ||
                            curSol.get("?type").toString().contains("Network") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SerialNumbers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Arrays") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Identifiers") ||
                            curSol.get("?label").toString().contains("computing") ||
                            curSol.get("?label").toString().contains("computer") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ApplicationLayerProtocols") ||
                            curSol.get("?type").toString().contains("FileSystem") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LightweightMarkupLanguages") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BrokenBlockCiphers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DataStructures") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CascadingStyleSheets")
                            ){
                        result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://dbpedia.org/ontology/Work",false));
                    }
                    //personnes
                    else if(
                            curSol.get("?type").toString().equals("http://dbpedia.org/ontology/Person") ||
                            curSol.get("?type").toString().equals("http://xmlns.com/foaf/0.1/Person")){
                        result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://dbpedia.org/ontology/Person",false));
                    }
                    //lieux
                    else if(curSol.get("?type").toString().equals("http://dbpedia.org/ontology/Place") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/YagoGeoEntity")
                            ){
                        result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://dbpedia.org/ontology/Place",false));
                    }
                    //evenements
                    else if(curSol.get("?type").toString().equals("http://dbpedia.org/ontology/Event") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BusinessConferences")
                            ){
                        result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://dbpedia.org/ontology/Event",false));
                    }
                    //reste
                    else if(curSol.get("?type").toString().equals("http://www.w3.org/2004/02/skos/core#Concept") ||
                            curSol.get("?type").toString().equals("http://www.w3.org/2002/07/owl#Thing") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SocialClasses") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/AilmentCondition") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/YouthRights") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Economies") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/EvaluationMethods") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SocialNetworks") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/EducationalStages") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ElectionsInTheUnitedStates") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/YagoPermanentlyLocatedEntity") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/IntelX86Microprocessors") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/IntelMicroprocessors") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerConnectors") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ComputerPeripherals") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UniversalIdentifiers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CryptographicProtocols") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/InternetProtocols") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CharacterSets") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SocialPrograms") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MachineTools") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TheoriesSurroundingTheJohnF.KennedyAssassination") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpticalDevices") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/ontology/Genre") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PocketKnives") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MechanicalHandTools") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SpaceStations") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TypographicalSymbols") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Securities") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FlagsOfTheUnitedKingdom") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UpcomingAutomobiles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Tax-advantagedSavingsPlans") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MediaFormats") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Standards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SequencedGenomes") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MolecularBiologyTechniques") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SatelliteNavigationSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UnitsOfInformation") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/AmericanInventions") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/SocialTheories") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BritishInventions") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Anima") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Polymers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Neurons") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DecorationsOfTheRoyalNavy") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/HumanRights") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/EducationalMaterials") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/OpenMethodologies") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BicycleParts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Fragrances") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FoodsFeaturingButter") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/StringInstruments") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Standards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LaborRights") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Demons") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Clocks") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CoinsOfAncientRome") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LaborRights") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TournamentSystems") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/HistoricalEras") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PhotographicTechniques") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/BatteryElectricVehicles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LegalDocuments") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Rafts") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ChineseFoodPreparationUtensils") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UnitsOfMeasure") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Trucks") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CourageAwards") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UnitedNationsConventionsAndCovenants") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MedicalSpecialties") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/IARCGroup1Carcinogens") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Rights") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Coolants") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Artifact100021939") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Animal") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FictionalCharactersWhoHaveMentalPowers") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Caps") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/DogBreeds") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Magazine") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Particulates") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Vitamins") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/TerpenesAndTerpenoids") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/StapleFoods") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CanonEFLenses") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/PersonalTaxes") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Jackets") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/WovenFabrics") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ArtMovements") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Chairs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FinancialCrises") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LegendaryCreaturesOfTheIndigenousPeoplesOfNorthAmerica") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MeasuringInstruments") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MobileTelecommunications") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Chords") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Insect") ||
                            curSol.get("?type").toString().equals("http://umbel.org/umbel/rc/Plant") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Riddles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/2005Singles") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MotivationalTheories") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Options") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Options") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UnitsOfMass") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/UnderlyingPrinciplesOfMicroeconomicBehavior") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Theories") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/InsecticideBrands") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Metaphors") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/LieGroups") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FinancialRatios") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Cadences") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/120MmDiscs") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/CompilationAlbums") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Monoliths") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/MedievalLegends") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/Clouds") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/JournalismGenres") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/FictionalWeaponsOfMassDestruction") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ConspiracyTheories") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/ClimbingTechniques") ||
                            curSol.get("?type").toString().equals("http://dbpedia.org/class/yago/StockCharacters") ||
                            curSol.get("?type").toString().equals("http://www.opengis.net/gml/_Feature")){
                        
                    }
                }
            }
            if(!result.containsKey("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                result.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", new Prop("http://www.w3.org/1999/02/22-rdf-syntax-ns#type","http://www.w3.org/2004/02/skos/core#Concept",false));
            }
            
            
            results = qexec.execSelect();
            
            //check if label is defined and assigned a default one
            if(result.get("http://www.w3.org/2000/01/rdf-schema#label") == null){
                String label = itemURI.substring(itemURI.lastIndexOf("/")+1).replaceAll("_", " ");
                result.put("http://www.w3.org/2000/01/rdf-schema#label", new Prop("http://www.w3.org/2000/01/rdf-schema#label",label));
            }
            Iterator it = result.keySet().iterator();
            ArrayList<Prop> resultArrayList = new ArrayList<Prop>();
            while( it.hasNext()){
                String cle = (String)it.next();
                resultArrayList.add(result.get(cle));
            }
            return resultArrayList;
                
        }catch(Exception ex){
            logger.error(ex);
            return null;
        }
        finally {
           qexec.close();
        }
    }
}
