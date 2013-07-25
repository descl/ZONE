module ItemsHelper
  def getButtonForFilter(filter)
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    @RSS_URI = 'http://purl.org/rss/1.0/'
    @SVM_PLUGIN_URI = 'http://zone-project.org/model/plugins/Categorization_SVM#result'
    @TWITTER_MENTIONED_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#mentioned'
    @TWITTER_HASHTAG_PLUGIN_URI = 'http://zone-project.org/model/plugins/twitter#hashtag'

    @LABEL = "label label-inverse label-source"
    @LABEL_INFO = "label label-info label-tag"
    @LABEL_SUCCESS = "label label-success label-tag"
    @LABEL_WARNING = "label label-warning label-tag"
    @LABEL_IMPORTANT = "label label-important label-tag"
    @LABEL_TWITTER = "label label-twitter label-tag"

    if(filter.value == "null")
      filter.value = "/null"
    end
    itemURI = items_path(:old => @filters,:new => filter)
    if(filter.prop.starts_with? @OPEN_CALAIS_URI)
      itemClassFilter=getClassLabel(filter.prop[@OPEN_CALAIS_URI.length,filter.prop.length])
      #res= link_to filter.prop[@OPEN_CALAIS_URI.length,filter.prop.length], itemURI, :class =>  itemClassFilter
      res= link_to filter.value, itemURI, :class =>  itemClassFilter
    elsif(filter.prop.starts_with? @WIKI_META_URI)
      itemClassFilter=getClassLabel(filter.prop[@WIKI_META_URI.length,filter.prop.length])
      #res=link_to filter.prop[@WIKI_META_URI.length,filter.prop.length], itemURI, :class => itemClassFilter
      if(filter.value.rindex('/') == nil)
        res=link_to filter.value, itemURI, :class => itemClassFilter

      else
        res=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => itemClassFilter
      end
    elsif(filter.prop.starts_with? @INSEE_GEO_URI)
      itemClassFilter=getClassLabel(filter.prop[@INSEE_GEO_URI.length,filter.prop.length])
      #res=link_to filter.prop[@INSEE_GEO_URI.length,filter.prop.length], itemURI, :class => itemClassFilter
      res=link_to filter.value[filter.value.rindex('/')+1, filter.value.length], itemURI, :class => itemClassFilter
    elsif(filter.prop.start_with? @RSS_URI+"source")
      #itemClassFilter=getClassLabel(filter.prop[@RSS_URI.length,filter.prop.length])
      #res=link_to filter.prop[@RSS_URI.length,filter.prop.length], itemURI, :class => itemClassFilter
      #res=link_to filter.value, itemURI, :class => itemClassFilter, title: filter.prop[@RSS_URI.length,filter.prop.length] + " : " + filter.value
    elsif(filter.prop.start_with? @SVM_PLUGIN_URI)
      #res=link_to "category", itemURI, :class => @LABEL_INFO
      res=link_to filter.value, itemURI, :class => @LABEL_INFO
    elsif(filter.prop.start_with? @TWITTER_HASHTAG_PLUGIN_URI)
      res=link_to filter.value, itemURI, :class => @LABEL_TWITTER
    elsif(filter.prop.start_with? @TWITTER_MENTIONED_PLUGIN_URI)
      res=link_to filter.value, itemURI, :class => @LABEL_TWITTER
    end
  end

  def removeFilterFromList(list, filter)
    puts list.class
    listCopy = list.clone
    return listCopy.delete_if{|element| element == filter}
  end
  
  def getClassLabel(prop)
    if prop=="source"
      return @LABEL
    elsif prop=="Departement" or prop=="Pays" or prop=="Canton" or prop=="Pseudo-canton" or prop=="Arrondissement" or prop=="LOC" or prop=="Region" or prop=="LOC.ADMI"
      return @LABEL_WARNING
    elsif prop=="PERSON" or prop=="PERS.HUM"
      return @LABEL_SUCCESS
    elsif prop=="TIME"
      return @LABEL_IMPORTANT
    else
      return @LABEL_INFO
    end
  end
  
  def getTitleSource(link)
    return link[0,25] + "...";
  end
end
