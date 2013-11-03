#= require items_show
#= require reminder_panel
# require "dino.js"

downloadNewsDatas = (uri) ->
  $.ajax uri,
    async: true
    context: uri
    success: (data) ->
      item = $('.item-bloc[local-uri="'+uri+'"]')
      if(item.length > 0)
        $(item).removeAttr("local-uri")
        $(item).find('[class*=item_wait]').detach()
        $(item).find('[class=item_container]').append(data)

    error: (xhr, ajaxOptions, thrownError) ->
      if (xhr.status == 500)
        downloadNewsDatas(uri)

$ ->
  #load items content for each item
  $('.item-bloc[local-uri]').each (id, element) ->
    localUri = $(element).attr('local-uri')
    downloadNewsDatas(localUri)

  $(".showFavorite").hover (->
    $(this).next(".row-favorite").fadeIn()
    $(this).hide()
  ), ->
    $(this).hide()

  $(".row-favorite").hover (->
    $(this).prev(".showFavorite").hide()
  ), ->
    $(this).hide()
    $(this).prev(".showFavorite").fadeIn()

  #activate the infinite scroll
  $('a.load-more-items').on 'inview', (e, visible) ->
    return unless visible
    $.getScript $(this).attr('href')