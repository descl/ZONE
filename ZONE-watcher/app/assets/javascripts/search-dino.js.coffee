
$ ->
  $("#keyword").focus();
  if($("#keyword").val() != "")
    $('#go-button').removeClass('hidden');

  #Instanciate the autocomplete for semantic search
  $("#keyword").autocomplete(
    source: [""]
    search: (event, ui) ->
      $("#keyword").autocomplete source: "/complete_entities/" + $("#keyword").val() + ".json"

    select: (event, ui) ->
      $("#keyword").attr "filter-uri", ui.item.uri

    minLength: 1
  ).autocomplete("widget").addClass "span1"




(exports ? this).movingDataFromIndex = ->
  $("#go-search-button").html '<i class="icon-refresh icon-refresh-animate"></i>'
  window.onbeforeunload = ""
  $("#movedData").html ""
  source = {}
  filtering = {}
  item = ""
  url = ""
  tabAnd = []

  tabItem = {}
  tabItem.value = $("#keyword").val()
  url = $("#keyword").attr("filter-uri")
  tabItem.kind = "dbpedia"
  tabItem.uri = url  if url isnt "" and url isnt "undefined"
  tabAnd.push tabItem

  source.twitter = []
  source.rss = []
  filtering.or = []
  filtering.and = tabAnd
  filtering.without = []
  inputDatas = "<input name='sources' type='hidden' value='" + JSON.stringify(source) + "'>" + "<input name='filters' type='hidden' value='" + JSON.stringify(filtering) + "'><input name='itemId' type='hidden' value=''>"
  $("#movedData").html inputDatas
  $(".formModal").submit()
