class Favorite
  extend ActiveModel::Naming
  include ActiveModel::AttributeMethods
  require 'rubygems'
  require 'rest_client'

  attr_accessor :uri, :user
  endpoint = Rails.application.config.virtuosoEndpoint

  update_uri = Rails.application.config.virtuosoEndpoint+"-auth"
  $repo       = RDF::Virtuoso::Repository.new(endpoint,
                                              :update_uri => update_uri,
                                              :username => Rails.application.config.virtuosoLogin,
                                              :password => Rails.application.config.virtuosoPassword,
                                              :auth_method => 'digest')
  def self.all(user,start=0,per_page=10)
    endpoint = Rails.application.config.virtuosoEndpoint
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT *
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE {
      ?concept RSS:title ?title.
      OPTIONAL { ?concept RSS:pubDateTime ?pubDateTime}.
      ?concept <#{ZoneOntology::ZONE_FAVORITE}> <#{ZoneOntology::ZONE_USER}#{user}>

    }ORDER BY DESC(?pubDateTime) LIMIT #{per_page} OFFSET #{start}"

    store = SPARQL::Client.new(endpoint)
    items = Array.new
    store.query(query).each do |item|
      items << Item.new(item.concept.to_s, item.title)
    end
    return {:result => items, :query => query}
  end

  def initialize(uri, user)
    @uri = uri
    @user = user
  end

  def userUri
    return "#{ZoneOntology::ZONE_USER}#{@user}"
  end
  def save
    graph = RDF::URI.new(ZoneOntology::GRAPH_ITEMS)
    subject = RDF::URI.new(@uri)
    userUri = RDF::URI.new(self.userUri)

    res = $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::ZONE_FAVORITE),userUri]).graph(graph))
  end

  def destroy
    graph = RDF::URI.new(ZoneOntology::GRAPH_ITEMS)
    subject = RDF::URI.new(@uri)
    userUri = RDF::URI.new(self.userUri)
    $repo.delete(RDF::Virtuoso::Query.delete_data([subject,RDF::URI.new(ZoneOntology::ZONE_FAVORITE),userUri]).graph(graph))
  end

end
