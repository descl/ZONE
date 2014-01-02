#= require items_show
#= require reminder_panel
#= require tags_managment
# require "dino.js"

#= require masonry/jquery.masonry
#= require masonry/jquery.event-drag
#= require masonry/jquery.imagesloaded.min
#= require masonry/jquery.infinitescroll.min
#= require masonry/modernizr-transitions

$ ->

  now = new Date();
  startDay = new Date(now)
  startDay.setMonth(-1)
  console.log(now)
  console.log(startDay)

  months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];

  $("#slider").dateRangeSlider
    valueLabels:"change",
    delayOut: 4000
    wheelMode: "scroll"
    step:
      days: 1
    bounds:
      min: new Date(2013 , 5,1)
      max: now
    defaultValues:
      min: startDay
      max: now

    scales: [
      first: (value) ->
        value

      end: (value) ->
        value

      next: (value) ->
        next = new Date(value)
        new Date(next.setMonth(value.getMonth() + 1))

      label: (value) ->
        months[value.getMonth()]

    ]
  $("#slider").bind "valuesChanged", (e, data) ->
    reloadTagsCloud(data.values.min, data.values.max)


  #load items content for each item
  $('.item-bloc[data-local-uri]').each (id, element) ->
    localUri = $(element).attr('data-local-uri')
    downloadNewsDatas(localUri)

  setInterval(getNewNews,30000)
  setInterval(updateLayout,1000)


  $container = $('#masonry-container');

  $container.masonry
    itemSelector: '.item-bloc',
    gutterWidth: 20,
    columnWidth: 40,
    isFitWidth: true,

  setTimeout ( ->
    #infinite scroll managment
    $container.infinitescroll
      navSelector: "#page-nav" # selector for the paged navigation
      nextSelector: "#page-nav a:first" # selector for the NEXT link (to page 2)
      itemSelector: ".item-bloc" # selector for all items you'll retrieve
      prefill: true #load other pages if too big window
      loading:
        finishedMsg: "No more pages to load."
        img: "http://i.imgur.com/6RMhx.gif"

      # trigger Masonry as a callback
      (newElements,opts) ->
        # hide new items while they are loading
        $newElements = $(newElements).css(opacity: 0)
        nextPath=opts.path[0]+opts.state.currPage
        $.getScript nextPath
  ),3000


(exports ? this).reloadTagsCloud = (minDate,maxDate) ->
  minDate.setHours(0,0)
  maxDate.setHours(23,59)
  console.log "Values just changed. min: " + minDate + " max: " + maxDate
  $.ajax
    url: "/searches/tagsCloud/"+getCurSearchId()+"/"+minDate.getTime()+"/"+maxDate.getTime()+".json"
    async: true
    dataType : 'json'
    success: (data) ->
      $("#cloudZone").html("<div class='background-dino'></div>")
      $("#cloudZone").jQCloud(data, {delayedMode:false, shape: "circle", afterWordRender: setTimeout('generateTags()',200)})
      generateTags()
    error: (xhr, ajaxOptions, thrownError) ->
      console.log(xhr)
      console.log(thrownError)

(exports ? this).updateLayout = () ->
  $('#masonry-container').masonry('reload')
  Arrow_Points()

#functions declaration
(exports ? this).getNewNews = () ->
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
