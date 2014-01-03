#= require items_show
#= require reminder_panel
#= require tags_managment
# require "dino.js"

#= require masonry/jquery.masonry
#= require masonry/jquery.event-drag
#= require masonry/jquery.imagesloaded.min
#= require masonry/jquery.infinitescroll.min
#= require masonry/modernizr-transitions

CURRENT_MIN_DATE = null
CURRENT_MAX_DATE = null

INFINITE_SCROLL_MAX_DATE = 0
INFINITE_SCROLL_NEXT_PAGE = 1

$ ->
  now = new Date()
  now.setHours(23,59,0,0)

  CURRENT_MIN_DATE = new Date()
  CURRENT_MIN_DATE.setMonth(-1)
  CURRENT_MIN_DATE.setHours(0,0,0,0)
  CURRENT_MAX_DATE = new Date()
  CURRENT_MAX_DATE.setHours(23,59,0,0)


  railsMin = parseInt($("#initialMINDate").html(),10)
  railsMax = parseInt($("#initialMAXDate").html(),10)
  CURRENT_MIN_DATE = new Date(railsMin) if(railsMin != 0)
  if(railsMax != 0)
    CURRENT_MAX_DATE = new Date(railsMax)

  months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];

  $("#slider").dateRangeSlider
    valueLabels:"change",
    delayOut: 4000
    wheelMode: "scroll"
    step:
      days: 1
    bounds:
      min: new Date(2013 ,9,5)
      max: now
    defaultValues:
      min: CURRENT_MIN_DATE
      max: CURRENT_MAX_DATE

    scales: [
      first: (value) -> value
      end  : (value) -> value
      label: (value) -> months[value.getMonth()]

      next: (value) ->
        next = new Date(value)
        new Date(next.setMonth(value.getMonth() + 1))
    ]
  $("#slider").bind "valuesChanged", (e, data) ->
    minDate = data.values.min
    maxDate = data.values.max

    minDate.setHours(0,0,0,0)
    maxDate.setHours(23,59,0,0)

    reloadTagsCloud(minDate, maxDate)
    updateNewsForNewDate(minDate,maxDate)

    CURRENT_MIN_DATE = minDate
    CURRENT_MAX_DATE = maxDate

  #load items content for each item
  $('.item-bloc[data-local-uri]').each (id, element) ->
    localUri = $(element).attr('data-local-uri')
    downloadNewsDatas(localUri)

  setInterval(getNewNews,30000)
  setInterval(updateLayout,1000)

  $('#masonry-container').masonry
    itemSelector: '.item-bloc',
    gutterWidth: 20,
    columnWidth: 40,
    isFitWidth: true,

  setTimeout ( ->
    #infinite scroll managment
    $('#masonry-container').infinitescroll
      navSelector: "#page-nav" # selector for the paged navigation
      nextSelector: "#page-nav a:first" # selector for the NEXT link (to page 2)
      itemSelector: ".item-bloc" # selector for all items you'll retrieve
      prefill: true #load other pages if too big window
      debug: false
      path: (page) ->
        INFINITE_SCROLL_NEXT_PAGE++
        newPath = $(".load-more-items").uri().removeSearch(["page","maxDate"]).addSearch(page: INFINITE_SCROLL_NEXT_PAGE, maxDate: INFINITE_SCROLL_MAX_DATE)
        newPath.resource()

      loading:
        finishedMsg: "No more pages to load."
        img: "http://i.imgur.com/6RMhx.gif"

      # trigger Masonry as a callback
      (newElements,opts) ->
        # hide new items while they are loading
        $(newElements).css(opacity: 0)
        nextPath=opts.path[0]+opts.state.currPage
        $.getScript nextPath
  ),3000


(exports ? this).reloadTagsCloud = (minDate,maxDate) ->
  $.ajax
    url: "/searches/tagsCloud/"+getCurSearchId()+"/"+minDate.getTime()+"/"+maxDate.getTime()+".json"
    async: true
    dataType : 'json'
    success: (data) ->
      $("#cloudZone").html("<div class='background-dino'></div>")
      $("#cloudZone").jQCloud(data, {delayedMode:false, shape: "circle", afterWordRender: setTimeout('generateTags()',200)})
    error: (xhr, ajaxOptions, thrownError) ->
      console.log(xhr)
      console.log(thrownError)

(exports ? this).updateLayout = () ->
  $('#masonry-container').masonry('reload')
  Arrow_Points()

#functions declaration
(exports ? this).getNewNews = () ->
  if(INFINITE_SCROLL_MAX_DATE == 0)
    lastNews = $('.items-box').children(".item-bloc").first()
    lastNewsDate = lastNews.attr("data-pub-date")

    searchId = getCurSearchId()
    uri = "/search/"+searchId+"/getNewsNumber/"+lastNewsDate

    $.ajax
      url: uri
      async: true
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

Arrow_Points = ->
  if (getPageFormat() != "time")
    return
  s = $("#masonry-container").find(".item-bloc")
  s.each (i, obj) ->
    posLeft = $(obj).css("left")

    date = $(obj).attr("data-pub-date-string")
    $(obj).find(".arrowAcronym").remove()

    acronym = "<acronym class='arrowAcronym' title="+date+" style='border-bottom:dotted 1px black; cursor:help;'>"
    if posLeft is  "5px"
      html = acronym+"<span class='rightCorner'></span></acronym>"
      $(obj).prepend html
    else
      html = acronym+"<span class='leftCorner'></span></acronym>"
      $(obj).prepend html


(exports ? this).getCurSearch = () ->
  return $($(".searchItem.active").attr("data-content"))

(exports ? this).getCurSearchId = () ->
    return getCurSearch().attr("id").substr(5)

(exports ? this).getPageFormat =->
  idSelected = $(".btn-format.active").attr("id")
  switch idSelected
    when "btnFormatTime" then return "time"
    when "btnFormatCard" then return "card"
    when "btnFormatList" then return "list"

(exports ? this).updateNewsForNewDate = (startDate, endDate) ->
  console.log(startDate.getTime())
  console.log(endDate.getTime())
  console.log(CURRENT_MAX_DATE.getTime())
  console.log(endDate)
  console.log(CURRENT_MAX_DATE)
  if(endDate.getTime() != CURRENT_MAX_DATE.getTime())
    console.log("we need to update the pruinted articles")

    INFINITE_SCROLL_MAX_DATE = endDate.getTime()
    INFINITE_SCROLL_NEXT_PAGE = 0
    #$(".load-more-items").uri().removeSearch("page").removeSearch("maxDate").addSearch("maxDate",endDate.getTime())


    $('.newNewsLinkBox').hide();
    $('#masonry-container').masonry('remove',$(".item-bloc"));
    $('#masonry-container').masonry('reloadItems');
    $('#masonry-container').masonry('reload');
    $('#masonry-container').infinitescroll('scroll')
