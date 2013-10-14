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

  attr_accessor :uri, :title, :props, :description, :date, :localURI, :similarity, :favorite, :filters

  endpoint = Rails.application.config.virtuosoEndpoint
  update_uri = Rails.application.config.virtuosoEndpoint+"-auth"
  $repo       = RDF::Virtuoso::Repository.new(endpoint,
                                              :update_uri => update_uri,
                                              :username => Rails.application.config.virtuosoLogin,
                                              :password => Rails.application.config.virtuosoPassword,
                                              :auth_method => 'digest')

  def self.all(search = "",start=0,per_page=10)
    sparqlFilter = search.generateSPARQLRequest
    filtersIds = search.getOrFilters.map {|elem| "?filter#{elem.id}"}.join(',')

    endpoint = Rails.application.config.virtuosoEndpoint
    #if start > (10000 - per_page)
    #  return Array.new
    #end
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT(?concept),?pubDateTime,  ?title, CONCAT( #{filtersIds} ) AS ?filtersVals
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept RSS:title ?title.
      OPTIONAL { ?concept RSS:pubDateTime ?pubDateTime}.
      #{sparqlFilter}

    }ORDER BY DESC(?pubDateTime) LIMIT #{per_page} OFFSET #{start}"

    store = SPARQL::Client.new(endpoint)
    items = Array.new
    store.query(query).each do |item|
      similarity = item.filtersVals.to_s.scan(/http/).count
      items << Item.new(item.concept.to_s, item.title, similarity)
    end
    return {:result => items, :query => query}
  end
  
  def self.find(param,user)
    endpoint = Rails.application.config.virtuosoEndpoint
    require 'cgi'
    require 'uri'
    uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    uri = uri.gsub("%23","#")
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE { <#{uri}> ?prop ?value.";
    query +="}"
    store = SPARQL::Client.new(endpoint)
    result = store.query(query)
    if result.size == 0
      return nil
    end
    puts query
    
    params = Hash.new
    result.each do |prop|
      if params[prop.prop.to_s] == nil
          params[prop.prop.to_s] = Array.new
      end
      params[prop.prop.to_s] << prop.value.to_s
    end
    
    item = Item.new(uri, params["http://purl.org/rss/1.0/title"])
    item.date = params["http://purl.org/rss/1.0/pubDate"] if params["http://purl.org/rss/1.0/pubDate"] != nil
    item.description = params["http://purl.org/rss/1.0/description"] if params["http://purl.org/rss/1.0/description"] != nil
    item.props = params
    item.filters = Array.new

    if params["http://zone-project.org/model/items#favorite"] != nil && user != nil
      params["http://zone-project.org/model/items#favorite"].each do |fav|
        if fav == "#{ZoneOntology::ZONE_USER}#{user.id}"
          item.favorite = true
        end
      end
    end

    item.props.each do |key|
      key[1].each do |value|
        filter = SearchFilter.new(:value =>  value)
        if value.start_with?("http")
          filter.uri = value
        end
        filter.prop = key[0]
        filter.item = item

        item.filters << filter
      end
    end

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
end