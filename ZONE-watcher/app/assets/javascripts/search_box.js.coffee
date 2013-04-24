//=  require jquery
//=  require jquery_ujs
//= require jquery-ui
//= require tag-it


$ ->

  #display sources list if used
  if($('#sources-list > *').length > 0)
    $('.select-source-input').css "display", 'block'

  tags = []
  $.ajax '/filters.json',
         success  : (data, status, xhr) ->
           for elem in data
             tags.push(elem['value']+" | "+elem['prop'])

         $("#search-form").tagit
           tags: tags
           field: "tags[]"


  sources = []
  $.ajax '/sources.json',
         success  : (data, status, xhr) ->
           for elem in data
             sources.push(elem['value'])

           $("#sources-list").tagit
             tags: sources
             field: "sources[]"



  #handle click on "select sources"
  $('.select-sources').click ->
    if $('.select-source-input').is(':visible')
      $('.select-source-input').css "display", 'none'
    else
      $('.select-source-input').css "display", 'block'




