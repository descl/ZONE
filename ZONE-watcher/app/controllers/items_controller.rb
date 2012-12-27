class ItemsController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  # GET /items
  # GET /items.json
  def index
    
    @filter = parseFilterParams(params)
    
    
    @items = Item.all
    

    gonItems = Array.new
    @items.each do |element|
      gonItems << url_encode(element.uri)
    end
    gon.items = gonItems
    
    gon.filter = filters_getNumber_path(:old => @filter)
    
    
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
    render  :layout => 'empty'
  end
  

end
