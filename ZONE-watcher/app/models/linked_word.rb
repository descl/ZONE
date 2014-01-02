include LinkedWordsHelper

class LinkedWord

  def self.complete(param = "", limit=10)
    if param == ""
      return Array.new
    end
    endpoint = Rails.application.config.virtuosoEndpoint
    words = param.split
    wordsRequest  = ""
    words.each do |word|
      if word.size >= 4
        wordsRequest += word+"* "
      else
        if wordsRequest.size == 0
          wordsRequest += word+" "
        end
      end
    end
    wordsRequest = wordsRequest[0..-2]

    #TODO: manage lang preferences
    query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT DISTINCT  ?tag ?label COUNT(?item) AS ?popularity WHERE{
      GRAPH <#{ZoneOntology::GRAPH_TAGS}> {
        ?tag rdfs:label ?label.
        ?label bif:contains '\"#{wordsRequest}\"'
        FILTER(regex(str(?label),\"^#{param}\",\"i\")).
      }
      GRAPH <#{ZoneOntology::GRAPH_ITEMS}> {
        ?item <#{ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES}> ?tag
      }
    }ORDER BY DESC(?popularity)  LIMIT #{limit}"
    puts query
    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    result = Array.new

    begin
      store.query(query).each do |item|
        result << {:value => item.label.to_s, :uri => item.tag.to_s}
      end
    rescue
      if(param.include?" ")
        return complete(param[0,param.rindex(" ")])
      else
        return complete(param[0,param.size-1])
      end
    end
    return result
  end

  def self.find(param)
    endpoint = Rails.application.config.virtuosoEndpoint
    query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT DISTINCT ?o ?label COUNT(?links) AS ?popularity  WHERE{
      GRAPH <#{ZoneOntology::GRAPH_TAGS}> {
        ?o rdfs:label ?label.
        ?label bif:contains '\"#{param}\"'
      }
      GRAPH <#{ZoneOntology::GRAPH_ITEMS}> {
        OPTIONAL{?links <#{ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES}> ?o}
      }
    }ORDER BY DESC(?popularity)  LIMIT 1"
    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    store.query(query).each do |item|
      return {:uri => item[:o].to_s, :label => item[:label]}
    end
    return nil
  end

  def self.getLinkedWords(param = "")
    endpoint = Rails.application.config.virtuosoEndpoint
    #old query with dbpedia datas
    #query = "SELECT DISTINCT ?linkedName ?linked COUNT(?links) AS ?popularity WHERE{
    #             ?o rdfs:label ?label.
    #              FILTER(lang(?label)='en')
    #             ?label bif:contains \"'#{escapeText(param)}'\".
    #             ?o rdf:type <http://www.w3.org/2002/07/owl#Thing>.
    #             {?o rdf:type ?linked.}
    #             UNION {?o dbpedia-owl:party ?linked.}
    #             ?linked rdfs:label ?linkedName
    #             FILTER(lang(?linkedName)='en')
    #             ?linked ?l ?links
    #         }ORDER BY DESC(?popularity)"

    query = "SELECT ?linkedEntity ?linkedName ?kind ?popularity WHERE{
      {SELECT ?linkedEntity COUNT(?linkedEntity) AS ?popularity FROM <http://zone-project.org/datas/items> {
        ?item <#{ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES}> <#{param}>.
        ?item <#{ZoneOntology::PLUGIN_SPOTLIGHT_ENTITIES}> ?linkedEntity.
        FILTER(?linkedEntity != <#{param}>)
      } ORDER BY DESC(?popularity) LIMIT 10}
      GRAPH <http://zone-project.org/datas/tags> {
        ?linkedEntity rdfs:label ?linkedName.
        OPTIONAL{?linkedEntity <#{ZoneOntology::RDF_TYPE}> ?kind}.
      }
    }ORDER BY DESC(?popularity) LIMIT 10"
    puts query
    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    result = Array.new
    store.query(query).each do |item|
      continue if item.linkedName.to_s == nil
      result << {:value => item.linkedName.to_s, :uri => item.linkedEntity.to_s,:kind => item.kind}
    end
    return result
  end
end