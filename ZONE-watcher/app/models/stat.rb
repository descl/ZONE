class Stat
  def self.all
    stats = Array.new
    endpoint = Rails.application.config.virtuosoEndpoint
    store = SPARQL::Client.new(endpoint)



    kind ="news not annotated for ExtractArticlesContent"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
          ?concept <http://purl.org/rss/1.0/title> ?title.
          OPTIONAL {?concept <http://zone-project.org/model/plugins/ExtractArticlesContent> ?pluginDefined}
          FILTER (!bound(?pluginDefined))
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    kind ="news not annotated for langDetect"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
          ?concept <http://purl.org/rss/1.0/title> ?title.
          ?concept <#{ZoneOntology::PLUGIN_EXTRACT_ARTICLES_CONTENT}> ?deps1.
          OPTIONAL {?concept <http://zone-project.org/model/plugins#lang> ?pluginDefined}
          FILTER (!bound(?pluginDefined))
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)


    ###############################################################
    kind ="news not annotated for spotlightFR"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?uri) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
        ?uri <http://purl.org/rss/1.0/title> ?title .
        ?uri <http://zone-project.org/model/plugins#lang> \"fr\" .
        OPTIONAL {?uri <http://zone-project.org/model/plugins/Spotlight> ?pluginDefined.  }
        FILTER (!bound(?pluginDefined)) }";

    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)


    ###############################################################
    kind ="news not annotated for spotlightEN"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?uri) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
        ?uri <http://purl.org/rss/1.0/title> ?title .
        ?uri <http://zone-project.org/model/plugins#lang> \"en\" .
        OPTIONAL {?uri <http://zone-project.org/model/plugins/Spotlight> ?pluginDefined.  }
        FILTER (!bound(?pluginDefined)) }";

    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    stats << {:kind => "", :val => ""}
    ###############################################################
    kind ="number of news"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
          ?concept RSS:title ?title.
          OPTIONAL { ?concept RSS:pubDateTime ?pubDateTime}.
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    if val == 0
      return
    end
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    kind ="news not annotated for spotlight"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_ITEMS}>
        WHERE {
          ?concept <http://purl.org/rss/1.0/title> ?title.
          ?concept <http://zone-project.org/model/plugins#lang> ?dep1
          OPTIONAL {?concept <http://zone-project.org/model/plugins/Spotlight> ?pluginDefined}
          FILTER (!bound(?pluginDefined))
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    stats << {:kind => "", :val => ""}
    ###############################################################
    kind ="number of sources"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_SOURCES}>
        WHERE {
          ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE}>.
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    kind ="number of twitter accounts"
    query = "PREFIX RSS: <http://purl.org/rss/1.0/>
        SELECT DISTINCT COUNT(?concept) AS ?num
        FROM <#{ZoneOntology::GRAPH_SOURCES}>
        WHERE {
          ?concept rdf:type <#{ZoneOntology::SOURCES_TYPE_TWITTER}>.
        }"
    val = store.query(query)[0][:num]
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    ###############################################################
    kind ="number of accounts"
    val = User.count
    stats << {:kind => kind, :val => val}
    StatHat::API.ez_post_value(kind,Rails.application.config.stathatId,val)

    return stats
  end
end