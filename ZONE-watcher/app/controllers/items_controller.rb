class ItemsController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /items
  # GET /items.json
  def index
    @filters = []
    @sources = []

    if params[:search] == nil
      @search = Search.find(Rails.application.config.defaultRequestId)
    else
      @search = Search.find(params[:search])
    end
    if params[:isNew] == "true"
      flash[:notice] = t('search.disclaimer')
    end

    if params[:per_page] != nil
      per_page = params[:per_page].to_i
    else
      per_page = 10
    end

    current_page = params[:page]
    current_page = 1 if current_page == nil
    current_page = Integer(current_page)

    @itemsNumber = @search.getItemsNumber
    #if itemsNumber > (10000 - per_page +1)
    #  itemsNumber = (10000 - per_page +1)
    #end

    @items = WillPaginate::Collection.create(current_page, per_page, @itemsNumber) do |pager|
      start = (current_page-1)*per_page # assuming current_page is 1 based.
      itemsTab = Item.all(@search,start,per_page)
      @sparqlRequest = itemsTab[:query]
      pager.replace(itemsTab[:result])
    end

    @items.each do |element|
      element.localURI = item_path(:id => element, :old => @filters)
    end
    
    @uriForItemsNumber = filters_getNumber_path(:old => @filters)

    #define the feed uri
    @feed_url= url_for(:controller => 'items', :action => 'index', :search => @search.id, :format => :rss)

    respond_to do |format|
      format.html
      format.js
      format.json { render json: @items }
      format.rss { render rss: @items }
    end
  end

  # GET /items/1
  # GET /items/1.json
  def show
    require 'cgi'
    require 'digest'
    uri = params[:id]
    if params[:format] != nil
      uri = uri+"."+params[:format]
    end
    uri = URI.escape(CGI.unescape(uri))
    if (uri[6] != '/' && uri[4]== ":") || (uri[7] != '/' && uri[5]== ":")
      uri = uri.insert(6,'/')
    end
    uri = CGI.escape(uri)
    
    @item = Item.find(uri,current_user)

    @filters = parseFilterParams(params)
    
    render  :layout => 'empty'
  end

  def deleteTag
    tag = params[:tag]
    if params[:tagUri] != nil && params[:tagUri] != ""
      tag = params[:tagUri]
    end
    if tag != ""
      @item = Item.find(params[:item],current_user)
      puts @item.to_json
      if @item == nil
        raise "item not found"
      end
      @item.deleteTag(tag)
    else
      raise "tag not found"
    end
    render :text => ""
  end

  def addTag
    tag = params[:tag]

    @item = Item.find(params[:item],current_user)
    @item.addTag(tag)
    @filter = SearchFilter.new(:value =>  tag)
    @filter.item = @item
    @filter.prop = ZoneOntology::PLUGIN_SOCIAL_ANNOTATION
    render  :layout => 'empty'
  end

end
