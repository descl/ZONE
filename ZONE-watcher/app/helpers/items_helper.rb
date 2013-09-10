module ItemsHelper
  def getButtonForFilter(filter)
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    @RSS_URI = 'http://purl.org/rss/1.0/'
    @SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
    @TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
    @TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'

    @LABEL_PEOPLE = "label label-success label-tag"
    @LABEL_PLACE = "label label-warning label-tag"
    @LABEL_TWITTER = "label label-twitter label-tag"
    @LABEL_OTHER = "label label-info label-tag"
    
    labels = @LABEL_OTHER

    if(filter.value == "null")
      filter.value = "/null"
    end
    itemURI = items_path(:old => @filters,:new => filter)
    
    filterval=""
    if(filter.prop.starts_with? @WIKI_META_URI)
      if(filter.value.rindex('/') == nil)
        filterval=filter.value
      else
        filterval=filter.value[filter.value.rindex('/')+1, filter.value.length]
      end
    elsif(filter.prop.starts_with? @INSEE_GEO_URI)
      filterval=filter.value[filter.value.rindex('/')+1, filter.value.length]
      labels = @LABEL_PLACE
    elsif(filter.prop.starts_with? ZoneOntology::PLUGIN_SOCIAL_ANNOTATION)
      filterval=filter.value
      labels = @LABEL_PLACE
    elsif( (filter.prop.start_with? @SVM_PLUGIN_URI) || (filter.prop.starts_with? @OPEN_CALAIS_URI))
      filterval=filter.value
    elsif (filter.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI)
      filterval=filter.value
      labels=@LABEL_TWITTER
    elsif(filter.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      filterval=filter.value
      labels=@LABEL_TWITTER
    end

    if(filterval != "")
      res=link_to filterval, itemURI, :class => labels, "data-uri" => search_filters_path(:uri => filter.uri ), "filter-uri" => filter.uri
    end
    return raw "<div class=\"btn-group btn-group-label btn-wrap\">#{res}</div>"
  end
  
  def getTitleSource(link)
    return link[0,25] + "...";
  end
end
