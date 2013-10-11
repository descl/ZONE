class SearchFiltersController < ApplicationController
  # GET /searches
  # GET /searches.json

  # GET /searches/1
  # GET /searches/1.json
  def index
    uri = URI.unescape(params[:uri])
    @search_filter = SearchFilter.new(:uri => uri)
    @infos = @search_filter.getInfos

    render  :layout => 'empty'
  end
end