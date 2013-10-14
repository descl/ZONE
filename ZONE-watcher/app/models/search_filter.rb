class SearchFilter < ActiveRecord::Base
  include Rails.application.routes.url_helpers
  include ActionView::Helpers::UrlHelper
  include ActionView::Helpers
  include SearchesHelper

  attr_accessible :value, :kind, :uri
  attr_accessor :prop,:item

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
      if self.uri.start_with? "http://dbpedia.org"
        self.uri = self.uri.insert 7, "www."
      end
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
    
    self.uri = "undefined" if self.uri == nil

    return "<span id=\"#{self.id}\" class=\"label label-#{type}\" draggable=\"true\" ondragstart=\"drag(event)\" filter-uri=\"#{self.uri}\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"$(this).closest(\'span\').remove(); showUpdate()\"></i></span>"
  end

  def getButtonInNews
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    @RSS_URI = 'http://purl.org/rss/1.0/'
    @SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
    @TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
    @TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'

    @LABEL_PEOPLE = "label label-success label-tag"
    @LABEL_PLACE = "label label-warning label-tag"
    @LABEL_TWITTER = "label label-twitter label-tag"
    @LABEL_OTHER = "label label-info label-tag"

    labels = @LABEL_OTHER

    if(self.value == "null")
      self.value = "/null"
    end

    filterVal=""
    if(self.prop.starts_with? @WIKI_META_URI)
      if(self.value.rindex('/') == nil)
        filterVal=self.value
      else
        filterVal=self.value[self.value.rindex('/')+1, self.value.length]
      end
      if self.prop.start_with? "http://www.wikimeta.org/Entities#TIME"
        return
      end
      labels= @LABEL_OTHER

    elsif(self.prop.starts_with? @INSEE_GEO_URI)
      filterVal=self.value[filter.value.rindex('/')+1, filter.value.length]
      labels = @LABEL_PLACE
    elsif(self.prop.starts_with? ZoneOntology::PLUGIN_SOCIAL_ANNOTATION)
      filterVal=self.value
      labels = @LABEL_PLACE
    elsif( (self.prop.start_with? @SVM_PLUGIN_URI) || (self.prop.starts_with? @OPEN_CALAIS_URI))
      filterVal=self.value
    elsif (self.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI)
      filterVal=self.value
      labels=@LABEL_TWITTER
    elsif(self.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      filterVal=self.value
      labels=@LABEL_TWITTER
    end

    return button_link(self.uri, labels,filterVal, self.item.uri)
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
            select * where {
              {<#{uri}> dbpedia-owl:abstract ?abstract.
              OPTIONAL{ <#{uri}> dbpedia-owl:thumbnail ?thumb}.
              Filter(lang(?abstract) = \"fr\")}
              UNION { <#{uri}> <http://dbpedia.org/ontology/wikiPageRedirects> ?sameAs}
            } LIMIT 1"
    store = SPARQL::Client.new(endpointDBpedia)
    query = store.query(queryStr)
    if query[0][:abstract] == nil && query[0][:sameAs]!= nil
      return SearchFilter.new(:uri =>query[0][:sameAs]).getInfos
    end
    return {:abstract => query[0][:abstract], :thumbnail => query[0][:thumb]}
  end
end
