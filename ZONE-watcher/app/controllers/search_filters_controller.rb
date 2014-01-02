class SearchFiltersController < ApplicationController
  caches_action :index, :cache_path => Proc.new { |c| c.params }, :expires_in => 1.day

  def index
    uri = URI.unescape(params[:uri])
    @search_filter = SearchFilter.new(:uri => uri)
    @infos = @search_filter.getInfos

    if @infos == nil
      render nothing: true
    else
      @linkedFilters = Array.new
      @infos[:linkedEntities].take(4).each do |entity|
        fi = SearchFilter.new(:uri => entity[:uri], :value => entity[:value])
        fi.prop = ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES
        fi.type = entity[:kind]
        fi.item = nil
        @linkedFilters << fi
      end
      render :layout => 'empty'
    end
  end
end