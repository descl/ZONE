/**
 * General section
 */

//Timer for keyword update
timer = "";

//When the document is ready, prepare and initiate different function to the object of the modal
$(document).ready(function() {
	$("#keywordTable").hide();
	$("#progressBarFiltering").hide();

	//Instanciate the autocomplete for semantic search
	$("#keyword").autocomplete({
		source : [""],
		search : function(event, ui) {
			$("#keyword").autocomplete({
				source : "../complete_entities/" + $("#keyword").val() + ".json"
			});
		},
		close : function() {
			askUpdateKeywordTable();
		},
		minLength : 4
	}).autocomplete("widget").addClass("span1");

	//show the favorite row when entering in an item
	$(".item_container").hover(function() {
		if ($("#btnList").hasClass('active')) {
			//$(this).find('.row-favorite').fadeIn();
		}
	}, function() {
		if ($("#btnList").hasClass('active')) {
			//$(this).find('.row-favorite').fadeOut();
		}
	});

	//Hide the favorite bar by default
	$(".row-favorite").hide();
	$(".row-list").hide();

	$("#addAllRSS").attr("disable", false);
	$("#addAllTwitter").attr("disable", false);

	//Reminder box in the item page
	$("#reminder").hover(function() {
	}, function() {
		$("#openReminder").fadeIn();
		$("#reminder").css("left", "-37%");
	});
	
	$("#openReminder").hover(function(){
		$("#openReminder").fadeOut();
		$("#reminder").css("transition", "all 0.5s ease-in");
		$("#reminder").css("left", "-1%");
	},function(){});
	
	$(".hideTag").hide();
	$(".btn-toolbar").hide();

	//action to show the tag on click
	$(".showTag").on('click', function() {
		$(this).parent().next(".btn-toolbar").slideDown();
		$(this).hide();
		$(this).next(".hideTag").show();
	});

	//Action to hide the tag on click
	$(".hideTag").on('click', function() {
		$(this).parent().next(".btn-toolbar").slideUp();
		$(this).hide();
		$(this).prev(".showTag").show();
	});

	//Generation of the popover of the tag
	var btnOptionnal = "";
	var btnMust = "";
	var btnBan = "";
	$(".label-tag").each(function() {
		btnOptionnal = "<button type='button' class='btn btn-info span12 btnTag' onclick='addTag(\"opt\",\"" + $(this).html() + "\");closePop()'>" + $("#titleOr").html() + "</button><br>";
		btnMust = "<button type='button' class='btn btn-success span12 btnTag' onclick='addTag(\"must\",\"" + $(this).html() + "\");closePop()'>" + $("#titleAnd").html() + "</button><br>";
		btnBan = "<button type='button' class='btn btn-danger span12 btnTag' onclick='addTag(\"no\",\"" + $(this).html() + "\");closePop()'>" + $("#titleWithout").html() + "</button>";
		$(this).popover({
			title : $("#titlePopover").html(),
			content : "<div class='row-fluid'><div class='span12'>" + btnOptionnal + "</div></div><div class='row-fluid'><div class='span12'>" + btnMust + "</div></div><div class='row-fluid'><div class='span12'>" + btnBan + "</div></div>",
			placement : "bottom"
		});
	});

	//Disable the default action onclick on the tag
	$(".label-tag").on('click', function() {
		return false;
	});

	$(".showFavorite").hover(function() {
		$(this).next(".row-favorite").fadeIn();
		$(this).hide();
	},function(){
		$(this).hide();
	});

	$(".row-favorite").hover(function(){
		$(this).prev(".showFavorite").hide();
	},function() {
		$(this).hide();
		$(this).prev(".showFavorite").fadeIn();
	});
	//Tweeter function
	! function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0];
		if (!d.getElementById(id)) {
			js = d.createElement(s);
			js.id = id;
			js.src = "https://platform.twitter.com/widgets.js";
			fjs.parentNode.insertBefore(js, fjs);
		}
	}(document, "script", "twitter-wjs");

	//Google+ function
	window.___gcfg = {
		lang : 'fr'
	};

	//Googlefunction
	(function() {
		var po = document.createElement('script');
		po.type = 'text/javascript';
		po.async = true;
		po.src = 'https://apis.google.com/js/plusone.js';
		var s = document.getElementsByTagName('script')[0];
		s.parentNode.insertBefore(po, s);
	})(); 
		
	//Facebook function
	(function(d, s, id) {
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id))
				return;
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/fr_FR/all.js#xfbml=1";
			fjs.parentNode.insertBefore(js, fjs);
		})(document, 'script', 'facebook-jssdk');
});

