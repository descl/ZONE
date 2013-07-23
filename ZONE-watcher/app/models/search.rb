class Search < ActiveRecord::Base
  attr_accessible :name
  belongs_to :user
  has_many :sources, class_name: "SearchSource"
  has_many :filters, class_name: "SearchFilter"

  def self.build_from_form(params)
    result = Search.new
    sourcesJson = JSON.parse params[:sources]
    sourcesJson.each do |kind,vals|
      vals.each do |value|
        result.sources << SearchSource.build_from_form(CGI.unescape(value),kind)
      end
    end

    filtersJson = JSON.parse params[:filters]
    filtersJson.each do |kind,vals|
      vals.each do |value|
        result.filters << SearchFilter.build_from_form(CGI.unescape(value),kind)
      end
    end

    return result
  end

  def getItemsNumber
    query = "SELECT ?number COUNT(DISTINCT ?concept) FROM <#{ZoneOntology::GRAPH_ITEMS}> WHERE {\n"
    query += self.generateSPARQLRequest
    query += "?concept <http://purl.org/rss/1.0/title> ?title.} LIMIT 1"
    store = SPARQL::Client.new($endpoint)
    if store.query(query).length == 0
      return 0
    else
      return store.query(query)[0]["callret-1"]
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
