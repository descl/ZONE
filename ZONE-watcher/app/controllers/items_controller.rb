class ItemsController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /items
  # GET /items.json
  def index
    
    @filters = parseFilterParams(params)
    
    
    current_page = params[:page]
    current_page = 1 if current_page == nil
    current_page = Integer(current_page)
    per_page = 10
    pageNumber = calculateNumber(params)
    if pageNumber > (10000 - per_page +1)
      pageNumber = (10000 - per_page +1)
    end
    @items = WillPaginate::Collection.create(current_page, per_page, pageNumber) do |pager|
      start = (current_page-1)*per_page # assuming current_page is 1 based.
      pager.replace(Item.all(generateFilterSPARQLRequest(@filters),start,per_page))
    end

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
    @uri = params[:id]
    if params[:format] != nil
      @uri = @uri+"."+params[:format]
    end
    @uri = URI.escape(CGI.unescape(@uri))
    if @uri[6] != '/'
      @uri = @uri.insert(6,'/')
    end
    @uri = CGI.escape(@uri)

    @uriHash = Digest::SHA1.hexdigest(@uri)
    
    @item = Item.find(@uri)
    
    @filters = parseFilterParams(params)
    
    render  :layout => 'empty'
  end
  

end
