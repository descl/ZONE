class SearchFiltersController < ApplicationController
  # GET /searches
  # GET /searches.json

  # GET /searches/1
  # GET /searches/1.json
  def index
    @infos = @search_filter.getInfos

    render  :layout => 'empty'
  end
end