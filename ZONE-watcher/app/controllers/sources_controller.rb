class SourcesController < ApplicationController
  # GET /sources
  # GET /sources.json
  def index
    if !user_signed_in?
      flash[:error] = 'You are not logged in'
      redirect_to :back
      return
    end
    @sources = Source.all(
        "?uri <#{ZoneOntology::SOURCES_OWNER}> ?owner.
         Filter(str(?owner) = \"#{current_user.id}\")")

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @sources }
    end
  end

  # GET /sources/1
  # GET /sources/1.json
  def show
    @source = Source.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @source }
    end
  end

  # GET /sources/new
  # GET /sources/new.json
  def new
    if !user_signed_in?
      flash[:error] = 'You are not logged in'
      redirect_to :back
      return
    end
    @themes = Filter.all(:prop => ZoneOntology::SOURCES_THEME)
    @langs = Filter.all(:prop => ZoneOntology::SOURCES_LANG)
    @source = Source.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @source }
    end
  end

  def langs
    @langs = Filter.all(:prop => ZoneOntology::SOURCES_LANG)
    @result = []
    @langs.each{|p|
      item =  {'text' => p.value, :value => p.value}
      item[:selected] = true if item[:value] == params[:selected]
      @result << item
    }

    respond_to do |format|
      format.json { render json: @result
      }
    end
    #render :json => { @langs.as_json }
  end

  def themes
    @themes = Filter.all(:prop => ZoneOntology::SOURCES_THEME)
    @result = []
    @themes.each{|p|
      item =  {'text' => p.value, :value => p.value}
      item[:selected] = true if item[:value] == params[:selected]
      @result << item
    }
    respond_to do |format|
      format.json { render json: @result }
    end
  end

  # GET /sources/1/edit
  def edit
    if !user_signed_in?
      flash[:error] = 'You are not logged in'
      redirect_to :back
      return
    end
    @source = Source.find(params[:id])
  end

  # POST /sources
  # POST /sources.json
  def create(update=false)
    if !user_signed_in?
      flash[:error] = 'You are not logged in'
      redirect_to :back
      return
    end
    @source = Source.new(params[:source][:uri],{
        :label => params[:source][:label],
        :lang => params[:lang],
        :theme => params[:theme],
        :owner => current_user.id
    })
    respond_to do |format|
      if @source.valid? && @source.save
        if update == true
          message = 'Source was successfully updated.'
        else
          message = 'Source was successfully created.'
        end
        format.html { redirect_to "/sources", notice: message }
        format.json { render json: @source, status: :created, location: @source }
      else
        format.html { render action: "new" }
        format.json { render json: @source.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /sources/1
  # PUT /sources/1.json
  def update
    if !user_signed_in?
      flash[:error] = 'You are not logged in'
      redirect_to :back
      return
    end
    @source = Source.find(params[:id])

    @source.destroy
    return create(true)
  end

  # DELETE /sources/1
  # DELETE /sources/1.json
  def destroy
    @source = Source.find(params[:id])
    if @source.destroy
      flash[:notice] = 'Source destroyed'
    else
      flash[:error] = 'Problem occured while removing source'
    end
    respond_to do |format|
      format.html { redirect_to sources_url }
      format.json { head :no_content }
    end
  end

  def uploadopml
    require 'rexml/document'
    require 'opml'
    xml =  params['upload']['datafile'].read
    opml = OpmlSaw::Parser.new(xml)
    opml.parse
    opml.feeds.each do |r|
      if r[:xml_url] == nil
        next;
      end

      s = Source.new(r[:xml_url],{
        :label => r[:title],
        :owner => current_user.id
      })
      s.save
    end
    flash[:notice] = 'Sources added'
    respond_to do |format|
      format.html { redirect_to sources_url }
      format.json { head :no_content }
    end
  end
end
