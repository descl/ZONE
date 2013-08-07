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
    $(this).parents(".row-fluid").next(".tableSource").slideDown()
    $(this).next('.hidetable').show()
    $(this).hide()
   
    
  $('.hidetable').click ->
    $(this).parents(".row-fluid").next(".tableSource").slideUp()
    $(this).prev('.showtable').show()
    $(this).hide()