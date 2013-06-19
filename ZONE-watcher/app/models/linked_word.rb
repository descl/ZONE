class LinkedWord
  $endpoint = "http://fr.dbpedia.org/sparql"

  def self.complete(param = "")
    query = "SELECT DISTINCT  ?o ?label COUNT(?link) AS ?popularity WHERE{
                ?o rdfs:label ?label.
                FILTER(lang(?label)='fr')
                ?label bif:contains \"#{param}\"
                FILTER(regex(str(?label),\"^#{param}\",\"i\")).
                ?o dbpedia-owl:wikiPageWikiLink ?link
             }ORDER BY DESC(?popularity)  LIMIT 10"



    store = SPARQL::Client.new($endpoint)
    result = Array.new
    store.query(query).each do |item|
      result << item.label.to_s
    end
    return result
  end

  def self.getLinkedWords(param = "")
    query = "SELECT DISTINCT ?linkedName WHERE{
                 ?o rdfs:label ?label.
                 ?label bif:contains \"'#{param}'\".
                 ?o rdf:type <http://www.w3.org/2002/07/owl#Thing>.
                 {?o prop-fr:fonction ?linked.}
                 UNION {?o prop-fr:parti ?linked.}
                 ?linked rdfs:label ?linkedName
                 FILTER(lang(?linkedName) = 'fr')
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