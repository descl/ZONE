# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

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
      error: (xhr, ajaxOptions, thrownError) ->
        alert(xhr.status)
        alert(xhr.statusText)
        alert(thrownError)