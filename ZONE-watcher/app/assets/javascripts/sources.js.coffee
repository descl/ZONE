$(document).ready ->
  $('.number_items_wait .icon-refresh').each ->
    myUri = $(this).data('url')
    $.ajax myUri,
      async: true
      success: (data) ->
        $('[class=number_items_container][data-url="'+myUri+'"]').append(data)
        $('[class*=number_items_wait][data-url="'+myUri+'"]').detach()
          
  $(".accordion-toggle").click ->
    $(".accordion-toggle").not(this).children("i").addClass "icon-expand"
    $(".accordion-toggle").not(this).children("i").removeClass "icon-collapse"
    if $(this).children("i").hasClass("icon-expand")
      $(this).children("i").removeClass "icon-expand"
      $(this).children("i").addClass "icon-collapse"
    else if $(this).children("i").hasClass("icon-collapse")
      $(this).children("i").removeClass "icon-collapse"
      $(this).children("i").addClass "icon-expand"
       
  $('.showtable').click ->
    $(this).parents(".row-fluid").next(".tableSource").slideDown()
    $(this).next('.hidetable').show()
    $(this).hide()
   
    
  $('.hidetable').click ->
    $(this).parents(".row-fluid").next(".tableSource").slideUp()
    $(this).prev('.showtable').show()
    $(this).hide()