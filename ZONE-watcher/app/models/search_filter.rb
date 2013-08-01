class SearchFilter < ActiveRecord::Base
  attr_accessible :value, :kind, :uri
  attr_accessor :prop #define the property of tags comming from items

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

    return "<span id=\"#{self.id}\" class=\"label label-#{type}\" draggable=\"true\" ondragstart=\"drag(event)\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"$(this).closest(\'span\').remove(); showUpdate()\"></i></span>"
  end

  def getInfos
    $endpointDBpedia = "http://dbpedia.org/sparql"
    queryStr = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
            select ?abstract ?thumb where {
              <#{self.uri}> dbpedia-owl:abstract ?abstract.
              <#{self.uri}> dbpedia-owl:thumbnail ?thumb.
              Filter(lang(?abstract) = \"fr\")
            } LIMIT 1"
    store = SPARQL::Client.new($endpointDBpedia)
    query = store.query(queryStr)
    puts query
    return {:abstract => query.select("?abstract"), :thumbnail => query.select("?thumb")}
  end
end
