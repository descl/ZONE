$(document).ready ->
  $('.number_items_wait .icon-refresh').each ->
    myUri = $(this).data('url')
    $.ajax myUri,
      async: true
      success: (data) ->
        $('[class=number_items_container][data-url="'+myUri+'"]').append(data)
        $('[class*=number_items_wait][data-url="'+myUri+'"]').detach()
          
  $(".accordion-toggle").click ->
    $(".accordion-toggle").not(this).children("i").addClass "icon-chevron-sign-right"
    $(".accordion-toggle").not(this).children("i").removeClass "icon-chevron-sign-down"
    if $(this).children("i").hasClass("icon-chevron-sign-right")
      $(this).children("i").removeClass "icon-chevron-sign-right"
      $(this).children("i").addClass "icon-chevron-sign-down"
    else if $(this).children("i").hasClass("icon-chevron-sign-down")
      $(this).children("i").removeClass "icon-chevron-sign-down"
      $(this).children("i").addClass "icon-chevron-sign-right"
       
  $('.showtable').click ->
    $(this).parents(".row-fluid").next(".tableSource").slideDown 'swing', ->
      checkHideAll()
    $(this).next('.hidetable').show()
    $(this).hide()
   
    
  $('.hidetable').click ->
    $(this).parents(".row-fluid").next(".tableSource").slideUp 'swing', ->
      checkHideAll()
    $(this).prev('.showtable').show()
    $(this).hide()
    checkHideAll()
    
  $('#hideAllSources').click ->
    $(".tableSource").slideUp 'swing' , ->
      checkHideAll()
    $('.hidetable').hide()
    $('.showtable').show()
    
  $('#showAllSources').click ->
    $(".tableSource").slideDown 'swing' , ->
      checkHideAll()
    $('.hidetable').show()
    $('.showtable').hide()
    
checkHideAll = ->
  showHide = false
  $(".tableSource").each ->
    showHide = true  if $(this).is(':visible')

  if showHide
    $("#showAllSources").fadeOut 'swing', ->
      $("#hideAllSources").fadeIn()
  else
    $("#hideAllSources").fadeOut 'swing', ->
      $("#showAllSources").fadeIn()