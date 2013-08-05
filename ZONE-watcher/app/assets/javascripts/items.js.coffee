#= require items_show
#= require reminder_panel

$(document).ready ->
  $.get gon.uriForItemsNumber, (data) ->
    $('#number_items_wait').detach()
    $('#number_items_container').append(data)


$(document).ready ->
  for id,uri of gon.gonItemsFiltersUri
    $.ajax uri,
      async: false
      dataType: "html"
      success: (data) ->
        $('[class=item_container][sourceid="'+id+'"]').append(data)
        $('[class*=item_wait][sourceid="'+id+'"]').detach()





$(document).ready ->

  #Hide the favorite bar by default
  $(".row-favorite").hide()
  $(".showFavorite").hide()
  $(".row-list").show()

  #Generation of the popover of the tag
  $(".label-tag").each ->
    btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + $(this).html() + "\");closePop()'>"+ $("#titleOr").html() + "</button><br>"
    btnMust = "<button type='button' class='btn btn-success span12 btnTag' onclick='addTag(\"must\",\"" + $(this).html() + "\");closePop()'>"+ $("#titleAnd").html() + "</button><br>"
    btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + $(this).html() + "\");closePop()'>"+ $("#titleWithout").html() + "</button>"
    
    titleTag = "<span class='titletag'>"+$(this).html() + "</span><i class='icon-remove-sign pull-right' title='Delete' onclick='deleteTag($(this))'></i><i class='icon-edit pull-right' title='Edit'></i>"
    
    if ($(this).attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") is 0)
      item = $(this)
      $.ajax
        url: item.attr("data-uri")
        success: (data) ->
          item.popover
            title: titleTag
            content: "<div class='row-fluid'><div class='span12'>"+data+"</div></div><div class='row-fluid'><div class='span12'>"+  btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>"
            placement: "bottom"
    else
       $(this).popover
            title: titleTag
            content: "<div class='row-fluid'><div class='span12'>"+  btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>"
            placement: "bottom"



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
