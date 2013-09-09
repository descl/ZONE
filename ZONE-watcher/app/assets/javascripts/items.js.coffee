#= require items_show
#= require reminder_panel
#= require "dino.js"
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