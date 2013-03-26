class SourcesController < ApplicationController
  # GET /sources
  # GET /sources.json
  def index
    @sources = Source.all

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
    @langs = FilterText.all(:prop => ZoneOntology::SOURCES_LANG)
    @result = []
    @langs.each{|p|
      @result << {'text' => p.value, :value => p.value}
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
      @result << {'text' => p.value, :value => p.value}
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
  def create
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
       format.html { redirect_to @source, notice: 'Source was successfully created.' }
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
    #TODO: should use the destroy method
    respond_to do |format|
      if @source.update_attributes(params[:source])
        format.html { redirect_to @source, notice: 'Source was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @source.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /sources/1
  # DELETE /sources/1.json
  def destroy
    @source = Source.find(params[:id])
    @source.destroy

    respond_to do |format|
      format.html { redirect_to sources_url }
      format.json { head :no_content }
    end
  end
end
