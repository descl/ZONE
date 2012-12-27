class ItemsController < ApplicationController
  include ERB::Util
  # GET /items
  # GET /items.json
  def index
    @filter = Array.new
    if(params.length > 1)
      if params[:old] != nil
        params[:old].each do |cur|
          @filter.push cur
        end
      end
      
      if params[:new] != nil
        @filter.push params[:new]
      end
    end
    
    
    @items = Item.all
    

    gonItems = Array.new
    @items.each do |element|
      gonItems << url_encode(element.uri)
    end
    gon.items = gonItems
    
    
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
