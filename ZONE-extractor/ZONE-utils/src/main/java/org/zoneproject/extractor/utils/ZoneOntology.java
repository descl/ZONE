package org.zoneproject.extractor.utils;

/**
 *
 * @author Desclaux Christophe <christophe@zouig.org>
 */
public class ZoneOntology {
    public static String SOURCES_PREFIX = "http://zone-project.org/model/sources";
    public static String PLUGIN_PREFIX = "http://zone-project.org/model/plugins";
    
    public static String PLUGIN_EXTRACT_ARTICLES_CONTENT =  PLUGIN_PREFIX+"/ExtractArticlesContent";
    public static String PLUGIN_EXTRACT_ARTICLES_CONTENT_RES = PLUGIN_EXTRACT_ARTICLES_CONTENT+"#result";
    
    public static String PLUGIN_INSEEGEO = PLUGIN_PREFIX+"/INSEEGeo";
    public static String PLUGIN_OPENCALAIS = PLUGIN_PREFIX+"/OpenCalais";
    public static String PLUGIN_WIKIMETA = PLUGIN_PREFIX+"/WikiMeta";
    
    public static String PLUGIN_SVM = PLUGIN_PREFIX+"/Categorization_SVM";
    public static String PLUGIN_SVM_RES = PLUGIN_SVM+"#result";
    
    public static String SOURCES_TYPE = SOURCES_PREFIX+"#Source";
    public static String SOURCES_LANG = SOURCES_PREFIX+"#lang";
    public static String SOURCES_THUMB = SOURCES_PREFIX+"#thumb";
    public static String SOURCES_THEME = SOURCES_PREFIX+"#theme";
    public static String SOURCES_OWNER = SOURCES_PREFIX+"#owner";
    
    public static String GRAPH_NEWS = "http://zone-project.org/datas/items";
    public static String GRAPH_SOURCES = "http://zone-project.org/datas/sources";
    
    
}
