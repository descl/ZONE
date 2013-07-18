include LinkedWordsHelper

class LinkedWord
  $endpoint = "http://fr.dbpedia.org/sparql"

  def self.complete(param = "")
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
                FILTER(lang(?label)='fr')
                ?label bif:contains '\"#{wordsRequest}\"'
                FILTER(regex(str(?label),\"^#{param}\",\"i\")).
                ?o dbpedia-owl:wikiPageWikiLink ?link
             }ORDER BY DESC(?popularity)  LIMIT 10"
    store = SPARQL::Client.new($endpoint)
    result = Array.new
    puts query
    store.query(query).each do |item|
      result << item[:label].value
    end
    return result
  end

  def self.find(param)
    query = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT DISTINCT ?o COUNT(?links) AS ?popularity  WHERE{
                ?o rdfs:label '#{param}'@fr.
                OPTIONAL{?o dbpedia-owl:wikiPageWikiLink ?links}
             }ORDER BY DESC(?popularity)  LIMIT 1"
    puts query
    store = SPARQL::Client.new($endpoint)
    store.query(query).each do |item|
      return item[:o].to_s
    end
    return nil
  end

  def self.getLinkedWords(param = "")
    #FILTER(lang(?linkedName) = 'fr')
    query = "SELECT DISTINCT ?linkedName WHERE{
                 ?o rdfs:label ?label.
                  FILTER(lang(?label)='fr')
                 ?label bif:contains \"'#{escapeText(param)}'\".
                 ?o rdf:type <http://www.w3.org/2002/07/owl#Thing>.
                 {?o prop-fr:fonction ?linked.}
                 UNION {?o prop-fr:parti ?linked.}
                 ?linked rdfs:label ?linkedName
                FILTER(lang(?linkedName)='fr')

             }"

    store = SPARQL::Client.new($endpoint)
    result = Array.new
    store.query(query).each do |item|
      continue if item.linkedName.to_s == nil
      result << item.linkedName.to_s
    end
    return result
  end
end