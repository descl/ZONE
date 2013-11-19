#= require items_show
#= require reminder_panel
# require "dino.js"

#= require masonry/jquery.masonry
#= require masonry/jquery.event-drag
#= require masonry/jquery.imagesloaded.min
#= require masonry/jquery.infinitescroll.min
#= require masonry/modernizr-transitions

$ ->
  #load items content for each item
  $('.item-bloc[data-local-uri]').each (id, element) ->
    localUri = $(element).attr('data-local-uri')
    downloadNewsDatas(localUri)

  setInterval(getNewNews,30000)
  setInterval(updateLayout,1000)


  $container = $('#masonry-container');

  $container.masonry
    itemSelector: '.item-bloc',
    gutterWidth: 0,
    columnWidth: 40

  #infinite scroll managment
  $container.infinitescroll
    navSelector: "#page-nav" # selector for the paged navigation
    nextSelector: "#page-nav a" # selector for the NEXT link (to page 2)
    itemSelector: ".item-bloc" # selector for all items you'll retrieve
    loading:
      finishedMsg: "No more pages to load."
      img: "http://i.imgur.com/6RMhx.gif"

    # trigger Masonry as a callback
    (newElements) ->
      # hide new items while they are loading
      $newElements = $(newElements).css(opacity: 0)
      $.getScript $(newElements).attr('href')

(exports ? this).updateLayout = () ->
  $('#masonry-container').masonry('reload')

#functions declaration
(exports ? this).getNewNews = () ->
  lastNews = $('.items-box').children().first()
  lastNewsDate = lastNews.attr("data-pub-date")

  searchId = getCurSearch().attr("id").substr(5)
  uri = "/search/"+searchId+"/getNewsNumber/"+lastNewsDate

  $.ajax
    url: uri
    context: uri
    timeout: 5000
    success: (data) ->
      if data != "0"
        $(".numberOfNewNews").html(data)
        $(".newNewsLinkBox").show()
    error: ->
      console.log("error in fetching new news")

(exports ? this).downloadNewsDatas = (uri,item=null) ->
  $.ajax uri,
    async: true
    context: uri
    success: (data) ->
      if item == null
        item = $('.item-bloc[data-local-uri="'+uri+'"]')
      if(item.length > 0)
        $(item).removeAttr("data-local-uri")
        $(item).find('[class*=item_wait]').detach()
        $(item).find('[class=item_container]').append(data)
        setHighlightTags($(item))
        $('#masonry-container').masonry('reload');

    error: (xhr, ajaxOptions, thrownError) ->
      if (xhr.status == 500)
        downloadNewsDatas(uri,item)
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