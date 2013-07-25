class SearchSource < ActiveRecord::Base
  attr_accessible :value, :kind
  belongs_to :search
  def self.build_from_form(value,kind)
    result = SearchSource.new
    result.value = value
    result.kind = kind
    if value == "Toutes les sources RSS de Reador" || value == "Toutes les sources twitter"
      result.value = "all"
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

  def getSparqlTriple
    if value == "all" || value ==  "Toutes les sources RSS de Reador" || value == "Toutes les sources twitter"
      if kind == "twitter"
        return "?concept <#{ZoneOntology::RSS_SOURCE}> <#{ZoneOntology::SOURCES_TYPE_TWITTER_TIMELINE}>"
      elsif kind == "rss"
        return "?concept <#{ZoneOntology::RSS_SOURCE}> ?newsType. ?newsType rdf:type <#{ZoneOntology::SOURCES_TYPE_RSS}>"
      end
    end
    if kind == "twitter"
      if value.start_with? "#"
        return "?concept <#{ZoneOntology::SOURCES_TYPE_TWITTER_HASHTAG}> \"#{value}\" "
        #TODO need to be done...
      elsif value.start_with? "@"
        return "?concept <#{ZoneOntology::SOURCES_TYPE_TWITTER_AUTHOR}> \"#{value[1..-1]}\""
      end
    elsif kind == "rss"
      return "?concept <#{ZoneOntology::RSS_SOURCE}> <#{self.getUri}>"
    end
  end

  def getButton
    type= "info"
    self.kind == "rss" ? "warning" : "info"
    return "<span class=\"label label-#{self.kind == "rss" ? "warning" : "info"} #{self.kind}Source\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"$(this).closest(\'span\').remove(); $(\'#updateSearch\').trigger(\'click\');\"></i></span>"
  end

end
