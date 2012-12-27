module ApplicationHelper
  def parseFilterParams(params)
    filter = Array.new
    if(params.length > 2)
      if params[:old] != nil
        params[:old].each do |cur|
          filter.push cur
        end
      end
    end
      
    if params[:new] != nil
      filter.push params[:new]
    end
    
    filter
  end
  
  def generateFilterSPARQLRequest(filter)
    extendQuery = ""
      
    filter.each do |cur|
      if cur["value"].match(/^http\:\/\//)
        #URI
        extendQuery += "?concept <"+cur["type"]+"> <"+cur["value"]+">. \n"
      else
        extendQuery += "?concept <"+cur["type"]+"> '"+cur["value"]+"'. \n"
      end
    end
    extendQuery
  end
end
