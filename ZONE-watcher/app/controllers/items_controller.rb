class ItemsController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /items
  # GET /items.json
  def index
    @filters = []
    @sources = []
    @searches = []
    @search = nil
    @layout = nil
    @minDate = params[:minDate].to_i
    @maxDate = params[:maxDate].to_i

    case params[:layout]
      when "time"
        @layout = "time"
      when "card"
        @layout = "card"
      when "list"
        @layout = "list"
      else
        @layout = "time"
    end

    userId=-1

    if user_signed_in?
      userId = current_user.id
      @searches = Search.where(:user_id => current_user.id).limit(15).order("updated_at desc")
      if params[:search] == nil
        if @searches.size > 0
          @search = @searches.first
        else
          @search = Search.find(Rails.application.config.defaultRequestId)
          @searches << @search
        end
      else
        begin
          @search = Search.find(params[:search])
          #check if he is allow to see the search
          if ((@search.user_id != userId) && (params[:search].to_i != Rails.application.config.defaultRequestId))
            @search =  @searches.first
            @error = t('search.cheaterDisclaimer')
          end
          if !@searches.include? @search
            @searches << @search
          end
        rescue
          if @searches.size == 0
            @search = Search.find(Rails.application.config.defaultRequestId)
          else
            @search = @searches.first
          end
          @error = t('search.noSearchDisclaimer')
        end
      end
    else
      #is visitor
      @searches = []

      if params[:search] == nil
        @search = Search.find(Rails.application.config.defaultRequestId)
        @searches << @search
      else
        begin
          @search = Search.find(params[:search])
          if ((@search.user_id != -1) && (params[:search].to_i != Rails.application.config.defaultRequestId))
            @search =  Search.find(Rails.application.config.defaultRequestId)
            @error = t('search.cheaterDisclaimer')
          end
          if !@searches.include? @search
            @searches << @search
          end
        rescue
          @search =  Search.find(Rails.application.config.defaultRequestId)
          @searches << Search.find(Rails.application.config.defaultRequestId)
          @error = t('search.noSearchDisclaimer')
        end
      end
    end

    if params[:per_page] != nil
      per_page = params[:per_page].to_i
    else
      per_page = 10
    end
    if params[:load_on_top] == "true"
      @loadOnTop = true
    else
      @loadOnTop = false
    end

    current_page = params[:page]
    current_page = 1 if current_page == nil
    current_page = Integer(current_page)


    if (@error != nil || (@search.filters.empty? && @search.sources.empty?) )
      if(@error != nil)
        flash[:error] = @error
      else
        flash[:error] = t('search.noFiltersDisclaimer')
      end
      @itemsNumber = 0

      @items = WillPaginate::Collection.create(1, 0, 0) do |pager|
        start = 0
        itemsTab = Array.new()
        @sparqlRequest = ""
        pager.replace(itemsTab)
      end
    else
      @itemsNumber = @search.getItemsNumber(userId)

      if params[:isNew] == "true"
        flash[:notice] = t('search.disclaimer')
      elsif(@itemsNumber == 0)
        flash[:error] = t('search.noResDisclaimer')
      end

      itemsNumberForSearchWithDates = @search.getItemsNumber(userId,@minDate,@maxDate)
      @items = WillPaginate::Collection.create(current_page, per_page, itemsNumberForSearchWithDates) do |pager|
        start = (current_page-1)*per_page # assuming current_page is 1 based.
        itemsTab = Item.all(@search,userId,start,per_page,@minDate,@maxDate)
        @sparqlRequest = itemsTab[:query]
        @error = itemsTab[:error]
        pager.replace(itemsTab[:result])
      end
      if(@error == 'cheater' )
        flash[:error] = t('search.cheaterDisclaimer')
      end

      #define the feed uri used by application.html.erb
      @feed_url= url_for(:controller => 'items', :action => 'index', :search => @search.id, :format => :rss)

    end



    respond_to do |format|
      format.html {
        if params[:widget] == "true"
          render "items/widget", :layout => "emptyWithJS", :locals => {:items => @items}
        else
          render html: @items
        end
      }
      format.js
      format.json { render json: @items }
      format.rss { render rss: @items }
    end
  end

  caches_action :show, :cache_path => Proc.new { |c| "#{c.params} - #{user_signed_in?}" }, :expires_in => 1.minute
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
    #hack in order to print items with ending slash
    if @item == nil
      @item  = Item.find(uri+"/",current_user)
    end

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
