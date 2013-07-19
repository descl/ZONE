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
    return value
  end

  def getSparqlTriple
    if value.start_with? "http"
      return "?concept ?filter#{self.id} <#{self.value}>"
    else
      return "?concept ?filter#{self.id} \"#{self.value}\""
    end
  end
end
