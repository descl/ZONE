$(document).ready(function() {
    //Waiting screen for the tag
    originalWaitingText = "<div class='infoPop'>"+getWaitingScreen()+"</div>"
    //Generation of the popover of the tag
    $(".label-tag").each(function() {
        if( $(this).data('popover') == null){
            var waitingScreen;
            var itemUri = $(this).parent().parent().parent().children(".titleItem").attr("href");
            if ($(this).attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") === 0) {
                waitingScreen = getWaitingScreen();
                $(this).popover({
                    title: getPopoverTitle($(this),itemUri),
                    content: originalWaitingText + getPopoverButton($(this)),
                    placement: "bottom",
                    trigger: "manual"
                });
            } else {
                $(this).popover({
                    title: getPopoverTitle($(this),itemUri),
                    content: getPopoverButton($(this)),
                    placement: "bottom",
                    trigger: "manual"
                });
            }
        }
    });



    $(".label-tag").on("click", function() {
        return false;
    });
    $('.label-tag').on("click", function() {
        $('.label-tag').not(this).popover('hide');
        $(this).popover('toggle');

        if ($('.popover').is(':visible') && $('.titletag').html() === $(this).html()) {
            return;
        }
        if ($(this).attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") === 0) {
            var popover = $(this).data('popover');
            errorText = "<div class='infoPop'>error</div>" + getPopoverButton($(this));
            if (popover.options.content !== (originalWaitingText + getPopoverButton($(this))) && popover.options.content !== errorText) {
                return;
            }
            var item = $(this);
            return $.ajax({
                url: item.attr("data-uri"),
                timeout: 5000,
                success: function(data) {
                    popover.options.content = "<div class='infoPop'>" + data + "</div>" + getPopoverButton($(this));
                    return $('.infoPop').html(data);
                },
                error: function() {
                    popover.options.content = errorText;
                    return $('.infoPop').html('error');
                }
            });
        }
    });
});


function getPopoverButton(tag) {
    var name = tag.html();
    var uri = tag.attr('filter-uri');
    var btnBan, btnMust, btnOptionnal, tagContent;
    btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + name + "\",\"" + uri + "\");closePop()'>" + $("#titleOr").html() + "</button><br>";
    btnMust = "<button type='button' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + name + "\",\"" + uri + "\");closePop()'>" + $("#titleAnd").html() + "</button><br>";
    btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + name + "\",\"" + uri + "\");closePop()'>" + $("#titleWithout").html() + "</button>";
    tagContent = "<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>" + $("#titlePopover").html() + "</b></div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>";
    return tagContent;
};

function getPopoverTitle(tag,itemUri) {
    tagHtml = tag.html().substr(0,20);

    tagDelete = tagHtml;
    tagUri = null;
    if (tag.attr("data-uri").indexOf("/search_filters?uri=http%3A%2F%2Fwww.dbpedia.org") === 0) {
        tagUri = tag.attr("data-uri").substr("/search_filters?uri=".length)
    }
    res = "<span class='titletag'>" + tagHtml + "</span>";
    if (tagHtml.indexOf("#") !=0 && tagHtml.indexOf("@") !=0){
        res += "<i class='icon-trash pull-right pointerMouse' title='Delete' onclick='deleteTag(\"" + tagHtml + "\",\"" + tagUri + "\",\"" + itemUri + "\")'></i>";
    }
    return res;
};

function getWaitingScreen() {
    return "<div class='row-fluid'><i class='icon-refresh'></i> Loading ...</div>";
};