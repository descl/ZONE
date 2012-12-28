class FiltersController < ApplicationController
  include ApplicationHelper
  # GET /filters
  # GET /filters.json
  def index
    @filters = Filter.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @filters }
    end
  end

  # GET /filters/1
  # GET /filters/1.json
  def show
    @filter = Filter.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @filter }
    end
  end

  # GET /filters/new
  # GET /filters/new.json

  def getNumber
    @filters = parseFilterParams(params)
    request = generateFilterSPARQLRequest(@filters)
    @query = "SELECT ?number COUNT(DISTINCT ?concept)  WHERE {\n"
    @query += request
    @query += "?concept <http://purl.org/rss/1.0/title> ?title.} LIMIT 1"
    store = SPARQL::Client.new($endpoint)
    if store.query(@query).length == 0
      @number = '0'
    else
      @number = store.query(@query)[0]["callret-1"]
    end
    respond_to do |format|
      format.html {render :inline => "<%= @number %>"}
    end
    
  end
  
end
