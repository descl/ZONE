var downloadNewsDatas;

downloadNewsDatas = function(uri, incr) {
    if (incr == null) {
        incr = 0;
    }
    if (incr > 10) {
        return;
    }
    item = $('.item-bloc[data-local-uri="' + uri + '"]');
    $('#masonry-container').masonry("appended", $(item), true);

return $.ajax({
    url: uri,
    async: true,
    context: uri,
    success: function(data) {
    var item;
    item = $('.item-bloc[data-local-uri="' + uri + '"]');
    if (item.length > 0) {
    $(item).removeAttr("data-local-uri");
    $(item).find('[class*=item_wait]').detach();
    $(item).find('[class=item_container]').append(data);
    $(item).css("opacity",1);
    $('#masonry-container').masonry('reload');
    return setHighlightTags($(item));
    }
},
error: function(xhr, ajaxOptions, thrownError) {
    if (xhr.status === 500) {
    return downloadNewsDatas(uri, incr + 1);
    }
}
});
};


$(function() {
    <% if@loadOnTop == true%>
        $('.newNewsLinkBox').hide();
        $('.items-list').prepend('<%= escape_javascript(render :partial => "items", :items => @items) %>');
        return $('.item-bloc[data-local-uri]').each(function(id, element) {
            var localUri;
            localUri = $(element).attr('data-local-uri');
            return downloadNewsDatas(localUri);
            });
    <%else%>
        return $('.item-bloc[data-local-uri]').each(function(id, element) {
            var localUri;
            localUri = $(element).attr('data-local-uri');
            return downloadNewsDatas(localUri);
            });
    <%end%>
});
