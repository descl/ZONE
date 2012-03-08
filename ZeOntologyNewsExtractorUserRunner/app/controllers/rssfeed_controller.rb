class RssfeedController < ApplicationController
  def getOne
    @itemURI = params[:element]

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @item }
    end
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
    
    load 'lib/store.rb'
    query = "SELECT ?concept ?relation ?result ?pubDateTime WHERE {\n"
    query += extendQuery
    query += "?concept <http://purl.org/rss/1.0/title> ?title. 
    ?concept ?relation ?result.  } ORDER BY ?concept LIMIT 1000"

    endpoint = 'http://zouig.org:8081/sparql/'
    puts query
    store = FourStore::Store.new endpoint
    @elements = store.select(query)

puts @elements;
    @result = Array.new
    if @elements.length > 0
      item = {"concept" => @elements[0]["concept"]}
      @result.push item
    end

    @elements.each() do |element|
      if @result[@result.length-1]["concept"] != element["concept"]
        
        item = {"concept" => element["concept"]}
        @result.push item
      end
        @result[@result.length-1][element["relation"]] = element["result"]
    end
    #@result.sort! { |a,b| b["http://purl.org/rss/1.0/pubDate"] <=> a["http://purl.org/rss/1.0/pubDate"] }
  end
end
