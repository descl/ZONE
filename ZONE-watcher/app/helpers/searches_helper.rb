module SearchesHelper
  def button_link(uri, labels,filterval)
    if(filterval != "" && uri != "")
      res= link_to filterval, "javascript:void(0);", :class => labels, "data-uri" => search_filters_path(:uri => uri ), "filter-uri" => uri
      return "<div class=\"btn-group btn-group-label btn-wrap\">#{res}</div>"
    end
  end
end
