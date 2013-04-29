# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/
#//

$ ->
  $('.more-infos').click ->
    if $('.infos').is(':visible')
      $('.splash').css "position", 'absolute'
      $('.infos').hide();
    else
      $('.infos').show();
      $('.splash').css "position", 'relative'