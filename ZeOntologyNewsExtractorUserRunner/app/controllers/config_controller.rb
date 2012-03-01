class ConfigController < ApplicationController
  def index
    items = Item.all
    
    @allGeo = Array.new
    items.each do |item|
      if item["value"].match(/^http:\/\/rdf\.insee\.fr/)
        it =item
        it["valueInfo"] = get4StoreNameElem(it["value"])
        allGeo.push it
      end
    end
    
  end
end
