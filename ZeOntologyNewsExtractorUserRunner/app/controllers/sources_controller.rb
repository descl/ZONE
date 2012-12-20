class SourcesController < ApplicationController
  include ApplicationHelper
  $endpoint = 'http://localhost:8890/sparql/'
  # GET /sources
  # GET /sources.json
  def index
    
    @query = "SELECT DISTINCT(?source) WHERE { _:a <http://purl.org/rss/1.0/source> ?source.} ORDER BY ?source"

    store = SPARQL::Client.new($endpoint)
    result = store.query(@query)
    
    @sources = Array.new
    filters = Hash.new
    result.each_solution do |element|
      @sources.push element.source.to_s
    end
    
    @sources = @sources.uniq
      
      
    @sources.each do |element|
      filters[element] = rssfeed_getNumberForParams_path(:old => [{:type => "http://purl.org/rss/1.0/source", :value => element}])
    end
    gon.filters = filters
      
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @sources }
    end
  end
end
