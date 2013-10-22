$(document).ready(function() {
    //Waiting screen for the tag
    originalWaitingText = "<div class='infoPop'>"+getWaitingScreen()+"</div>"
    //Generation of the popover of the tag
    $(".label-tag").each(function() {

        //check if the tag is already "popoverised"
        if( $(this).data('clickover') != null){
            return;
        }

        //create the popover
        $(this).clickover({ placement: 'bottom' })

        //add the onclick action
        $(this).on("click", function() {

            if ($(this).attr("filter-uri").indexOf("dbpedia.org") != -1) {
                var popover = $(this).data('clickover');
                errorText = "<div class='infoPop'>error</div>" + getPopoverButton($(this));
                var item = $(this);
                $.ajax({
                    url: item.attr("data-uri"),
                    timeout: 5000,
                    success: function(data) {
                        //popover.options.content = "<div class='infoPop'>" + data + "</div>" + getPopoverButton($(this));

                        $('.infoPop').html(data);

                        //truncate the dbpedia message
                        $('.popover').find('.textContent').jTruncate({
                            length: 200,
                            minTrail: 0,
                            moreText: "(...)",
                            lessText: "[-]",
                            ellipsisText: "",
                            moreAni: "fast",
                            lessAni: "fast"
                        })

                    },
                    error: function() {
                        //popover.options.content = errorText;
                        return $('.infoPop').html('');
                    }
                });
            }
            return false;
        });
    });
});

function getPopoverButton(tag) {
    var name = tag.html();
    var uri = tag.attr('filter-uri');
    var btnBan, btnMust, btnOptionnal, tagContent;
    btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleOr").html() + "</button><br>";
    btnMust = "<button type='button' class='btn btn-success span12 btnTag paddingMust' onclick='addTag(\"must\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleAnd").html() + "</button><br>";
    btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + name + "\",\"" + uri + "\");'>" + $("#titleWithout").html() + "</button>";
    tagContent = "<hr><div class='row-fluid'><div class='span12 text-center'><b class='adding-info'>" + $("#titlePopover").html() + "</b></div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>";
    return tagContent;
};

function getPopoverTitle(tag,itemUri) {
    tagHtml = tag.html().substr(0,20);

    tagDelete = tagHtml;
    tagUri = null;
    if (tag.attr("filter-uri").indexOf("dbpedia.org") != -1) {
        tagUri = tag.attr("filter-uri")
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