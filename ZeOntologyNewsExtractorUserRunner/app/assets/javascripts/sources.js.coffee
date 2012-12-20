# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/

$(document).ready ->
  for uri, filter of gon.filters
    myUri = uri
    $.ajax filter,
      async: false
      success: (data) ->
        $('[class=number_items_container][sourceid="'+myUri+'"]').append(data)
        $('[class*=number_items_wait][sourceid="'+uri+'"]').detach()
