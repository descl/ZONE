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
    "succes" if self.kind == "and"
    "info"   if self.kind == "or"
    "error"  if self.kind == "without"

    return "<span class=\"
                          label
                          label-#{self.kind == "rss" ? "warning" : "info"}
                          #{self.kind}Source
                        \">
            #{self.value}
            <i class=\"icon-remove\" onclick=\"$(this).closest(\"span\").remove();checkWell()\"></i>
            </span>"
  end
end