//Allow to switch tab in the new semantic search
function switchTab() {
	if ($('#filtering').hasClass('active'))
		return;
	$('#sources').removeClass('active');
	$('#breadcrumbSources').removeClass('active');

	$('#filtering').addClass('active');
	$('#breadcrumbFiltering').addClass('active');
	$('#breadcrumbFiltering').show();

	$('#filtering').hide();
	$('#sources').fadeOut('swing', function() {
		$('#filtering').fadeIn();
	});

	$(".form").hide();
}

//Allow to reverse switch tab in the new semantic search
function reverseSwitchTab() {
	if ($('#sources').hasClass('active'))
		return;
	$('#filtering').removeClass('active');
	$('#breadcrumbFiltering').removeClass('active');

	$('#sources').addClass('active');
	$('#breadcrumbSources').addClass('active');
	$('#breadcrumbSources').show();

	$('#sources').hide();
	$('#filtering').fadeOut('swing', function() {
		$('#sources').fadeIn();
	});
}

//Prepare the input with all data
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
}

/**
 * End of general section
 */

/**
 *Source section
 */

//slide down the source tab for the source selected
function slideDown(id) {
	if ($(id).is(":visible"))
		$(id).slideUp();
	else
		$(".form").not(id).slideUp('swing', function() {
			$(".form").not(id).hide();
			$(id).slideDown();
		});
}

