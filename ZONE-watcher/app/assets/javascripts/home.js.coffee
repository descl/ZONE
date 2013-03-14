# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://jashkenas.github.com/coffee-script/
#//
#//  require jquery_ujs
//= require jquery-ui
//= require tag-it

$(document).ready ->
  #availableTags = ["ActionScript","AppleScript","Asp","BASIC"];
  #jQuery('#demo6').tagit({tagSource:availableTags, sortable:true});
    
  
  tags = ["Java", "Javascript", "Python", "C", "C++", "Ruby", "CSS", "HTML", "C#", "Visual Basic", "Prolog", "Smalltalk", "Scala", "Haskel", "Bash"]
  $("#languages-select").tagit
    tags: tags
    field: "language"
  
  $("#add-all-link").click (event) ->
    $.each tags, (i, e) ->
      $("#languages-select").tagit "addTag", e
  
    event.preventDefault()