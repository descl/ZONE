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
    
    filters
  end
  
  def generateFilterSPARQLRequest(filter)
    extendQuery = ""
      
    filter.each do |cur|
      if cur.value.match(/^http\:\/\//)
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
    request = generateFilterSPARQLRequest(filters)
    query = "SELECT ?number COUNT(DISTINCT ?concept)  WHERE {\n"
    query += request
    query += "?concept <http://purl.org/rss/1.0/title> ?title.} LIMIT 1"
    store = SPARQL::Client.new($endpoint)
    if store.query(query).length == 0
      return '0'
    else
      return store.query(query)[0]["callret-1"]
    end
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
