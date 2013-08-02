require 'net/http'
require 'cgi'

class SparqlRequest < ActiveRecord::Base
  def self.query(query,endpoint)
    params={
        "default-graph" => "",
        "should-sponge" => "",
        "query" => query,
        "debug" => "on",
        "timeout" => "",
        "format" => "json",
        "save" => "display",
        "fname" => ""
    }

    querypart=""

    params.each { |k,v|
      querypart+="#{k}=#{CGI.escape(v)}&"
    }
    puts query
    sparqlURL=endpoint+"?#{querypart}"
    puts sparqlURL
    response = Net::HTTP.get_response(URI.parse(sparqlURL))
    return JSON.parse(response.body)["results"]["bindings"]
  end
end
