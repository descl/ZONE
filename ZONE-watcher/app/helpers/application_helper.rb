module ApplicationHelper
  def parseFilterParams(params)
    filters = Array.new
    if params[:new] != nil
      filters << Filter.new(eval(params[:new]))
    end
    
    if params[:old] != nil
      params[:old].each do |cur|
        filters << Filter.new(eval(cur))
      end
    end
    
    return filters
  end
  
  def generateFilterSPARQLRequest(filter)
    extendQuery = ""
      
    filter.each do |cur|
      if cur.value.match(/^http\:\/\//) || cur.value.match(/^https\:\/\//)
        #URI
        extendQuery += "?concept <"+cur.prop+"> <"+cur.value+">. \n"
      else
        extendQuery += "?concept <"+cur.prop+"> '"+cur.value+"'. \n"
      end
    end
    extendQuery
  end
  
  def calculateNumber(params)
    filters = parseFilterParams(params)
    return calculateNumberFromFilters(filters)
  end

  def calculateNumberFromFilters(filters)
    endpoint = Rails.application.config.virtuosoEndpoint
    request = generateFilterSPARQLRequest(filters)
    query = "SELECT ?number COUNT(DISTINCT ?concept) FROM <#{ZoneOntology::GRAPH_ITEMS}> WHERE {\n"
    query += request
    query += "?concept <http://purl.org/rss/1.0/title> ?title.} LIMIT 1"
    puts query
    store = SPARQL::Client.new(endpoint)
    if store.query(query).length == 0
      return 0
    else
      return store.query(query)[0]["callret-1"]
    end
  end

  def getSourcesFromFilters(filters)
    result = []
    filters.each do |filter|
      if filter.prop == ZoneOntology::RSS_SOURCE
        result << filter
      end
    end
    return result
  end

  #devise spefific helpers
  def resource_name
    :user
  end

  def resource
    @resource ||= User.new
  end

  def devise_mapping
    @devise_mapping ||= Devise.mappings[:user]
  end
end
