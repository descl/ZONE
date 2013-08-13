include LinkedWordsHelper

class LinkedWord

  def self.complete(param = "")
    endpoint = "http://dbpedia.org/sparql"
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

    query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT DISTINCT  ?o ?label COUNT(?link) AS ?popularity WHERE{
                ?o rdfs:label ?label.
                FILTER(lang(?label)='fr' || lang(?label)='en')
                ?label bif:contains '\"#{wordsRequest}\"'
                FILTER(regex(str(?label),\"^#{param}\",\"i\")).
                ?o ?t ?link
             }ORDER BY DESC(?popularity)  LIMIT 10"
    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    result = Array.new

    store.query(query).each do |item|
      result << {:value => item.label.to_s, :uri => item.o.to_s}
    end
    return result
  end

  def self.find(param)
    endpoint = "http://dbpedia.org/sparql"
    query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT DISTINCT ?o COUNT(?links) AS ?popularity  WHERE{
                ?o rdfs:label ?label.
                ?label bif:contains '\"#{param}\"'
                OPTIONAL{?o ?t ?links}
             }ORDER BY DESC(?popularity)  LIMIT 1"
    puts query
    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    store.query(query).each do |item|
      return item[:o].to_s
    end
    return nil
  end

  def self.getLinkedWords(param = "")
    endpoint = "http://dbpedia.org/sparql"
    #FILTER(lang(?linkedName) = 'fr')
    query = "SELECT DISTINCT ?linkedName ?linked COUNT(?links) AS ?popularity WHERE{
                 ?o rdfs:label ?label.
                  FILTER(lang(?label)='en')
                 ?label bif:contains \"'#{escapeText(param)}'\".
                 ?o rdf:type <http://www.w3.org/2002/07/owl#Thing>.
                 {?o rdf:type ?linked.}
                 UNION {?o dbpedia-owl:party ?linked.}
                 ?linked rdfs:label ?linkedName
                 FILTER(lang(?linkedName)='en')
                 ?linked ?l ?links

             }ORDER BY DESC(?popularity)"

    store = SPARQL::Client.new(endpoint,{:read_timeout => 10})
    result = Array.new
    store.query(query).each do |item|
      continue if item.linkedName.to_s == nil
      result << {:value => item.linkedName.to_s, :uri => item.linked.to_s}
    end
    return result
  end
end