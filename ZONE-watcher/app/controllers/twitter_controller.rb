class TwitterController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /twitter_timelines
  # GET /twitter_timelines.json
  def index
    if !user_signed_in? || current_user.provider != "twitter"
      flash[:error] = 'You are not logged with a twitter account.'
        redirect_to :back 
        return
    end
    
    userSource = Source.find("#{ZoneOntology::SOURCES_DATA_TWITTER_TIMELINE}/#{current_user.login}")
    if userSource.to_json == "null"
      add_timeline_to_sources
      flash[:notice] = 'Your twitter account has just been added to the annotation server. Tweets filtering will be possible for you in 5 - 10 minutes.'
    end

    @filter = Filter.new(:prop => "http://purl.org/rss/1.0/source",:value=> "#{ZoneOntology::SOURCES_DATA_TWITTER_TIMELINE}/#{current_user.login}")
    redirect_to items_path(:new => @filter)
  end
  
  def add_timeline_to_sources
    @source = Source.new(
      "#{ZoneOntology::SOURCES_DATA_TWITTER_TIMELINE}/#{current_user.login}",
      {
        :owner => current_user.id, 
        :attrs => {
          RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE,
          ZoneOntology::SOURCES_TWITTER_TOKEN =>current_user.token, 
          ZoneOntology::SOURCES_TWITTER_TOKEN_SECRET => current_user.tokenSecret
        }
      }
    )
    @source.save
  end

end
