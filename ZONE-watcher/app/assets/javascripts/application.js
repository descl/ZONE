//= require jquery
//  require_tree .
//=  require jquery_ujs
//=  require jquery.ui.all
//= require jquery.remotipart

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
    var url = ""
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
        
        url = $(this).attr("filter-uri");
        tabOr.push(url);
    });
    $("#summaryOr").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabOr.push(item);
         
        url = $(this).attr("filter-uri");
        tabOr.push(url);
    });
    $("#wellAnd").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabAnd.push(item);
        
       	url = $(this).attr("filter-uri");
        tabAnd.push(url);
    });
    $("#summaryAnd").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabAnd.push(item);
        
        url = $(this).attr("filter-uri");
        tabAnd.push(url);
    });
    $("#wellWithout").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        item = item.replace(/'/g, "\\&#39;");
        tabWithout.push(item);
        
        url = $(this).attr("filter-uri");
        tabWithout.push(url);
    });
    $("#summaryWithout").children().each(function() {
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabWithout.push(item);
        
        url = $(this).attr("filter-uri");
        tabWithout.push(url);        
    });

    source.twitter = tabTwitter;
    source.rss = tabRss;

    filtering.or = tabOr;
    filtering.and = tabAnd;
    filtering.without = tabWithout;

    $('#movedData').html($('#movedData').html() + "<input name='sources' type='hidden' value='" + JSON.stringify(source) + "'>" + "<input name='filters' type='hidden' value='" + JSON.stringify(filtering) + "'>");

	$('#formModal').submit();
}

// Functions for the draggable tags
function allowDrop(ev)
{
	ev.preventDefault();
}

function drag(ev)
{
	ev.dataTransfer.setData("Text",ev.target.id);
}

function drop(ev)
{
	ev.preventDefault();
	var data=ev.dataTransfer.getData("Text");
	ev.target.appendChild(document.getElementById(data));
	checkColor();
}

//Check the color of the tags in the well box
function checkColor(){
	$(".well-info").children().each(function(){
		$(this).removeClass();
		$(this).addClass("label label-info");
	});
	
	$(".well-success").children().each(function(){
		$(this).removeClass();
		$(this).addClass("label label-success");
	});
	
	$(".well-danger").children().each(function(){
		$(this).removeClass();
		$(this).addClass("label label-danger");
	});
}

//Return an id for the filter
function getId(type){
	var idbasic = 0;
	$(".well-info").children().each(function(){
		if ( idbasic <= parseInt($(this).attr("id")))
			idbasic = parseInt($(this).attr("id")) +1;
	});
	$(".well-success").children().each(function(){
		if ( idbasic <= parseInt($(this).attr("id")))
			idbasic = parseInt($(this).attr("id")) +1;
	});
	$(".well-danger").children().each(function(){
		if ( idbasic <= parseInt($(this).attr("id")))
			idbasic = parseInt($(this).attr("id")) +1;
	});
	return idbasic;
}