class LinkedWord
  $endpoint = "http://fr.dbpedia.org/sparql"

  def self.complete(param = "")
    query = "SELECT DISTINCT  ?o ?label COUNT(?link) AS ?popularity
                WHERE{
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
end