class TwitterController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /twitter_timelines
  # GET /twitter_timelines.json
  def index
    if !user_signed_in? || (current_user.provider == nil) || ((current_user.provider != nil) && (!current_user.provider.include? "twitter"))
      flash[:error] = t("twitter.err.notlogged")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
        return
    end
    
    userSource = Source.find("#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{current_user.login}")
    if userSource.to_json == "null"
      current_user.add_timeline_to_sources
      flash[:notice] = t("twitter.created")
    end

    sourceURI =  "#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{current_user.login}"

    search = nil
    Search.joins(:sources).where(["search_sources.value = ?", sourceURI]).each do |i|
      if i.filters.length == 0
        search = i
      end
    end

    if search == nil || search.user_id != current_user.id
      redirect_to url_for(:controller => :searches, :action => :create,  :sources => {:rss => [sourceURI]}, :searchName => "TwitterTimeline", :isNew => true), :method => 'post'
    else
      redirect_to url_for(search)
    end
  end


end
