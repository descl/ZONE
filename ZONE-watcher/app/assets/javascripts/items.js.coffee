#= require items_show
#= require reminder_panel
# require "dino.js"

$ ->
  #load items content for each item
  $('.item-bloc[local-uri]').each (id, element) ->
    localUri = $(element).attr('local-uri')
    downloadNewsDatas(localUri)

  #activate the infinite scroll
  $('a.load-more-items').on 'inview', (e, visible) ->
    return unless visible
    $.getScript $(this).attr('href')


(exports ? this).downloadNewsDatas = (uri) ->
  $.ajax uri,
    async: true
    context: uri
    success: (data) ->
      item = $('.item-bloc[local-uri="'+uri+'"]')
      if(item.length > 0)
        $(item).removeAttr("local-uri")
        $(item).find('[class*=item_wait]').detach()
        $(item).find('[class=item_container]').append(data)
        setHighlightTags($(item))

    error: (xhr, ajaxOptions, thrownError) ->
      if (xhr.status == 500)
        downloadNewsDatas(uri)
(exports ? this).setHighlightTags = (item) ->
  curSearch = getCurSearch()

  #we get all the filters
  filtersSearch = new Array();
  curSearch.find("#summaryOr,#summaryAnd").children().each (id,fi) ->
    val = $(fi).attr("filter-uri")
    if(val == "undefined")
      val = $(fi).attr("filter-value")
    filtersSearch.push(val)

  #we check filters in the news
  $(item).find(".label-tag").each (id,filterItem) ->
    filterval = $(filterItem).attr("filter-uri")
    if(filterval == undefined)
      filterval = $(filterItem).attr("filter-value")
    if ($.inArray(filterval,filtersSearch) != -1)
      $(filterItem).addClass("label-selected")


(exports ? this).getCurSearch = () ->
  return $($(".searchItem.active").attr("data-content"))