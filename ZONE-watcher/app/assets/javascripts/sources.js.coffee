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
  
  #Id de la ligne originale
  originalId = "#" + data
  
  #Id temporaire pour le nouvel tr
  tempId = $(originalId).attr("id") + "c"
  
  #Contenu du tr ( ensemble de ses td )
  trContent = $(originalId).html()
  
  #Url de la source
  sourceUrl = $(originalId).find("a.linkSource").html()
  
  #Url a appellé en ajax pour enregistrer la modification de theme
  urlUpdate = "sources/changeCategory?id=" + sourceUrl + "&theme=" + idTheme
  
  #Ajout de la ligne dans le nouveau tableau
  $(idTheme).append "<tr id='" + tempId + "' draggable='true' ondragstart='drag(event)'>" + trContent + "</tr>"
  $.ajax uri: urlUpdate
  
  #Remove the orignal line from the table where the drag first come from
  $(originalId).remove()
