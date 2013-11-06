class SourcesController < ApplicationController
  # GET /sources
  # GET /sources.json
  def index
    @themes = Filter.find(:prop => ZoneOntology::SOURCES_THEME)
    @langs = Filter.find(:prop => ZoneOntology::SOURCES_LANG)
    @source = Source.new


    respond_to do |format|
      format.html {
        if !user_signed_in?
          flash[:error] = t("devise.failure.unauthenticated")
          begin
            redirect_to(:back)
          rescue ActionController::RedirectBackError
            redirect_to root_path
          end
          return
        else
          @sources = current_user.getSources
          @sources.sort! {|a,b| [a.theme,a.label,a.uri,a.lang]<=>[b.theme,b.label,b.uri,b.lang]}
        end
      }# index.html.erb
      format.json {
        sources = Source.all
        @result = []
        sources.each{|p|
          item =  {'prop' => p.uri, :value => p.uri}
          item[:selected] = true if item[:value] == params[:selected]
          @result << item
        }
        render json: @result }
    end
  end

  # GET /sources/1
  # GET /sources/1.json
  def show
    @source = Source.find(params[:id])

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @source }
    end
  end

  # GET /sources/new
  # GET /sources/new.json
  def new
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
      return
    end
    @themes = Filter.find(:prop => ZoneOntology::SOURCES_THEME)
    @langs = Filter.find(:prop => ZoneOntology::SOURCES_LANG)
    @source = Source.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @source }
    end
  end

  def langs
    @langs = Filter.find(:prop => ZoneOntology::SOURCES_LANG)
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
    @themes = Filter.find(:prop => ZoneOntology::SOURCES_THEME,:userId => current_user.id)
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
      flash[:error] =  t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
      return
    end
    @source = Source.find(params[:id])
  end

  # POST /sources
  # POST /sources.json
  def create(update=false)
    if !user_signed_in?
      flash[:error] =  t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
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
          message = t("source.updated")
        else
          message = t("source.created")
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
      flash[:error] = t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
      return
    end
    @source = Source.find(params[:id])

    @source.destroy
    return create(true)
  end

  def changeCategory
    if !user_signed_in?
      flash[:error] =  t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
      return
    end

    puts params[:id]

    @oldSource = Source.find(params[:id])

    @newSource = Source.new(@oldSource.uri,{
        :label => @oldSource.label,
        :lang => @oldSource.lang,
        :theme => params[:theme],
        :owner => @oldSource.owner
    })

    if @newSource.valid?
      @oldSource.destroy
      @newSource.save
    end
  end

  # DELETE /sources/1
  # DELETE /sources/1.json
  def destroy
    @source = Source.find(params[:id])
    if @source.destroy
      flash[:notice] = t("source.destroyed.ok")
    else
      flash[:error] = t("source.destroyed.err")
    end
    respond_to do |format|
      format.html { redirect_to sources_url }
      format.json { head :no_content }
    end
  end

  def uploadopml
    xml =  params['upload']['datafile'].read
    opml = OpmlSaw::Parser.new(xml)
    opml.parse
    puts opml.feeds
    @sourcesList = Array.new
    opml.feeds.each do |r|
      if r[:xml_url] == nil
        next;
      end
      s = Source.new(r[:xml_url],{
        :label => r[:title],
        :owner => current_user.id,
        :theme => r[:tag]
      })
      @sourcesList << s.uri
      s.save
    end
    flash[:notice] = 'Sources added'
    respond_to do |format|
      format.js
    end
  end
end
