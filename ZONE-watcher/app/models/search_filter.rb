class SearchFilter < ActiveRecord::Base
  include Rails.application.routes.url_helpers
  include ActionView::Helpers::UrlHelper
  include ActionView::Helpers
  include SearchesHelper

  attr_accessible :value, :kind, :uri
  attr_accessor :prop,:item, :type, :abstract, :thumb

  belongs_to :search
  def self.build_from_form(filter,kind)
    result = SearchFilter.new

    if filter["uri"] != nil
      result.uri = filter["uri"]
    elsif filter["kind"] == "dbpedia"
      word = LinkedWord.complete(filter["value"],1)
      if word == nil || word.size == 0
        raise "CantCompleteRequest"
      end
      word = word[0]
      result.uri = word[:uri]
      result.value = word[:value]
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
    
    self.uri = "undefined" if self.uri == nil

    return "<span id=\"filter#{self.id}\" class=\"label label-#{type}\" draggable=\"true\" ondragstart=\"drag(event)\" filter-value=\"#{self.value}\" filter-uri=\"#{self.uri}\">#{self.value} <i class=\"icon-remove pointerMouse\" onclick=\"removeElement('#{self.id}',event);\"></i></span>"
  end

  def getButtonInNews
    @URI_SPOTLIGHT = 'http://zone-project.org/model/plugins/Spotlight#entities'
    @URI_LANG = 'http://zone-project.org/model/plugins#lang'
    @URI_TWITTER = 'http://zone-project.org/model/plugins/twitter'
    @URI_SOCIAL = ZoneOntology::PLUGIN_SOCIAL_ANNOTATION


    @URI_PLACE = 'http://dbpedia.org/ontology/Place'
    @URI_WORK = 'http://dbpedia.org/ontology/Work'
    @URI_ORGANIZATION= 'http://schema.org/Organization'
    @URI_PERSON = 'http://dbpedia.org/ontology/Person'
    @URI_EVENT = 'http://dbpedia.org/ontology/Event'

    @LABEL_PLACE   = "label-place"
    @LABEL_WORK    = "label-work"
    @LABEL_ORGANIZATION    = "label-organization"
    @LABEL_PERSON    = "label-person"
    @LABEL_EVENT    = "label-event"

    if(self.value == "null")
      self.value = "/null"
    end

    if(self.type != nil)
      if(self.type == @URI_PLACE)
        kind = "place"
      elsif(self.type == @URI_WORK)
        kind= "work"
      elsif(self.type == @URI_ORGANIZATION)
        kind= "organization"
      elsif(self.type == @URI_PERSON)
        kind= "person"
      elsif(self.type == @URI_EVENT)
        kind= @LABEL_EVENT
      end
    elsif(self.prop.starts_with? @URI_SOCIAL)
      kind = "social"
    elsif(self.prop.start_with? @URI_TWITTER)
      kind="twitter"
    elsif(self.prop.start_with? @URI_LANG)
      kind="lang"
    else
      kind = "other"
    end

    filterVal = getPrettyPrintingValue()
    return button_link(self, kind,filterVal)
  end

  def getPrettyPrintingValue
    if self.value != nil && !self.value.start_with?("http")
      return self.value
    end

    if(self.value == nil)
      self.value = self.uri
    end

    filterVal = ""
    if(self.value.starts_with? "http")
      if(self.value.rindex('/') == nil)
        filterVal=self.value
      else
        filterVal=self.value[self.value.rindex('/')+1, self.value.length]
      end
      if(filterVal.rindex("%23") != nil)
        filterVal=filterVal[0, filterVal.rindex("%23")]

      end
    end

    return filterVal.gsub "_", " "
  end
  def getInfos
    endpoint = Rails.application.config.virtuosoEndpoint
    if self.uri == nil
      return {:abstract => nil, :thumbnail => nil}
    end

    if self.uri.start_with? "http://fr.dbpedia"
      endpointDBpedia = "http://fr.dbpedia.org/sparql"
      lang = "fr"
    else
      endpointDBpedia = "http://dbpedia.org/sparql"
      lang = "en"
    end

    queryStr = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
            select * where {
              {<#{uri}> dbpedia-owl:abstract ?abstract.
              OPTIONAL{ <#{uri}> dbpedia-owl:thumbnail ?thumb}.
              Filter(lang(?abstract) = \"#{lang}\")}
              UNION { <#{uri}> <http://dbpedia.org/ontology/wikiPageRedirects> ?sameAs}
            } LIMIT 1"
    store = SPARQL::Client.new(endpointDBpedia)
    query = store.query(queryStr)
    if query.size == 0
      return nil
    end
    if query[0][:abstract] == nil && query[0][:sameAs]!= nil
      return SearchFilter.new(:uri =>query[0][:sameAs]).getInfos
    end

    linkedEntities = LinkedWord.getLinkedWords(uri)
    return {:abstract => query[0][:abstract], :thumbnail => query[0][:thumb] ,:linkedEntities => linkedEntities, :kind => query[0][:kind]}
  end
end
