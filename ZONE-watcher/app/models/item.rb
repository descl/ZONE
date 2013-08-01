class Item# < ActiveRecord::Base
  extend ActiveModel::Naming
  include ActiveModel::AttributeMethods
  require 'rubygems'
  require 'rest_client'
  $endpoint = Rails.application.config.virtuosoEndpoint


  attr_accessor :uri, :title, :props, :description, :date, :localURI, :similarity

  def self.all(param = "",start=0,per_page=10)
    #if start > (10000 - per_page)
    #  return Array.new
    #end
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT *
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept RSS:title ?title.
      OPTIONAL { ?concept RSS:pubDateTime ?pubDateTime}.
      #{param}

    }ORDER BY DESC(?pubDateTime) LIMIT #{per_page} OFFSET #{start}"

    store = SPARQL::Client.new($endpoint)
    items = Array.new
    store.query(query).each do |item|
      similarity = item.count - 3
      items << Item.new(item.concept.to_s, item.title, similarity)
    end
    return {:result => items, :query => query}
  end
  
  def self.find(param)
    require 'cgi'
    require 'uri'
    
    uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE { <#{uri}> ?prop ?value.}"
    store = SPARQL::Client.new($endpoint)
    result = store.query(query)
    
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
      return "assets/twitter.png"
    else
      return "assets/foregroundRSS.png"
    end
  end
end