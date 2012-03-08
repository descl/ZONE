class HomeController < ApplicationController
  def index
    load 'lib/store.rb'
    query = "SELECT DISTINCT (COUNT(?concept) as ?totalSize) WHERE {\n"
    query += "?concept <http://purl.org/rss/1.0/title> ?title.
    }"
    endpoint = 'http://zouig.org:8081/sparql/'
    puts query
    store = FourStore::Store.new endpoint
    puts store.select(query)
    @size = store.select(query)
  end
end