//Add a source in the table
function addSource(table, value) {
	if (table == "Twitter") {
		text = $(value).val();
		if (value == "#searchTwitter")
			text = "#" + text;
		else
			text = "@" + text;
		$("#wellSources").append("<span class='label label-info twitterSource'>" + text + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();checkWell()'></i></span> ");
		$("#addAllTwitter").attr("disabled", true);
	} else if (table == "RSS") {
		$("#wellSources").append("<span class='label-wrap label label-warning rssSource' >" + $(value).val() + " <i class='icon-remove' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();checkWell()'></i></span> ");
		$("#addAllRSS").attr("disabled", true);
	}
	$(value).val("");
	$(value).html("");
}

//add "all" sources in the table
function addAllSource(table, value) {
	if (table == "Twitter") {
		$("#wellSources").append("<span class='label label-info twitterSource'>" + value + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();$(\"#loginTwitter\").attr(\"disabled\",false);$(\"#searchTwitter\").attr(\"disabled\",false);checkWell(\"Twitter\")'></i></span> ");
		$("#searchTwitter").attr("disabled", true);
		$("#loginTwitter").attr("disabled", true);
		$("#searchTwitter").val("").html("");
		$("#loginTwitter").val("").html("");
		$("#addAllTwitter").attr("disabled", true);
	} else if (table == "RSS") {
		$("#wellSources").append("<span class='label label-warning label-wrap rssSource'>" + value + " <i class='icon-remove' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();$(\"#searchRSS\").attr(\"disabled\",false);checkWell(\"RSS\")'></i></span> ");
		$("#searchRSS").attr("disabled", true);
		$("#searchRSS").val("").html("");
		$("#addAllRSS").attr("disabled", true);
	}
}

//Check the well to see if it has children. If not, addAllSources button are activated.
function checkWell(table) {
	if (table == "Twitter")
		$("#addAllTwitter").attr("disabled", false);
	else if (table == "RSS")
		$("#addAllRSS").attr("disabled", false);
	if ($("#wellSources").children(".twitterSource").length == 0)
		$("#addAllTwitter").attr("disabled", false);
	if ($("#wellSources").children(".rssSource").length == 0)
		$("#addAllRSS").attr("disabled", false);
}

/*
* End of source section
*/

/**
 * Filtering section
 */

//Add a filter to the list of filter
function addFilter(type) {
	$("#progressBarFiltering").hide();
	$("#keywordTable").hide();
	clearTimeout(timer);
	if ($('#keyword').val() == "") {
		return false
	}
	var id = 1;
	var classtr = "";
	//Choose the class of the line of the table ( red for WITHOUT, blue for OR, green for AND)
	if (type == 'and') {
		id = 1;
		classtr == 'success';
	} else if (type == 'or') {
		id = 0;
		classtr = 'info';
	} else if (type == 'without') {
		id = 2;
		classtr = 'error';
	} else {
		return false;
	}

	//Add the related word to the keyword
	var motcle = $('#keyword').val();

	//Add the line to the table
	if (type == 'and')
		$('#wellAnd').append("<span class='label label-success'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");
	else if (type == 'or')
		$('#wellOr').append("<span class='label label-info'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");
	else if (type == 'without')
		$('#wellWithout').append("<span class='label label-danger'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");

	$('#keywordTable tbody > tr > td > label > input').each(function() {
		if ($(this).is(':checked')) {
			if (type == 'and')
				$('#wellAnd').append("<span class='label label-success'>" + $(this).val() + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");
			else if (type == 'or')
				$('#wellOr').append("<span class='label label-info'>" + $(this).val() + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");
			else if (type == 'without')
				$('#wellWithout').append("<span class='label label-danger'>" + $(this).val() + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span> ");
		}
	});
	$('#keyword').val('');

	//Reset the filtering
	rebootFiltering();
}

//Reset the dropdown selector and the table of related word
function rebootFiltering() {
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});

}

//Fill the related keyword table with the right keyword
function updateKeywordTable() {
	rebootFiltering();
	if ($('#keyword').val() != "" && $('#keyword').val() != null)
		$.getJSON('../linked_words/' + $('#keyword').val() + '.json', function(data) {
			rebootFiltering();
			$.each(data, function(key, val) {
				if ($('#keyword').val() != "") {
					$('#keywordTable').append('<tr><td><label class="checkbox"><input type="checkbox" value="' + val + '">' + val + '</label></td></tr>');
				}
			});
			$("#keywordTable").show();
			$("#progressBarFiltering").hide();
		});
}

//ask the timer if the related keyword table can be show. It will wait 1 second before doing it. If the function is call again, it will stop the current timer and start a new
function askUpdateKeywordTable() {
	$("#keywordTable").hide();
	if ($('#keyword').val() != "" && $('#keyword').val() != null) {
		$("#progressBarFiltering").show();
		clearTimeout(timer);
		timer = setTimeout(function() {
			updateKeywordTable()
		}, 1000);
	} else {
		$("#progressBarFiltering").hide();
	}
}

//Define the action on ENTER key press for the filtering
function keywordComplete() {
	if (event.keyCode == 13) {
		addFilter();
		$('#keyword').autocomplete('close');
		return false;
	}
}

/*
* End of filtering section
*/

/*
* Items section
*/

//Function that change the disposition of the items, used in /items
function changeItemFormat(type) {
	if (type == 'card') {
		$("#btnCard").addClass('active');
		$("#btnList").removeClass('active');

		$(".item-bloc:even").addClass('span6 pull-left');
		$(".item-bloc:even").addClass('clear-left');
		$(".item-bloc:odd").addClass('span6 pull-right');
		$(".item-bloc:odd").addClass('clear-right');

		$(".row-favorite").hide();
		$(".showFavorite").hide();
		$(".row-list").show();
	} else {
		$("#btnList").addClass('active');
		$("#btnCard").removeClass('active');

		$('.item-bloc').removeClass('span6');
		$('.item-bloc').removeClass('pull-left');
		$('.item-bloc').removeClass('pull-right');
		$('.item-bloc').removeClass('clear-right');
		$('.item-bloc').removeClass('clear-left');

		$(".row-list").hide();
		$(".showFavorite").show();
	}

}

//Add the tag to the summary panel
function addTag(type, value) {
	if (type == 'opt')
		$(".well-info").append('<span class="label label-info">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();"></i></span> ');
	else if (type == 'must')
		$(".well-success").append('<span class="label label-success">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();"></i></span> ');
	else if (type == 'no')
		$(".well-danger").append('<span class="label label-danger">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();"></i></span> ');
	return false;
};

//Close all the popover after clicking on a selction
function closePop() {
	$(".label-tag").popover('hide');
	$('#openReminder').popover('show');
	setTimeout(function() {
		$('#openReminder').popover('hide');
	}, 2000);
	return false;
}

/*
 * End of items section
 */