# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/


$(document).ready ->
  $.get gon.filter, (data) ->
    $('#number_items_wait').detach()
    $('#number_items_container').append(data)
