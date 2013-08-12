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
      
#Functions for the draggable source
window.dropSource = (ev, idTheme) ->
  ev.preventDefault()
  data = ev.dataTransfer.getData("Text")
  
  #Original line id
  originalId = "#" + data
  
  #Temp ID for the new <tr>
  tempId = $(originalId).attr("id") + "c"
  
  #Content of the <tr> ( all his <td> )
  trContent = $(originalId).html()
  
  #Source URL
  sourceUrl = $(originalId).find("a.linkSource").html()
  
  theme= idTheme
  theme ="" if theme =="undefined"
  
  #URL to call via ajax to save the update
  updateUrl = "/sources/changeCategory?id=" + encodeURI(sourceUrl) + "&theme=" + encodeURI(theme)
  
  #ID of the new table
  idTheme = "#"+idTheme
  
  #Add the <tr> with his <td> to the new table
  $(idTheme).append "<tr id='" + tempId + "' draggable='true' ondragstart='drag(event)'>" + trContent + "</tr>"
  
  $.ajax
    url: updateUrl,
    success: (data) ->
      $("#"+tempId).addClass("success")
      setTimeout (->
        $("#"+tempId).removeClass("success")
      ),2000
    error: (jqXHR, textStatus, errorThrown) ->
      $("#"+tempId).addClass("error")
      setTimeout (->
        $("#"+tempId).removeClass("error")
      ),2000
  
  #Remove the orignal line from the table where the drag first come from
  $(originalId).remove()
