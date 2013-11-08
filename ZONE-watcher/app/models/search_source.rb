class SearchSource < ActiveRecord::Base
  attr_accessible :value, :kind
  belongs_to :search
  def self.build_from_form(value,kind,user)
    result = SearchSource.new
    result.value = value
    result.kind = kind
    if value == "Toutes les sources RSS de Reador" || value == "Toutes les sources twitter"
      result.value = "all"
    end
    if kind == "twitter"
      if value.starts_with? "#"
        Twitter.add_hashtag_to_sources(value[1..value.length])
      elsif  value.starts_with? "@"
        Twitter.add_user_to_sources(value[1..value.length])
      end
    elsif kind == "rss"
      if !(value  =~ URI::regexp)
        return nil
      end
      source = Source.find(value)
      if source == nil
        puts source
        puts (source  =~ URI::regexp)
        source = Source.new(value)
        if user != nil
          source.owner = user
        end
        source.save
      else
        #TODO
        #if user != nil
        #  source.addOwner(user)
          #TODO : finish!
        #end
      end
    end

    return result
  end

  def getUri
    if self.kind == "rss"
      return value
    elsif self.kind == "twitter"
      return "http://twitter..../#{value}"
    end
  end

  def getSparqlTriple(user)
    if value == "all" || value ==  "Toutes les sources RSS de Reador" || value == "Toutes les sources twitter"
      if kind == "twitter"
        return "?concept <#{ZoneOntology::RSS_SOURCE}> <#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}>"
      elsif kind == "rss"
        return "?concept <#{ZoneOntology::RSS_SOURCE}> ?newsType. ?newsType rdf:type <#{ZoneOntology::SOURCES_TYPE_RSS}>"
      end
    end
    if kind == "twitter"
      if value.start_with? "#"
        return "{?concept <#{ZoneOntology::PLUGIN_TWITTER_HASHTAG}> ?val. ?val bif:contains '\"#{value}\"'} UNION { ?concept <#{ZoneOntology::PLUGIN_TWITTER_HASHTAG}> \"#{value}\" } "
      elsif value.start_with? "@"
        return "?concept <#{ZoneOntology::PLUGIN_TWITTER_AUTHOR}> \"#{value[0..-1]}\""
      end
    elsif kind == "rss"
      if value.start_with? ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE
        if user == nil || user < 0
          return nil
        else
          twitterUser = value[(ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE.length() +1),value.length]

          account = User.find(user)
          if twitterUser != account.login
            return nil
          end
        end
      end
      return "?concept <#{ZoneOntology::RSS_SOURCE}> <#{self.getUri}>"
    end
  end

  def getButton
    type= "info"
    self.kind == "rss" ? "warning" : "info"
    return "<span id='filter#{self.id}' class=\"label label-#{self.kind == "rss" ? "warning" : "info"} #{self.kind}Source\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"removeElement('#{self.id}',event);\"></i></span>"
  end

end
