#TODO: bad hack, this file is a copy of app/assets/javascripts/items.js.coffee

downloadNewsDatas = (uri,incr=0) ->
  if(incr > 10)
    return
  $.ajax
    url: uri,
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
        downloadNewsDatas(uri,incr+1)


$ ->
  #insert the new items
  $('.items-list').append('<%= escape_javascript(render :partial => "items", :items => @items) %>');

  #remove the more items links if no more items to print
  if "<%=@items.next_page%>" == ""
    $('a.load-more-items').remove();
  else
    $('a.load-more-items').attr('href', '<%=items_path :page => @items.next_page, :search => @search.id  %>');

  #load items content for each item
  $('.item-bloc[local-uri]').each (id, element) ->
    localUri = $(element).attr('local-uri')
    downloadNewsDatas(localUri)
