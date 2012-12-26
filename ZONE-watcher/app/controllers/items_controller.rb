class ItemsController < ApplicationController
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
    

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @items }
    end
  end

  # GET /items/1
  # GET /items/1.json
  def show
    @uri = params[:id].insert 5 , "/"
    @item = Item.find(@uri)
    render  :layout => 'empty'
  end
end
