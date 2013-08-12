# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

$(document).ready ->
  for id,uri of gon.gonItemsUri
    $.ajax uri,
      async: false
      dataType: "html"
      success: (data) ->
        $('[class=item_container][sourceid="'+id+'"]').append(data)
        $('[class*=item_wait][sourceid="'+id+'"]').detach()