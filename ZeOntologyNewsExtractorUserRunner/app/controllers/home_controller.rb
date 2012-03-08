class HomeController < ApplicationController
  def index
    load 'lib/store.rb'
    query = "SELECT DISTINCT ?concept WHERE {\n"
    query += "?concept <http://purl.org/rss/1.0/title> ?title.
    }"
    endpoint = 'http://zouig.org:8081/sparql/'
    puts query
    store = FourStore::Store.new endpoint
    @size = store.select(query).length
  end
end
