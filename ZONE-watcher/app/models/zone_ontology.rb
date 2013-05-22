class ZoneOntology < ActiveRecord::Base
  PLUGIN_EXTRACT_ARTICLES_CONTENT =  "http://zone-project.org/model/plugins/ExtractArticlesContent"
  PLUGIN_EXTRACT_ARTICLES_CONTENT_RES = "http://zone-project.org/model/plugins/ExtractArticlesContent#result"
  PLUGIN_INSEEGEO = "http://zone-project.org/model/plugins/INSEEGeo"
  PLUGIN_OPENCALAIS = "http://zone-project.org/model/plugins/OpenCalais"
  PLUGIN_WIKIMETA = "http://zone-project.org/model/plugins/WikiMeta"
  PLUGIN_SVM = "http://zone-project.org/model/plugins/Categorization_SVM"
  PLUGIN_SVM_RES = "http://zone-project.org/model/plugins/Categorization_SVM#result"
  ANNOTATION = "http://zone-project.org/model/annotation"
  
  GRAPH_ITEMS = "http://zone-project.org/datas/items";
  GRAPH_SOURCES= "http://zone-project.org/datas/sources"
  
  SOURCES_PREFIX = "http://zone-project.org/model/sources"
  SOURCES_TYPE = "#{SOURCES_PREFIX}#Source"
  SOURCES_LANG = "#{SOURCES_PREFIX}#lang"

  SOURCES_THUMB = "#{SOURCES_PREFIX}#thumb"
  SOURCES_THEME = "#{SOURCES_PREFIX}#theme"
  SOURCES_OWNER = "#{SOURCES_PREFIX}#owner"

  SOURCES_TYPE_TWITTER = "#{SOURCES_PREFIX}#twitter"

  SOURCES_TYPE_TWITTER_TIMELINE = "#{SOURCES_TYPE_TWITTER}/timeline"
  SOURCES_TWITTER_TOKEN = "#{SOURCES_PREFIX}#twitterToken"
  SOURCES_TWITTER_TOKEN_SECRET = "#{SOURCES_PREFIX}#twitterSecretToken"

  SOURCES_TYPE_TWITTER_HASHTAG="#{SOURCES_TYPE_TWITTER}/hashTag"

  RSS_SOURCE= "http://purl.org/rss/1.0/source"
  
end