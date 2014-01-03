class Item# < ActiveRecord::Base
  extend ActiveModel::Naming
  include ActiveModel::AttributeMethods
  require 'rubygems'
  require 'rest_client'


  OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
  WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
  INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
  RSS_URI = 'http://purl.org/rss/1.0/'
  SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
  TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
  TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'
  LANG_URI = 'http://zone-project.org/model/plugins#lang'

  attr_accessor :uri, :title, :props, :description, :date, :localURI, :similarity, :favorite, :filters

  endpoint = Rails.application.config.virtuosoEndpoint
  update_uri = Rails.application.config.virtuosoEndpoint+"-auth"
  $repo       = RDF::Virtuoso::Repository.new(endpoint,
                                              :update_uri => update_uri,
                                              :username => Rails.application.config.virtuosoLogin,
                                              :password => Rails.application.config.virtuosoPassword,
                                              :auth_method => 'digest')

  def self.all(search = "",user=-1,start=0,per_page=10, minDate=0, maxDate=0)
    sparqlFilter = search.generateSPARQLRequest(user)
    filtersIds = search.getOrFilters.map {|elem| "?filter#{elem.id}"}.join(',')

    endpoint = Rails.application.config.virtuosoEndpoint
    #if start > (10000 - per_page)
    #  return Array.new
    #end
    if sparqlFilter == nil
      return {:result => Array.new, :query => "", :error =>"cheater"}
    end
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
SELECT * WHERE{
    SELECT DISTINCT(?concept),?pubDateTime,  ?title, CONCAT( #{filtersIds} ) AS ?filtersVals
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept RSS:title ?title.
      #{sparqlFilter}"
      query += "?concept RSS:pubDateTime ?pubDateTime."

    if (minDate != 0 || maxDate != 0)
      if (maxDate == 0)
        maxDate = 99999999999999999999999
      end
      query += "FILTER((xsd:integer(?pubDateTime) > #{minDate.to_i}) AND (xsd:integer(?pubDateTime) <= #{maxDate.to_i}))"
    end

    query += "}} ORDER BY DESC(?pubDateTime) LIMIT #{per_page} OFFSET #{start}"

    store = SPARQL::Client.new(endpoint)
    items = Array.new
    store.query(query).each do |item|
      similarity = item.filtersVals.to_s.scan(/http/).count
      itemObj = Item.new(item.concept.to_s, item.title, similarity)
      itemObj.date = Time.now.to_i
      itemObj.date = item.pubDateTime.to_s if item.bound?(:pubDateTime)
      items << itemObj
    end
    return {:result => items, :query => query}
  end
  
  def self.find(param,user)
    endpoint = Rails.application.config.virtuosoEndpoint
    require 'cgi'
    require 'uri'
    uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    #uri = uri.gsub("%23","#")
    uri.gsub!('%25','%')
    if !uri.include?("%25")
      uri.gsub!('/%\d2','%25')
    end
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT *
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_TAGS}>
    WHERE { <#{uri}> ?prop ?value.
            OPTIONAL{?value ?extraProp ?extraValue}";
    query +="}"
    puts query
    store = SPARQL::Client.new(endpoint)
    result = store.query(query)
    if result.size == 0
      return nil
    end
    puts query
    
    params = Hash.new
    result.each do |prop|
      propUri = prop.prop.to_s.force_encoding("UTF-8")
      propUri.encode("UTF-8")
      propValue = prop.value.to_s.force_encoding("UTF-8")
      propValue.encode("UTF-8")
      if params[propUri] == nil
          params[propUri] = Hash.new
      end
      if (!prop.bound?(:extraProp))
        if params[propUri][:value] == nil
          params[propUri][:value] = Array.new
        end
        params[propUri][:value] << propValue
      else
        if params[propUri][propValue] == nil
          params[propUri][propValue] = Hash.new
        end
        params[propUri][propValue][prop.extraProp.to_s] = prop.extraValue.to_s
      end
    end
    puts params.to_json
    
    item = Item.new(uri, params["http://purl.org/rss/1.0/title"][:value][0])
    item.date = params["http://purl.org/rss/1.0/pubDate"][:value] if params["http://purl.org/rss/1.0/pubDate"] != nil
    item.description = params["http://purl.org/rss/1.0/description"][:value] if params["http://purl.org/rss/1.0/description"] != nil
    item.props = params
    item.filters = Array.new

    if params["http://zone-project.org/model/items#favorite"] != nil && user != nil && params["http://zone-project.org/model/items#favorite"][:value] != nil
      params["http://zone-project.org/model/items#favorite"][:value].each do |fav|
        if fav == "#{ZoneOntology::ZONE_USER}#{user.id}"
          item.favorite = true
        end
      end
    end

    #add the filters
    item.props.each do |key|
      if (key[0] == "http://zone-project.org/model/items#favorite") || (key[0].start_with? "http://purl.org/rss") || (key[0].start_with? "http://zone-project.org/model/plugins/ExtractArticlesContent")
        next
      end

      key[1].each do | filterKey, filterVal|
        if filterKey.to_s == "value"
          filterVal.each do |curVal|
            if curVal == "true"
              next
            end
            filter = SearchFilter.new(:value =>  curVal)
            if curVal.start_with?("http")
              filter.uri = curVal
              filter.type = "http://www.w3.org/2004/02/skos/core#Concept"
            end
            filter.prop = key[0]
            filter.item = item
            item.filters << filter
          end
        else
          filter = SearchFilter.new(:value =>  filterVal[ZoneOntology::RDF_LABEL])
          filter.uri = filterKey
          filter.prop = key[0]
          filter.item = item
          filter.type = filterVal[ZoneOntology::RDF_TYPE] if filterVal[ZoneOntology::RDF_TYPE] != nil
          filter.abstract = filterVal[ZoneOntology::DBPEDIA_ABSTRACT] if filterVal[ZoneOntology::DBPEDIA_ABSTRACT] != nil
          filter.thumb = filterVal[ZoneOntology::DBPEDIA_THUMB] if filterVal[ZoneOntology::DBPEDIA_THUMB] != nil

          item.filters << filter

        end
      end
    end

    item.filters.sort! { |a,b|
      if a.prop == "http://zone-project.org/model/plugins#lang"
        -1
      elsif b.prop == "http://zone-project.org/model/plugins#lang"
        1
      elsif a.type == nil
        -1
      elsif b.type == nil
        1
      else
        a.type.downcase <=> b.type.downcase
      end
    }

    return item
  end
  
  def initialize(uri,title, similarity=0)
    @uri = uri
    @title = title
    @filters = Hash.new
    @similarity = similarity
  end
  
  def to_param
    require 'cgi'
    CGI.escape(@uri.to_s)
  end

  def getUriHash
    return Digest::SHA1.hexdigest(self.uri)
  end

  def getTypePicture
    if uri.starts_with?('https://twitter.com') || uri.starts_with?('http://twitter.com')
      return "/assets/twitter.png"
    else
      return "/assets/foregroundRSS.png"
    end
  end

  def getTags
    result = Array.new

    self.props.each do |prop|
      prop[1].each do |value|
        filter = SearchFilter.new(:value =>  value)
        if value.start_with?("http")
          filter.uri = value
        end
        filter.prop = prop[0]

        if(filter.prop.starts_with? WIKI_META_URI)
          if(filter.uri == nil)
            next
          end
          result << filter
        elsif(filter.prop.starts_with? INSEE_GEO_URI)
          result << filter
        elsif (filter.prop.starts_with? TWITTER_MENTIONED_PLUGIN_URI)
          filter.value = "@#{filter.value}"
          result << filter
        elsif (filter.prop.starts_with? TWITTER_HASHTAG_PLUGIN_URI)
          filter.value = "##{filter.value}"
          result << filter
        end
      end
    end
    return result
  end

  def addTag(tag)
    graph = RDF::URI.new(ZoneOntology::GRAPH_ITEMS)
    subject = RDF::URI.new(@uri)
    if tag.start_with? "http"
      tagNode = RDF::URI.new(tag)
    else
      tagNode = tag
    end
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::PLUGIN_SOCIAL_ANNOTATION),tagNode]).graph(graph))
  end

  def deleteTag(tag)
    graph = RDF::URI.new(ZoneOntology::GRAPH_ITEMS)
    subject = RDF::URI.new(@uri)
    self.props.each do |prop|
      prop[1].each do |value|
        if value == tag
          if tag.start_with? "http"
            tagNode = RDF::URI.new(tag)
          else
            tagNode = tag
          end
          $repo.delete(RDF::Virtuoso::Query.delete_data([subject, RDF::URI.new(prop[0]), tagNode]).graph(graph))
        end
      end
    end
  end

  def getLang()
    lang = self.props[LANG_URI]
    if lang != nil
      lang =  lang[0]
      if lang == "en"
        return "gb"
      end
    end
    return lang

  end
end