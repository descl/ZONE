#= require items_show
#= require reminder_panel
#= require "dino.js"
$(document).ready ->
  $.get gon.uriForItemsNumber, (data) ->
    $('#number_items_wait').detach()
    $('#number_items_container').append(data)


  downloadNewsDatas = (id,uri) ->
    console.log(id)
    $.ajax uri,
      async: true
      context: id
      success: (data) ->
        $('[class*=item_wait][sourceid="' + id + '"]').detach()
        $('[class=item_container][sourceid="' + id + '"]').append(data)

      error: (xhr, ajaxOptions, thrownError) ->
        if (xhr.status == 500)
          downloadNewsDatas(id,uri)



  for id,uri of gon.gonItemsFiltersUri
    downloadNewsDatas(id,uri)

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