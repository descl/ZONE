module ItemsHelper
  def getButtonForFilter(filter)
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    @RSS_URI = 'http://purl.org/rss/1.0/'
    @SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
    @TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
    @TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'

    if(filter.value == "null")
      filter.value = "/null"
    end
    itemURI = items_path(:old => @filters,:new => filter)
    if(filter.prop.starts_with? @OPEN_CALAIS_URI)
      res= link_to filter.prop[@OPEN_CALAIS_URI.length,filter.prop.length], itemURI, :class => "label label-success"
      res+= link_to filter.value, itemURI, :class => "label label-success"
    elsif(filter.prop.starts_with? @WIKI_META_URI)
      res=link_to filter.prop[@WIKI_META_URI.length,filter.prop.length], itemURI, :class => "label label-info"
      if(filter.value.rindex('/') == nil)
        res+=link_to filter.value, itemURI, :class => "label label-info"

      else
        res+=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => "label label-info"
      end
    elsif(filter.prop.starts_with? @INSEE_GEO_URI)
      res=link_to filter.prop[@INSEE_GEO_URI.length,filter.prop.length], itemURI, :class => "label label-warning"
      res+=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => "label label-warning"
    elsif(filter.prop.start_with? @RSS_URI+"source")
      res=link_to filter.prop[@RSS_URI.length,filter.prop.length], itemURI, :class => "label"
      res+=link_to filter.value, itemURI, :class => "label"
    elsif(filter.prop.start_with? @SVM_PLUGIN_URI)
      res=link_to "category", itemURI, :class => "label label-important"
      res+=link_to filter.value, itemURI, :class => "label label-important"
    elsif(filter.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI)
      res=link_to "#"+filter.value, itemURI, :class => "btn-auth  btn-twitter"
    elsif(filter.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      res=link_to "@"+filter.value, itemURI, :class => "btn-auth btn-twitter"
    end
  end

  def removeFilterFromList(list, filter)
    puts list.class
    listCopy = list.clone
    return listCopy.delete_if{|element| element == filter}
  end
end
