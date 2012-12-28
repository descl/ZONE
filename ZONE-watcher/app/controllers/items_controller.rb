class ItemsController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  # GET /items
  # GET /items.json
  def index
    
    @filters = parseFilterParams(params)
    
    
    @items = Item.all(generateFilterSPARQLRequest(@filters))
    

    gonItemsFiltersUri = Array.new
    @items.each do |element|
      element.localURI = item_path(:id => element, :old => @filters)
      gonItemsFiltersUri << element.localURI
    end
    gon.gonItemsFiltersUri = gonItemsFiltersUri
    
    @uriForItemsNumber = filters_getNumber_path(:old => @filters)
    gon.uriForItemsNumber = @uriForItemsNumber
    
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @items }
    end
  end

  # GET /items/1
  # GET /items/1.json
  def show
    require 'cgi'
    require 'digest'
    @uri = CGI.escape(params[:id])
    @uriHash = Digest::SHA1.hexdigest(@uri)
    
    @item = Item.find(@uri)
    
    @filters = parseFilterParams(params)
    
    render  :layout => 'empty'
  end
  

end
