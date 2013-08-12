class StatsController < ApplicationController
  # GET /stats
  # GET /stats.json
  def index
    @stats = Array.new
    endpoint = Rails.application.config.virtuosoEndpoint
    store = SPARQL::Client.new(endpoint)


    kind ="number of news"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_ITEMS}>
    WHERE {
      ?concept RSS:title ?title.
      OPTIONAL { ?concept RSS:pubDateTime ?pubDateTime}.
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    kind ="news not annotated for ExtractArticlesContent"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept <http://zone-project.org/model/plugins/ExtractArticlesContent> ?pluginDefined.
      FILTER (!bound(?pluginDefined))
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    kind ="news not annotated for WikiMeta"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept <http://zone-project.org/model/plugins/WikiMeta> ?pluginDefined.
      FILTER (!bound(?pluginDefined))
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    kind ="news not annotated for OpenCalais"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept <http://zone-project.org/model/plugins/OpenCalais> ?pluginDefined.
      FILTER (!bound(?pluginDefined))
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    kind ="news not annotated for INSEEGeo"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept <http://zone-project.org/model/plugins/INSEEGeo> ?pluginDefined.
      FILTER (!bound(?pluginDefined))
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    @stats << {:kind => "", :val => ""}
    ###############################################################
    kind ="number of sources"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE}>.
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}

    ###############################################################
    kind ="number of twitter accounts"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
    SELECT DISTINCT COUNT(?concept) AS ?num
    FROM <#{ZoneOntology::GRAPH_SOURCES}>
    WHERE {
      ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE_TWITTER}>.
    }"
    @stats << {:kind => kind, :val => store.query(query)[0][:num]}








    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @stats }
    end
  end
end
