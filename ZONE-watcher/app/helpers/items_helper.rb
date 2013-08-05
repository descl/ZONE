module ItemsHelper
  def getButtonForFilter(filter)
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    @RSS_URI = 'http://purl.org/rss/1.0/'
    @SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
    @TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
    @TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'

    labels = "label label-twitter label-tag"

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
    elsif( (filter.prop.start_with? @SVM_PLUGIN_URI) || (filter.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI) || (filter.prop.starts_with? @OPEN_CALAIS_URI))
      filterval=filter.value
    elsif(filter.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      filterval="@"+filter.value
    end

    if(filterval != "")
      res=link_to filterval, itemURI, :class => labels, "data-uri" => search_filters_path(:uri => filter.uri ), "filter-uri" => filter.uri
    end
    return res
  end
  
  def getTitleSource(link)
    return link[0,25] + "...";
  end
end
