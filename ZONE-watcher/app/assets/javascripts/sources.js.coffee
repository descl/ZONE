$(document).ready ->
  $('.number_items_wait .icon-refresh').each ->
    myUri = $(this).data('url')
    $.ajax myUri,
      async: true
      success: (data) ->
        $('[class=number_items_container][data-url="'+myUri+'"]').append(data)
        $('[class*=number_items_wait][data-url="'+myUri+'"]').detach()
          
  $("#btn_up").click ->
    $("html,body").animate
      scrollTop: 0
    , "slow"
  
  $(window).scroll ->
    if $(window).scrollTop() < 50
      $("#btn_up").fadeOut()
    else
      $("#btn_up").fadeIn()
      $("#btn_up").css("display","block")