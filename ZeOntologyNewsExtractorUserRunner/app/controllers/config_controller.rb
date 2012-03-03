class ConfigController < ApplicationController
  def index
    items = Item.all
    
    @allGeo = Array.new
    items.each do |item|
      if item["value"].match(/^http:\/\/rdf\.insee\.fr/)
        @allGeo.push item
      end
    end
    
  end
end