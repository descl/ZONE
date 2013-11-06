class Filter
  include ActiveModel::Serialization
    
  attr_accessor :prop, :value

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
    endpoint = Rails.application.config.virtuosoEndpoint

    query = "PREFIX SOURCE: <#{ZoneOntology::SOURCES_PREFIX}>
    SELECT DISTINCT(?value)
    WHERE {
      ?uri <#{param[:prop]}> ?value.
      ?uri <#{ZoneOntology::SOURCES_OWNER}>\"#{param[:userId]}\"
    }"
    store = SPARQL::Client.new(endpoint)
    filters = Array.new
    store.query(query).each do |res|
      filters << Filter.new(:prop => param[:prop], :value => res.value.to_s)
    end
    return filters
  end

  def self.all
    endpoint = Rails.application.config.virtuosoEndpoint

    query = "PREFIX SOURCE: <#{ZoneOntology::SOURCES_PREFIX}>
    SELECT DISTINCT ?prop ?value
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE {
      ?prop <#{ZoneOntology::ANNOTATION}> \"true\".
      ?uri ?prop ?value.

    }"
    puts query
    store = SPARQL::Client.new(endpoint)
    filters = Array.new
    store.query(query).each do |res|
      filters << Filter.new(:prop => res.prop.to_s, :value => res.value.to_s)
    end
    return filters
  end

  def get_search_button
    return @value+" | "+@prop
  end

  def self.build_from_search(search)
    if search.kind == "RSS"
      return self.new(:prop => "http://purl.org/rss/1.0/source", :value => search.value)
    elsif search.kind == "twitter"
      return self.new(:prop => "http://purl.org/rss/1.0/source", :value => search.value)
    end
  end
end
