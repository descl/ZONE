module RssfeedHelper
  def removeFilterFromList(list, filter)
    puts list.class
    listCopy = list.clone
    return listCopy.delete_if{|element| element == filter}
  end
  
  def getButtonForElement(item)
    @OPEN_CALAIS_URI = 'http://www.opencalais.org/Entities#'
    @WIKI_META_URI = 'http://www.wikimeta.org/Entities#'
    @INSEE_GEO_URI = 'http://rdf.insee.fr/geo/'
    if(item[0].starts_with? @OPEN_CALAIS_URI)
      res= link_to item[0][@OPEN_CALAIS_URI.length,item[0].length], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-success"
      res+= link_to item[1], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-success"
    elsif(item[0].starts_with? @WIKI_META_URI)
      res=link_to item[0][@WIKI_META_URI.length,item[0].length], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-info"
      res+=link_to item[1][item[1].rindex('/')+1, item[1].length], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-info"
    elsif(item[0].starts_with? @INSEE_GEO_URI)
      res=link_to item[0][@INSEE_GEO_URI.length,item[0].length], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-warning"
      res+=link_to item[1][item[1].rindex('/')+1, item[1].length], rssfeed_index_path(:old => @filter,:new => {:type => item[0], :value => item[1]}), :class => "btn btn-warning"
    end
  end
end
