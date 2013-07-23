class SearchFilter < ActiveRecord::Base
  attr_accessible :value, :kind, :uri
  belongs_to :search
  def self.build_from_form(value,kind)
    if !value.start_with? "http"
      uri = LinkedWord.find(value)
    else
      uri = value
    end

    result = SearchFilter.new
    result.value = value
    result.uri = uri
    result.kind = kind
    return result
  end

  def getUri
    return uri
  end

  def getSparqlTriple
    if value.start_with? "http"
      return "?concept ?filter#{self.id} <#{self.value}>"
    else
      return "?concept ?filter#{self.id} \"#{self.value}\""
    end
  end

  def getButton
    type= "info"
    type = "success" if self.kind == "and"
    type = "info"   if self.kind == "or"
    type = "danger"  if self.kind == "without"

    return "<span class=\"label label-#{type}\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"$(this).closest(\'span\').remove();checkWell()\"></i></span>"
  end
end
