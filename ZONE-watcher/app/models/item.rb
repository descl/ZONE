class Item# < ActiveRecord::Base
  extend ActiveModel::Naming
  include ActiveModel::AttributeMethods
  require 'rubygems'
  require 'rest_client'
  $endpoint = 'http://localhost:8890/sparql/'


  attr_accessor :uri, :title

  def self.all
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?news ?title 
    FROM <http://demo.zone-project.org/data>
    WHERE {
      ?news
        RSS:title ?title;
        RSS:pubDateTime ?pubDateTime.
      
    }ORDER BY DESC(?pubDateTime) LIMIT 10"

    store = SPARQL::Client.new($endpoint)
    items = Array.new
    store.query(query).each do |item|
      items << Item.new(item.news.to_s, item.title)
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
    params = Hash.new
    store.query(query).each do |prop|
      params[prop.prop.to_s] = prop.value.to_s
    end
    return params
  end
  
  def initialize(uri,title)
    @uri = uri
    @title = title
  end
  
  def to_param
    require 'cgi'
    CGI.escape(@uri.to_s)
  end
end