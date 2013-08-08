class Source
  extend ActiveModel::Naming
  include ActiveModel::Conversion
  include ActiveModel::Validations
  include ActiveModel::AttributeMethods

  require 'rubygems'
  require 'rest_client'

  attr_accessor :id, :uri, :label, :lang, :licence, :owner, :thumb, :theme, :attrs
  attr_reader   :errors

  validates_format_of :uri, :with => URI::regexp(%w(http https))

  endpoint = Rails.application.config.virtuosoEndpoint

  update_uri = Rails.application.config.virtuosoEndpoint+"-auth"
  $repo       = RDF::Virtuoso::Repository.new(endpoint,
                                              :update_uri => update_uri,
                                              :username => Rails.application.config.virtuosoLogin,
                                              :password => Rails.application.config.virtuosoPassword,
                                              :auth_method => 'digest')

  def self.all(param = "")
    endpoint = Rails.application.config.virtuosoEndpoint

    query = "PREFIX SOURCE: <#{ZoneOntology::SOURCES_PREFIX}>
    SELECT DISTINCT ?concept
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE}>.
      #{param}
    }ORDER BY ?concept"#"

    store = SPARQL::Client.new(endpoint)
    sources = Array.new
    store.query(query).each do |source|
      sources << Source.find(source.concept.to_s)
    end
    return sources
  end
  
  def self.find(param)
    endpoint = Rails.application.config.virtuosoEndpoint
    require 'cgi'
    require 'uri'

    #uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    uri = param
    id = uri
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE { <#{uri}> ?prop ?value.}"
    store = SPARQL::Client.new(endpoint)
    result = store.query(query)

    source = Source.new(uri)
    result.each do |prop|
      propName = prop.prop.to_s.rpartition("#").last

      if source.respond_to? propName
        source.send("#{propName}=",prop.value.to_s)
      else
        if source.attrs == nil
          source.attrs= Array.new
        end
        source.attrs << {prop.prop.to_s => prop.value.to_s}
      end
    end
    if result.length == 0
      return nil
    end
    return source
  end

  def initialize(uri="",attributes = {})  
    @uri = uri
    @label = ""
    @lang = ""
    @licence = ""
    @owner = ""
    @thumb = ""
    @theme = ""
    @id = uri
    attributes.each do |name, value|  
      send("#{name}=", value)  
    end
    @errors = ActiveModel::Errors.new(self)
  end
  
  def to_param
    require 'cgi'
    CGI.escape(@uri.to_s)
  end
  
  def persisted?
    return false if @uri == nil
    return Source.find(@uri.to_s) != nil
  end 
  
  def save


    graph = RDF::URI.new(ZoneOntology::GRAPH_SOURCES)
    subject = RDF::URI.new(@uri)

    res = $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF.type,RDF::URI.new(ZoneOntology::SOURCES_TYPE)]).graph(graph))
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new("http://www.w3.org/2000/01/rdf-schema#label"),@label]).graph(graph)) if @label != nil
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_LANG),@lang]).graph(graph)) if @lang != nil
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_THUMB),@thumb]).graph(graph)) if @thumb != nil
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_THEME),@theme]).graph(graph)) if @theme != nil
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_OWNER),@owner.to_s]).graph(graph)) if @owner != nil
    if attrs != nil
      attrs.each do |attr,valStr|
        if valStr.start_with? 'http'
          val = RDF::URI.new(valStr)
        else
          val = valStr
        end
        $repo.insert(RDF::Virtuoso::Query.insert_data([RDF::URI.new(@uri),RDF::URI.new(attr),val]).graph(graph))
      end
    end
    
    return res
  end

  def destroy
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE { <#{@uri}> ?prop ?value.}"
    store = SPARQL::Client.new(endpoint)
    result = store.query(query)
    graph = RDF::URI.new(ZoneOntology::GRAPH_SOURCES)
    subject = RDF::URI.new(@uri)
    result.each do |prop|
      $repo.delete(RDF::Virtuoso::Query.delete_data([subject,prop.prop,prop.value]).graph(graph))
    end
    return true
  end
end