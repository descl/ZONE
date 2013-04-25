$(document).ready ->
  $('.number_items_wait .icon-refresh').each ->
    myUri = $(this).data('url')
    $.ajax myUri,
      async: true
      success: (data) ->
        $('[class=number_items_container][data-url="'+myUri+'"]').append(data)
        $('[class*=number_items_wait][data-url="'+myUri+'"]').detach()
