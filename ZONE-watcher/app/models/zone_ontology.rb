class ZoneOntology < ActiveRecord::Base
  PLUGIN = "http://zone-project.org/model/plugins"

  PLUGIN_EXTRACT_ARTICLES_CONTENT =  "#{PLUGIN}/ExtractArticlesContent"
  PLUGIN_EXTRACT_ARTICLES_CONTENT_RES = "#{PLUGIN}/ExtractArticlesContent#result"
  PLUGIN_INSEEGEO = "#{PLUGIN}/INSEEGeo"
  PLUGIN_OPENCALAIS = "#{PLUGIN}/OpenCalais"
  PLUGIN_WIKIMETA = "#{PLUGIN}/WikiMeta"
  PLUGIN_SVM = "#{PLUGIN}/Categorization_SVM"
  PLUGIN_SVM_RES = "#{PLUGIN}/Categorization_SVM#result"
  PLUGIN_SPOTLIGHT_ENTITIES = "#{PLUGIN}/Spotlight#entities"
  ANNOTATION = "http://zone-project.org/model/annotation"
  
  GRAPH_ITEMS = "http://zone-project.org/datas/items";
  GRAPH_SOURCES= "http://zone-project.org/datas/sources"
  GRAPH_TAGS= "http://zone-project.org/datas/tags"
  
  SOURCES_PREFIX = "http://zone-project.org/model/sources"
  SOURCES_TYPE = "#{SOURCES_PREFIX}#Source"
  SOURCES_LANG = "#{SOURCES_PREFIX}#lang"

  SOURCES_THUMB = "#{SOURCES_PREFIX}#thumb"
  SOURCES_THEME = "#{SOURCES_PREFIX}#theme"
  SOURCES_OWNER = "#{SOURCES_PREFIX}#owner"

  SOURCES_TYPE_RSS = "#{SOURCES_PREFIX}#RSS"

  SOURCES_TYPE_TWITTER = "#{SOURCES_PREFIX}#twitter"

  SOURCES_TYPE_TWITTER_TIMELINE = "#{SOURCES_TYPE_TWITTER}/timeline"
  SOURCES_TWITTER_TOKEN = "#{SOURCES_PREFIX}#twitterToken"
  SOURCES_TWITTER_TOKEN_SECRET = "#{SOURCES_PREFIX}#twitterSecretToken"

  SOURCES_TYPE_TWITTER_HASHTAG="#{SOURCES_TYPE_TWITTER}/hashtag"
  SOURCES_TYPE_TWITTER_AUTHOR="#{SOURCES_TYPE_TWITTER}/author"

  PLUGIN_TYPE_TWITTER = "#{PLUGIN}/twitter"
  PLUGIN_TWITTER_HASHTAG="#{PLUGIN_TYPE_TWITTER}#hashtag"
  PLUGIN_TWITTER_AUTHOR="#{PLUGIN_TYPE_TWITTER}#author"

  PLUGIN_SOCIAL_ANNOTATION = "#{PLUGIN}/socialAnnotation"

  RSS_SOURCE= "http://purl.org/rss/1.0/source"

  ZONE_USER = "http://zone-project.org/datas/users/"
  ZONE_FAVORITE = "http://zone-project.org/model/items#favorite"

  RDF_LABEL = "http://www.w3.org/2000/01/rdf-schema#label"
  RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
  DBPEDIA_ABSTRACT = "http://dbpedia.org/ontology/#abstract"
  DBPEDIA_THUMB = "http://dbpedia.org/ontology/#thumbnail"
end