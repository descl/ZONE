class Filter
  include ActiveModel::Serialization
    
  attr_accessor :prop, :value

  $endpoint = 'http://localhost:8890/sparql/'
  
  def initialize(attributes = {})  
    attributes.each do |name, value|  
      send("#{name}=", value)  
    end  
  end
  def to_param
     {:prop => @prop, :value => @value}
  end

  #find all filters with a specific prop
  def self.find(param = "")

    query = "PREFIX SOURCE: <#{ZoneOntology::SOURCES_PREFIX}>
    SELECT DISTINCT(?value)
    WHERE {
      ?uri <#{param[:prop]}> ?value.
    }"
    store = SPARQL::Client.new($endpoint)
    filters = Array.new
    store.query(query).each do |res|
      filters << Filter.new(:prop => param[:prop], :value => res.value.to_s)
    end
    return filters
  end

  def self.all

    query = "PREFIX SOURCE: <#{ZoneOntology::SOURCES_PREFIX}>
    SELECT DISTINCT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE {
      ?prop <#{ZoneOntology::ANNOTATION}> \"true\".
      ?uri ?prop ?value.

    }"
    puts query
    store = SPARQL::Client.new($endpoint)
    filters = Array.new
    store.query(query).each do |res|
      filters << Filter.new(:prop => res.prop.to_s, :value => res.value.to_s)
    end
    return filters
  end
end
