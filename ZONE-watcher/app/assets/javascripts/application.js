//= require jquery
//  require_tree .
//  require jquery_ujs
//=  require jquery.ui.all


//Prepare the input with all data
//Read all the tag selected ( sources and filters ) and create input with the map corresponding to these tags
//Generate one hashmap for sources ( name : source) and one hashmap for filters ( name : filtering )
function movingData() {
    $("#movedData").html("");
    var source = {};
    var filtering = {};

    var tabTwitter = [];
    var tabRss = [];
    var item = ""
    $("#wellSources").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        if ($(this).hasClass('twitterSource')) {
            tabTwitter.push(item);
        } else if ($(this).hasClass('rssSource')) {
            tabRss.push(item);
        }
    });

    var tabOr = [];
    var tabAnd = [];
    var tabWithout = [];
    $("#wellOr").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabOr.push(item);
    });
    $("#summaryOr").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabOr.push(item);
    });
    $("#wellAnd").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabAnd.push(item);
    });
    $("#summaryAnd").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabAnd.push(item);
    });
    $("#wellWithout").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        item = item.replace(/'/g, "\\&#39;");
        tabWithout.push(item);
    });
    $("#summaryWithout").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabWithout.push(item);
    });

    source.twitter = tabTwitter;
    source.rss = tabRss;

    filtering.or = tabOr;
    filtering.and = tabAnd;
    filtering.without = tabWithout;

    $('#movedData').html($('#movedData').html() + "<input name='sources' type='hidden' value='" + JSON.stringify(source) + "'>" + "<input name='filters' type='hidden' value='" + JSON.stringify(filtering) + "'>");

	$('#formModal').submit();
}