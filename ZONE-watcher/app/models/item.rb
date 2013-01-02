class Item# < ActiveRecord::Base
  extend ActiveModel::Naming
  include ActiveModel::AttributeMethods
  require 'rubygems'
  require 'rest_client'
  $endpoint = 'http://localhost:8890/sparql/'


  attr_accessor :uri, :title, :props, :description, :date, :localURI

  def self.all(param = "")
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?concept ?title 
    FROM <http://demo.zone-project.org/data>
    WHERE {
      ?concept
        RSS:title ?title;
        RSS:pubDateTime ?pubDateTime.
      #{param}
      
    }ORDER BY DESC(?pubDateTime) LIMIT 50"

    store = SPARQL::Client.new($endpoint)
    items = Array.new
    store.query(query).each do |item|
      items << Item.new(item.concept.to_s, item.title)
    end
    return items
  end
  
  def self.find(param)
    require 'cgi'
    require 'uri'
    
    uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <http://demo.zone-project.org/data>
    WHERE { <#{uri}> ?prop ?value.}"
    puts query
    store = SPARQL::Client.new($endpoint)
    result = store.query(query)
    
    params = Hash.new
    result.each do |prop|
      params[prop.prop.to_s] = prop.value.to_s
    end
    
    item = Item.new(uri, params["http://purl.org/rss/1.0/title"])
    item.props = params
    return item
  end
  
  def initialize(uri,title)
      if ! uri.start_with? 'http://'
        uri = 'http://'+uri
      end
    @uri = uri
    @title = title
    @filters = Hash.new
  end
  
  def to_param
    require 'cgi'
    CGI.escape(@uri.to_s.sub("http://",""))
  end
end