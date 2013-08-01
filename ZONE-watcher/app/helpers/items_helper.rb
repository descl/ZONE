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

    if(filter.prop.starts_with? @WIKI_META_URI)
      if(filter.value.rindex('/') == nil)
        res=link_to filter.value, itemURI, :class => labels
      else
        res=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => labels
      end
    elsif(filter.prop.starts_with? @INSEE_GEO_URI)
      res=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => labels
    elsif( (filter.prop.start_with? @SVM_PLUGIN_URI) || (filter.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI) || (filter.prop.starts_with? @OPEN_CALAIS_URI))
      res=link_to filter.value, itemURI, :class => labels
    elsif(filter.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      res=link_to "@"+filter.value, itemURI, :class => labels
    end
    return res
  end
  
  def getTitleSource(link)
    return link[0,25] + "...";
  end
end
