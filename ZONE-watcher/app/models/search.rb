class Search < ActiveRecord::Base
  attr_accessible :name
  belongs_to :user
  has_many :sources, class_name: "SearchSource"
  has_many :filters, class_name: "SearchFilter"

  def self.build_from_form(params)
    result = Search.new

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
          result.sources << SearchSource.build_from_form(CGI.unescape(value),kind)
        end
      end
    end

    if inputFilters != nil
      inputFilters.each do |kind,vals|
        vals.each do |value|
          result.filters << SearchFilter.build_from_form(CGI.unescape(value),kind)
        end
      end
    end
    return result
  end

  def getItemsNumber
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT COUNT(DISTINCT ?concept) as ?number
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    FROM <#{ZoneOntology::GRAPH_SOURCES}> WHERE {\n"
    query += self.generateSPARQLRequest
    query += "?concept RSS:title ?title.} LIMIT 1"
    store = SPARQL::Client.new($endpoint)
    if store.query(query).length == 0
      return 0
    else
      return store.query(query)[0]["number"]
    end
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

  def generateSPARQLRequest
    extendQuery = ""
    self.sources.each do |source|
      extendQuery += "{ #{source.getSparqlTriple}.} \nUNION "
    end
    if self.sources.length > 0
      extendQuery = extendQuery[0..-8]
    end

    self.getAndFilters.each do |filter|
      extendQuery += "#{filter.getSparqlTriple}. \n"
    end

    orFilters = self.getOrFilters
    orFilters.each do |filter|
      extendQuery += "{ #{filter.getSparqlTriple}.} \nUNION "
    end
    if orFilters.length > 0
      extendQuery = extendQuery[0..-9]+".\n"
    end

    self.getWithoutFilters.each do |filter|
      extendQuery += "FILTER NOT EXISTS { #{filter.getSparqlTriple}.}. \n"
    end

    extendQuery
  end
end
