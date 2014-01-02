getPopoverButton = (tag) ->
  name = tag.html()
  uri = tag.attr("filter-uri")
  btnBan = undefined
  btnMust = undefined
  btnOptionnal = undefined
  tagContent = undefined
  btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleOr").html() + "</button><br>"
  btnMust = "<button type='button' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleAnd").html() + "</button><br>"
  btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleWithout").html() + "</button>"
  tagContent = "<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>" + $("#titlePopover").html() + "</b></div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>"
  tagContent

getPopoverTitle = (tag, itemUri) ->
  tagHtml = tag.html().substr(0, 20)
  tagDelete = tagHtml
  tagUri = null
  tagUri = tag.attr("filter-uri")  unless tag.attr("filter-uri").indexOf("dbpedia.org") is -1
  res = "<span class='titletag'>" + tagHtml + "</span>"
  res += "<i class='icon-trash pull-right pointerMouse' title='Delete' onclick='deleteTag(\"" + tagHtml + "\",\"" + tagUri + "\",\"" + itemUri + "\")'></i>"  if tagHtml.indexOf("#") isnt 0 and tagHtml.indexOf("@") isnt 0
  res

getWaitingScreen = ->
  "<div class='row-fluid'><i class='icon-refresh'></i> Loading ...</div>"

$(document).ready ->
  #Generation of the popover of the tag
  generateTags()

(exports ? this).generateTags = () ->
  $(".label-tag").each ->

    #check if the tag is already "popoverised"
    return  if $(this).data("clickover")?

    #create the popover
    $(this).clickover placement: "bottom"

    #add the onclick action
    $(this).on "click", ->
      unless $(this).attr("filter-uri").indexOf("dbpedia.org") is -1
        popover = $(this).data("clickover")
        errorText = "<div class='infoPop'>error</div>" + getPopoverButton($(this))
        item = $(this)
        urlTag = item.attr("filter-uri")
        $.ajax
          url: item.attr("data-uri")
          context: urlTag
          timeout: 5000
          success: (data) ->
            $(".infoPop[filter-uri='" + urlTag + "']").hide()
            $(".infoPop[filter-uri='" + urlTag + "']").html data

            #truncate the dbpedia message
            $(".infoPop[filter-uri='" + urlTag + "']").find(".textContent").jTruncate
              length: 200
              minTrail: 0
              moreText: "(...)"
              lessText: "[-]"
              ellipsisText: ""
              moreAni: "fast"
              lessAni: "fast"
            $(".infoPop[filter-uri='" + urlTag + "']").show()

          error: ->
            $(".infoPop").html ""
