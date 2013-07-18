class SearchFilter < ActiveRecord::Base
  attr_accessible :value, :kind
  belongs_to :search
  def self.build_from_form(value,kind)
    if !value.start_with? "http"
      uri = LinkedWord.find(value)
      if uri != nil
        value = uri
      end
    end
    result = SearchFilter.new
    result.value = value
    result.kind = kind
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
    return "?concept <#{ZoneOntology::RSS_SOURCE}> <#{self.getUri}>"
  end
end
