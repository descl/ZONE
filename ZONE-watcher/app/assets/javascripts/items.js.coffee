#= require items_show
#= require reminder_panel

$(document).ready ->
  $.get gon.uriForItemsNumber, (data) ->
    $('#number_items_wait').detach()
    $('#number_items_container').append(data)


  for id,uri of gon.gonItemsFiltersUri
    $.ajax uri,
      async: true
      context: id
      success: (data) ->
        id = $(this)[0]
        $('[class=item_container][sourceid="' + id + '"]').append(data)
        $('[class*=item_wait][sourceid="' + id + '"]').detach()

  
  #Waiting screen for the tag
  originalWaitingText = "<div class='infoPop'>"+getWaitingScreen()+"</div>"
  #Generation of the popover of the tag
  $(".label-tag").each ->
    #If the tag can have more info
    if ($(this).attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") is 0)
      waitingScreen = getWaitingScreen()
      $(this).popover
          title: getPopoverTitle($(this).html())
          content: originalWaitingText + getPopoverButton($(this).html())
          placement: "bottom"
          trigger: "manual"
    else
      $(this).popover
          title: getPopoverTitle($(this).html())
          content: getPopoverButton($(this).html())
          placement: "bottom"
          trigger: "manual"
        
  $('.label-tag').click ->
    #If popover visible and the the title is the same as the tag, nothing to do here
    if( $('.popover').is(':visible') && $('.titletag').html()==$(this).html())
      return
    #If the tag can have more info
    if ($(this).attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") is 0)
      popover = $(this).data('popover')
      errorText = "<div class='infoPop'>error</div>" + getPopoverButton($(this).html())
      #If popover got no loading screen and no error screen --> popover got info so no ajax call
      if (popover.options.content != (originalWaitingText+ getPopoverButton($(this).html())) && popover.options.content != errorText)
        return
      item = $(this)
      $.ajax
        url: item.attr("data-uri")
        timeout: 5000
        success: (data) ->
          #set the content of the popover of the tag (this do not refresh the content dynamically )
          popover.options.content= "<div class='infoPop'>"+data+"</div>" + getPopoverButton($(this).html())
          #Show the data to the user
          $('.infoPop').html(data)
        error: ->
          #set the content of the popover of the tag (this do not refresh the content dynamically )
          popover.options.content= errorText
          #Show the data to the user
          $('.infoPop').html('error')

  #Disable the default action onclick on the tag
  $(".label-tag").on "click", ->
    false

  $(".showFavorite").hover (->
    $(this).next(".row-favorite").fadeIn()
    $(this).hide()
  ), ->
    $(this).hide()

  $(".row-favorite").hover (->
    $(this).prev(".showFavorite").hide()
  ), ->
    $(this).hide()
    $(this).prev(".showFavorite").fadeIn()

getPopoverButton = (tagHtml) ->
  btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + tagHtml + "\",\"" + $(this).attr('filter-uri') + "\");closePop()'>"+ $("#titleOr").html() + "</button><br>"
  btnMust = "<button type='button' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + tagHtml + "\",\"" + $(this).attr('filter-uri') + "\");closePop()'>"+ $("#titleAnd").html() + "</button><br>"
  btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + tagHtml + "\",\"" + $(this).attr('filter-uri') + "\");closePop()'>"+ $("#titleWithout").html() + "</button>"
    
  tagContent = "<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>"+$("#titlePopover").html()+"</b></div></div><div class='row-fluid'><div class='span12'>"+  btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>"
  
  tagContent
  
getPopoverTitle = (tagHtml) ->
  titleTag = "<span class='titletag'>"+tagHtml + "</span><i class='icon-trash pull-right pointerMouse' title='Delete' onclick='deleteTag(\""+tagHtml+"\")'></i>"
  
  titleTag 
  
getWaitingScreen = ->
  text = "<div class='row-fluid'><i class='icon-refresh'></i> Loading ...</div>"
    
  text