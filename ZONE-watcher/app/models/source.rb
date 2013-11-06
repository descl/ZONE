class Source
  extend ActiveModel::Naming
  include ActiveModel::Conversion
  include ActiveModel::Validations
  include ActiveModel::AttributeMethods

  require 'rubygems'
  require 'rest_client'

  attr_accessor :id, :uri, :label, :lang, :licence, :owner, :thumb, :theme, :attrs, :persisted
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
    SELECT DISTINCT ?concept ?prop ?value
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE}>.
      ?concept ?prop ?value.
      #{param}
    }ORDER BY ?concept"#"

    store = SPARQL::Client.new(endpoint)
    sources = Array.new
    request = store.query(query)
    if request.size == 0
      return []
    end
    curSource = Source.new(request[0].concept.to_s)
    request.each do |elem|
      if curSource.uri != elem.concept.to_s
        sources << curSource
        curSource = Source.new(elem.concept.to_s,:persisted => true)
      end

      propName = elem.prop.to_s.rpartition("#").last

      if curSource.respond_to? propName
        curSource.send("#{propName}=",elem.value.to_s)
      else
        if curSource.attrs == nil
          curSource.attrs= Array.new
        end
        curSource.attrs << {elem.prop.to_s => elem.value.to_s}
      end
    end
    sources << curSource
    return sources
  end
  
  def self.find(param)
    endpoint = Rails.application.config.virtuosoEndpoint
    require 'cgi'
    require 'uri'

    #uri = CGI.unescape(URI.escape(CGI.unescape(param)))
    uri = param
    id = uri
    if (!uri.start_with? "http://") && (!uri.start_with? "https://")
      uri.insert 6, "/"
    end
    
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE { <#{uri}> ?prop ?value.}"
    store = SPARQL::Client.new(endpoint)
    request = store.query(query)

    if request.length == 0
      return nil
    end

    source = Source.new(uri,:persisted => true)
    request.each do |prop|
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
    return false if @uri == ""
    return true if @persisted == true
    puts self.to_json
    return Source.find(@uri.to_s) != nil
  end

  def addOwner(user)
    graph = RDF::URI.new(ZoneOntology::GRAPH_SOURCES)
    subject = RDF::URI.new(@uri)
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_OWNER),user.to_s]).graph(graph))
  end
  
  def save


    graph = RDF::URI.new(ZoneOntology::GRAPH_SOURCES)
    subject = RDF::URI.new(@uri)

    res = $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF.type,RDF::URI.new(ZoneOntology::SOURCES_TYPE)]).graph(graph))
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new("http://www.w3.org/2000/01/rdf-schema#label"),@label]).graph(graph)) if (@label != nil && @label != "")
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_LANG),@lang]).graph(graph)) if (@lang != nil && @lang != "")
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_THUMB),@thumb]).graph(graph)) if (@thumb != nil && @thumb != "")
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_THEME),@theme]).graph(graph)) if (@theme != nil && @theme != "")
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new(ZoneOntology::SOURCES_OWNER),@owner.to_s]).graph(graph)) if (@owner != nil && @owner != "")
    $repo.insert(RDF::Virtuoso::Query.insert_data([subject,RDF::URI.new("http://purl.org/rss/1.0/pubDateTime"),Time.now.to_i.to_s]).graph(graph))
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
    endpoint = Rails.application.config.virtuosoEndpoint
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