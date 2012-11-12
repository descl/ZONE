require '4store-ruby'
class RssfeedController < ApplicationController
  def getOne
    @itemURI = params[:element]
    render  :layout => 'empty'
    #respond_to do |format|
    #  format.html # show.html.erb
    #  format.json { render json: @item }
    #end
  end
  
  def index
    @filter = Array.new
    extendQuery = ""
    puts params
    if(params.length > 2)
      puts 'ya des choses'
      #item = {"type"=> params[:type], "value"=>params[:value]}
      puts params[:old]
      if params[:old] != nil
        params[:old].each do |cur|
          @filter.push cur
        end
      end
      
      if params[:new] != nil
        @filter.push params[:new]
      end
      
      @filter.each do |cur|
        if cur["value"].match(/^http\:\/\//)
          #URI
          extendQuery += "?concept <"+cur["type"]+"> <"+cur["value"]+">. \n"
        else
          extendQuery += "?concept <"+cur["type"]+"> '"+cur["value"]+"'. \n"
        end
      end
    end
    
    @query = "SELECT ?concept ?relation ?result ?pubDateTime WHERE {\n"
    @query += extendQuery
    @query += "?concept <http://purl.org/rss/1.0/title> ?title.
?concept <http://purl.org/rss/1.0/pubDateTime> ?pubDateTime.
?concept ?relation ?result.
} ORDER BY DESC(?pubDateTime) LIMIT 500"

    endpoint = 'http://localhost:8080/sparql/'
    puts @query
    store = FourStore::Store.new endpoint
    @elements = store.select(@query)

# @result = Array.new
# if @elements.length > 0
# item = {"concept" => @elements[0]["concept"]}
 # @result.push item
 # end

# @elements.each() do |element|
# if @result[@result.length-1]["concept"] != element["concept"]
#
# item = {"concept" => element["concept"]}
# @result.push item
# end
# @result[@result.length-1][element["relation"]] = element["result"]
# end
    
# @result.delete_if{|x| x["http://purl.org/rss/1.0/pubDateTime"] == nil}
# @result.delete_if{|x| x["http://purl.org/rss/1.0/description"] == nil}
    
    @result = {}
    
    @elements.each() do |element|

      if @result[element["concept"]] == nil
        @result[element["concept"]] = Array.new
      end
      @result[element["concept"]].push([element["relation"],element["result"]])
      
      #if @result[@result.length-1]["concept"] != element["concept"]
        
      # item = {"concept" => element["concept"]}
      # @result.push item
      #end
      # @result[@result.length-1][element["relation"]] = element["result"]
    end
    
    puts @result
    
    #@result.each.delete_if{|x| x["http://purl.org/rss/1.0/link"] == nil}
    #@result.delete_if{|x| x["http://purl.org/rss/1.0/description"] == nil}
    
  end
end
