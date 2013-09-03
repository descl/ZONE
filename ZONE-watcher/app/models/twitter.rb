class Twitter
  def self.add_hashtag_to_sources(tag)
    source = Source.find("#{ZoneOntology::SOURCES_TYPE_TWITTER_HASHTAG}/#{tag}")
    if source == nil

      @source = Source.new(
          "#{ZoneOntology::SOURCES_TYPE_TWITTER_HASHTAG}/#{tag}",
          {
              :attrs => {
                  RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_HASHTAG
              }
          }
      )
      @source.save
    end
  end

  def self.add_user_to_sources(user)
    source = Source.find("#{ZoneOntology::SOURCES_TYPE_TWITTER_AUTHOR}/#{user}")
    if source == nil
      @source = Source.new(
          "#{ZoneOntology::SOURCES_TYPE_TWITTER_AUTHOR}/#{user}",
          {
              :attrs => {
                  RDF.type => ZoneOntology::SOURCES_TYPE_TWITTER_AUTHOR
              }
          }
      )
      @source.save
    end
  end
end