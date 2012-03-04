class RssfeedController < ApplicationController
  def index
    load 'lib/store.rb'
    query = "SELECT ?concept ?relation ?result WHERE {\n"
    query += "?concept ?relation ?result} ORDER BY ?concept ?relation"

    endpoint = 'http://zouig.org:8081/sparql/'

    store = FourStore::Store.new endpoint
    @elements = store.select(query)
    puts @elements


    @result = Array.new
    if @elements.length > 0
      item = {"concept" => @elements[0]["concept"]}
      @result.push item
    end

    @elements.each() do |element|
      if @result[@result.length-1]["concept"] != element["concept"]
        #we sort the last tab
        puts @result[@result.length-1]
        
        item = {"concept" => element["concept"]}
        @result.push item
      end
        @result[@result.length-1][element["relation"]] = element["result"]
    end
    @result.sort! { |a,b| b["http://purl.org/rss/1.0/pubDate"] <=> a["http://purl.org/rss/1.0/pubDate"] }
  end
end
