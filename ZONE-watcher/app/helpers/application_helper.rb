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
end
