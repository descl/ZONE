class SearchFilter < ActiveRecord::Base
  attr_accessible :value, :kind, :uri
  attr_accessor :prop #define the property of tags comming from items

  belongs_to :search
  def self.build_from_form(filter,kind)
    result = SearchFilter.new

    if filter["uri"] != nil
      result.uri = filter["uri"]
    end
    result.value = CGI.unescape(filter["value"])
    result.kind = kind
    return result
  end

  def getUri
    return uri
  end

  def getSparqlTriple
    if self.uri != nil
      return "?concept ?filter#{self.id} <#{self.uri}>"
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
    if self.uri == nil
      return {:abstract => nil, :thumbnail => nil}
    end
    if self.uri.start_with? "http://www"
      self.uri = "http://#{self.uri[11,self.uri.length]}"
    end

    endpointDBpedia = "http://dbpedia.org/sparql"
    queryStr = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
            select ?abstract ?thumb where {
              <#{uri}> dbpedia-owl:abstract ?abstract.
              OPTIONAL{ <#{uri}> dbpedia-owl:thumbnail ?thumb}.
              Filter(lang(?abstract) = \"fr\")
            } LIMIT 1"
    store = SPARQL::Client.new(endpointDBpedia)
    query = store.query(queryStr)
    return {:abstract => query[0][:abstract], :thumbnail => query[0][:thumb]}
  end
end
