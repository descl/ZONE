
module SearchesHelper

  def button_link(filter, kind,filterVal)



    if(filterVal != "" && filter.uri != "")


      tagUri = filter.uri.to_s

      title = filterVal
      if ( !(filterVal.start_with? "#") && ! (filterVal.start_with? "@"))

        itemUri = filter.item ? filter.item.uri : nil
        title += "<i class='icon-trash pull-right pointerMouse' title='Delete' onclick='deleteTag(\"#{filterVal}\",\"#{tagUri}\",\"#{itemUri}\")'></i>";
      end


      if (filterVal.size > 15) && (!filterVal.start_with? "<")
        filterVal = "#{filterVal[0,13]}..."
      end

      if(kind =="lang")
        filterVal = "<img src='/assets/langs/#{filterVal}.png' alt='#{filterVal}'>"
      end

      res= link_to raw(filterVal), "javascript:void(0);",
                   :class => "label label-tag label-#{kind}", "data-uri" => search_filters_path(:uri => filter.uri ),
                   "filter-uri" => filter.uri,
                   "filter-value" => filter.value,
                   "data-content"=>tag_content(filterVal,filter.uri),
                   "data-original-title"=> title
      return "<div class=\"btn-group btn-group-label btn-wrap\" title='#{printable_kind(kind,filterVal)}'>#{res}</div>"
    end
  end

  def printable_kind(kind,filterVal)
    printableKind = ""
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
      else
        printableKind = t('items.tags.other')
    end

    if (filterVal.size > 15) && (!filterVal.start_with? "<")
      printableKind = "#{filterVal} (#{printableKind})"
    end

    return printableKind
  end

  def tag_content(filterVal,filterUri)
    tagContent = ""

    waitingText = ""
    if filterUri != nil && (filterUri.include? "dbpedia.org")
      wait =  "<div class='row-fluid'><i class='icon-refresh'></i> Loading ...</div>";
      waitingText = "<div class='infoPopBox'><div class='infoPop' filter-uri=\""+filterUri+"\">"+wait+"</div></div>"
    end

    if(filterVal.start_with?"<img src")
      filterVal = filterVal[24..25]
    end

    btnMust =      "<button type='button' data-dismiss='clickover' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + filterVal + "\",\"#{filterUri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonMusthave')}</button><br>";
    btnOptionnal = "<button type='button' data-dismiss='clickover' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + filterVal + "\",\"#{filterUri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonOption')}</button><br>";
    btnBan =       "<button type='button' data-dismiss='clickover' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + filterVal + "\",\"#{filterUri}\");openReminderOnChange()'>#{t('newmodal.filtering.buttonBan')}</button>";
    tagContent = "<div style='width:276px'></div>#{waitingText}<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>#{t('items.popover.smalltitle')}</b></div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>";

    return tagContent
  end

end
