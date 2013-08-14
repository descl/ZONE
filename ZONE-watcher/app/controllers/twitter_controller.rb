class TwitterController < ApplicationController
  include ERB::Util
  include ApplicationHelper
  include FiltersHelper
  # GET /twitter_timelines
  # GET /twitter_timelines.json
  def index
    if !user_signed_in? || current_user.provider != "twitter"
      flash[:error] = t("twitter.err.notlogged")
        redirect_to :back 
        return
    end
    
    userSource = Source.find("#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{current_user.login}")
    if userSource.to_json == "null"
      add_timeline_to_sources
      flash[:notice] = t("twitter.created")
    end

    sourceURI =  "#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{current_user.login}"
    redirect_to url_for(:controller => :searches, :action => :create,  :sources => {:rss => [sourceURI]}), :method => 'post'
  end
  
  def add_timeline_to_sources
    @source = Source.new(
      "#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}/#{current_user.login}",
      {
        :owner => current_user.id, 
        :attrs => {
          RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE,
          RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER,
          ZoneOntology::SOURCES_TWITTER_TOKEN =>current_user.token, 
          ZoneOntology::SOURCES_TWITTER_TOKEN_SECRET => current_user.tokenSecret
        }
      }
    )
    @source.save
  end

  def add_hashtag_to_sources(tag)

    @source = Source.new(
        "#{ZoneOntology::SOURCES_DATA_TWITTER_HASHTAG}/##{tag}",
        {
            :owner => current_user.id,
            :attrs => {
                RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_HASHTAG
            }
        }
    )
    @source.save
  end

end
