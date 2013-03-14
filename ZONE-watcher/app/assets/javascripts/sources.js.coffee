$(document).ready ->
  jQuery ->
    $('.number_items_wait .icon-refresh').each ->

      myUri = $(this).data('url')
      $.ajax myUri,
        async: false
        success: (data) ->
          console.log(data)
          console.log($('[class=number_items_container][data-url="'+myUri+'"]'))
          $('[class=number_items_container][data-url="'+myUri+'"]').append(data)
          $('[class*=number_items_wait][data-url="'+myUri+'"]').detach()