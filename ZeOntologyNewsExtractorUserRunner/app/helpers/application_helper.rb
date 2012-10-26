#require '4store-ruby'
require 'net/http'
require 'uri'
require 'json'
#require 'rdf'
#require 'rdf/raptor' # for RDF/XML support
#require 'rdf/ntriples'

module ApplicationHelper
  def get4StoreNameElem(item)
    
    if item.match(/dbpedia/)
      get4StoreNameElemDBpedia(item)
    elsif item.match(/insee/)
      get4StoreNameElemINSEE(item)
    end
  end
  
  
  def get4StoreNameElemDBpedia(item)
    
    item = item.sub 'www.', ''
    
    
    query = "SELECT ?a ?name WHERE {<"+item+"> rdfs:label ?name FILTER(lang(?name)='fr') } "
    params = {:query => query,
             :format => "application/sparql-results+json",
             'default-graph-uri' => "http://dbpedia.org"}
    postData = Net::HTTP.post_form(URI.parse('http://dbpedia.org/sparql'), params)
    begin
      results = JSON.parse(postData.body)["results"]["bindings"]
      itemName = results[0]["name"]["value"]
    rescue
      
    end
    itemName
  end
  
  def get4StoreNameElemINSEE(item)
    endpoint = 'http://zouig.org:8081/sparql/'
    load 'lib/store.rb'
    store = FourStore::Store.new endpoint
    query = "SELECT ?nom WHERE { <"+item+"> <http://rdf.insee.fr/geo/nom> ?nom } LIMIT 1"

    result = store.select(query)
    puts query
    puts result
    if result.length > 0
      store.select(query)[0]["nom"]
    end
  end
end