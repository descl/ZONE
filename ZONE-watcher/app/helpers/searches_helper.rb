
module SearchesHelper

  def button_link(filter, kind,filterVal)

    uri = filter.uri
    itemUri = filter.item.uri
    if(filterVal != "" && uri != "")
      waitingText = ""
      tagUri = uri
      if uri != nil && (uri.include? "dbpedia.org")
        wait =  "<div class='row-fluid'><i class='icon-refresh'></i> Loading ...</div>";
        waitingText = "<div class='infoPop'>"+wait+"</div>"
      end
      if uri == nil
        tagUri = ""
      end

      name = filterVal
      btnMust =      "<button type='button' data-dismiss='clickover' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + name + "\",\"#{uri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonMusthave')}</button><br>";
      btnOptionnal = "<button type='button' data-dismiss='clickover' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + name + "\",\"#{uri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonOption')}</button><br>";
      btnBan =       "<button type='button' data-dismiss='clickover' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + name + "\",\"#{uri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonBan')}</button>";
      tagContent = "#{waitingText}<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>#{t('items.popover.smalltitle')}</b></div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>";

      title = filterVal
      tagUri = uri
      if ( !(filterVal.start_with? "#") && ! (filterVal.start_with? "@"))
          title += "<i class='icon-trash pull-right pointerMouse' title='Delete' onclick='deleteTag(\"#{filterVal}\",\"#{tagUri}\",\"#{itemUri}\")'></i>";
      end

      case kind
        when "twitter"
          printableKind = t('items.tags.twitter')
        when "place"
          printableKind = t('items.tags.place')
        when "work"
          printableKind = t('items.tags.work')
        when "organization"
          printableKind = t('items.tags.organization')
        when "person"
          printableKind = t('items.tags.person')
        when "event"
          printableKind = t('items.tags.event')
        when "lang"
          printableKind = t('items.tags.lang')
          filterVal = "<img src='/assets/langs/#{filterVal}.png' alt='#{filterVal}'>"
      else
        printableKind = ""
      end

      res= link_to raw(filterVal), "javascript:void(0);",
                   :class => "label label-tag label-#{kind}", "data-uri" => search_filters_path(:uri => uri ),
                   "filter-uri" => uri,
                   "filter-value" => filter.value,
                   "data-content"=>tagContent,
                   "data-original-title"=> title
      return "<div class=\"btn-group btn-group-label btn-wrap\" title='#{printableKind}'>#{res}</div>"
    end
  end
end
