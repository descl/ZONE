require 'json'

class SearchesController < ApplicationController
  include SearchesHelper
  # GET /searches
  # GET /searches.json
  def index
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
        begin
          begin
            redirect_to(:back)
          rescue ActionController::RedirectBackError
            redirect_to root_path
          end
        rescue ActionController::RedirectBackError
          redirect_to root_path
        end
      return
    end
    @searches = Search.where(:user_id => current_user.id).paginate(:page => params[:page])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @searches.to_json(:include => [:sources,:filters]) }
    end
  end

  # GET /searches/1
  # GET /searches/1.json
  def show
    redirect_to items_path(:search => params[:id],:notice => notice )
  end

  def tagsCloud
    start_date = params[:start]
    end_date = params[:end]
    tagsInfos = Search.find(params[:id]).getTagsCloud(getUserId(),start_date,end_date)
    @tags = Array.new
    tagsInfos[:result].each do |tag|
      tag[:html] = {
          #title: tag[:weight],
          class: "label-tag",

          "data-content"=> tag_content(tag[:text], tag[:link]),
          "filter-uri" => tag[:link],
          "data-original-title" => tag[:text],
          "data-uri" => search_filters_path(:uri => tag[:link] )
      }
      @tags << tag
    end

    respond_to do |format|
      format.html {render html: @tags, :layout => 'emptyWithJS'}# index.html.erb
      format.json { render json: @tags.to_json, status: :ok }
    end
  end

  # GET /searches/new
  # GET /searches/new.json
  def new
    @search = Search.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @search }
    end
  end

  # GET /searches/1/edit
  def edit
    if params[:itemId] != nil
      @search = retrieveSearchFromForm(params)
    else
      @search = Search.find(params[:id])
    end
    @search.touch
    @search.save
    @search.generateThumbTagsCloud(getUserId())
  end

  # POST /searches
  # POST /searches.json
  def create
    begin
      @search = retrieveSearchFromForm(params)
    rescue
      flash[:error] = t("search.badKeyWord")
      flash.keep
      return redirect_to(root_path)
    end
    @search.touch
    isNew = ((@search.id == nil) && (params[:isNew] != "false"))
    respond_to do |format|
      if @search.save
        @search.generateThumbTagsCloud(getUserId())
        if isNew
          format.html { redirect_to items_path(:search => @search.id, :isNew => true )  }
        else
          format.html { redirect_to items_path(:search => @search.id )  }
        end
          format.json { render json: @search, status: :created, location: @search }
      else
        format.html { render action: "new" }
        format.json { render json: @search.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /searches/1
  # PUT /searches/1.json
  def update
    @search = retrieveSearchFromForm(params)

    respond_to do |format|
      if @search.update_attributes(params[:search])
        format.html { redirect_to items_path(:search => @search.id )  }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @search.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /searches/1
  # DELETE /searches/1.json
  def destroy
    if params[:id] != Rails.application.config.defaultRequestId.to_s
      @search = Search.find(params[:id])
      @search.destroy
    end

    respond_to do |format|
      format.html {
        begin
          begin
            redirect_to(:back)
          rescue ActionController::RedirectBackError
            redirect_to root_path
          end
        rescue ActionController::RedirectBackError
          redirect_to root_path
        end
      }
      format.json { head :no_content }
    end
  end

  def selectSources
    @sources = current_user.getSources
    render  :layout => 'empty'
  end

  def getUserId
    if user_signed_in?
      return current_user.id
    else
      return -1
    end

  end

  def retrieveSearchFromForm(form)

    search = Search.new
    if form[:itemId] != nil && form[:itemId] != '' && form[:itemId]!= Rails.application.config.defaultRequestId.to_s
      search = Search.find(form[:itemId])
      search.filters = Array.new
      search.sources = Array.new
    else
      search = Search.new
    end
    if form[:searchName] != nil && form[:searchName] != ""
      search.name = form[:searchName]
    end

    search.build_from_form(form,getUserId())

    return search
  end

  def getNewsNumber

    @search = Search.find(params[:search])
    @number = @search.getItemsNumber(getUserId(),params[:minDate].to_i,params[:maxDate].to_i)


    respond_to do |format|
      format.html {render :inline => "<%= @number %>"}
    end

  end
end
