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

  $(".showTag").hide();
  #$(".btn-toolbar").hide();

  #action to show the tag on click
  $(".showTag").on "click", ->
    $(this).parent().next(".btn-toolbar").slideDown()
    $(this).hide()
    $(this).next(".hideTag").show()


  #Action to hide the tag on click
  $(".hideTag").on "click", ->
    $(this).parent().next(".btn-toolbar").slideUp()
    $(this).hide()
    $(this).prev(".showTag").show()


  #Generation of the popover of the tag
  btnOptionnal = ""
  btnMust = ""
  btnBan = ""
  $(".label-tag").each ->
    btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + $(this).html() + "\");closePop()'>" + $("#titleOr").html() + "</button><br>"
    btnMust = "<button type='button' class='btn btn-success span12 btnTag' onclick='addTag(\"must\",\"" + $(this).html() + "\");closePop()'>" + $("#titleAnd").html() + "</button><br>"
    btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + $(this).html() + "\");closePop()'>" + $("#titleWithout").html() + "</button>"
    $(this).popover
      title: $("#titlePopover").html()
      content: "<div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>"
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
