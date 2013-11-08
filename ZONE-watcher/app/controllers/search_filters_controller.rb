class SearchFiltersController < ApplicationController
  # GET /searches
  # GET /searches.json

  # GET /searches/1
  # GET /searches/1.json
  def index
    uri = URI.unescape(params[:uri])
    @search_filter = SearchFilter.new(:uri => uri)
    @infos = @search_filter.getInfos

    @linkedFilters = Array.new
    @infos[:linkedEntities].take(4).each do |entity|
      fi = SearchFilter.new(:uri => entity[:uri], :value => entity[:value])
      fi.prop = ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES
      fi.type = entity[:kind]
      fi.item = nil
      @linkedFilters << fi
    end
    render  :layout => 'empty'
  end
end