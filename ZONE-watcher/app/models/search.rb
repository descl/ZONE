class Search < ActiveRecord::Base
  attr_accessible :name
  belongs_to :user
  has_many :sources, class_name: "SearchSource"
  has_many :filters, class_name: "SearchFilter"

  def build_from_form(params,user)
    self.user_id = user

    if params[:sources].class == String
      inputSources = JSON.parse params[:sources]
    else
      inputSources = params[:sources]
    end
    if params[:filters].class == String
      inputFilters = JSON.parse params[:filters]
    else
      inputFilters = params[:filters]
    end

    if inputSources != nil
      inputSources.each do |kind,vals|
        vals.each do |value|
          source = SearchSource.build_from_form(CGI.unescape(value),kind,user)
          if source != nil
            self.sources << source
          end
        end
      end
    end

    if inputFilters != nil
      inputFilters.each do |kind,vals|
        vals.each do |filter|
          self.filters << SearchFilter.build_from_form(filter,kind)
        end
      end
    end
  end

  def getItemsNumber(user,sinceWhen=0)
    endpoint = Rails.application.config.virtuosoEndpoint
    sparqlTriples = self.generateSPARQLRequest(user)
    if sparqlTriples == nil
      return 0
    end
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT COUNT(DISTINCT ?concept) as ?number
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}> WHERE {\n"
    query +="?concept RSS:title ?title."
    query += sparqlTriples
    query += "?concept RSS:pubDateTime ?pubDateTime."
    query += "FILTER(xsd:integer(?pubDateTime) > #{sinceWhen})"
    query += "} "
    store = SPARQL::Client.new(endpoint)
    if store.query(query).length == 0
      return 0
    else
      return store.query(query)[0]["number"].value.to_i
    end
  end


  def generateSPARQLRequest(user)
    extendQuery = ""
    self.sources.each do |source|
      sparqlTriple = source.getSparqlTriple(user)
        if sparqlTriple== nil
          return nil
        end
        extendQuery += "{ #{sparqlTriple}.} \nUNION "
    end
    if self.sources.length > 0
      extendQuery = extendQuery[0..-8]
    end

    self.getAndFilters.each do |filter|
      extendQuery += "#{filter.getSparqlTriple}. \n"
    end

    orFilters = self.getOrFilters
    orFilters.each do |filter|
      extendQuery += "OPTIONAL { #{filter.getSparqlTriple}.} \n"
    end

    self.getWithoutFilters.each do |filter|
      extendQuery += "FILTER NOT EXISTS { #{filter.getSparqlTriple}.}. \n"
    end
    extendQuery
  end

  def getName(default)
    if (self.name == "" || self.name == nil)
      return "#{default} #{self.id}"
    else
      return self.name
    end
  end

  def getTagsCloud(user)
    endpoint = Rails.application.config.virtuosoEndpoint
    sparqlTriples = self.generateSPARQLRequest(user)
    if sparqlTriples == nil
      return 0
    end
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT ?tagEntity, ?tagName, COUNT(?tagEntity) AS ?popularity
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_TAGS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}> WHERE {\n"

    query += "GRAPH <#{ZoneOntology::GRAPH_ITEMS}> {"
    query +="?concept RSS:title ?title."
    query += sparqlTriples
    query += "?concept <http://zone-project.org/model/plugins/Spotlight#entities> ?tagEntity."

    query +="}
    GRAPH <http://zone-project.org/datas/tags> {
        ?tagEntity rdfs:label ?tagName.
    }"

    #query += "?concept RSS:pubDateTime ?pubDateTime."
    #query += "FILTER(xsd:integer(?pubDateTime) > #{sinceWhen})"
    query += "} ORDER BY DESC(?popularity) LIMIT 100"
    puts query
    store = SPARQL::Client.new(endpoint)

    items = Array.new
    store.query(query).each do |item|
      tagObj = {:text => item.tagName.to_s, :uri => item.tagEntity.to_s, :weight => item.popularity.to_i}
      items << tagObj
    end
    return {:result => items, :query => query}
  end


  def getOrFilters
    return self.filters.find_all{|f| f.kind == "or" }
  end
  def getAndFilters
    return self.filters.find_all{|f| f.kind == "and" }
  end
  def getWithoutFilters
    return self.filters.find_all{|f| f.kind == "without" }
  end
end
