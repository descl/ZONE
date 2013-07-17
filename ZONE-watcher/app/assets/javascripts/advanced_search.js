/** filtrage**/

//Timer for keyword update
timer = "";

//When the document is ready, prepare and initiate different function to the object of the modal
$(document).ready(function() {
	//Define the action on show and hide of the modal
	$('#advancedSearchModal').on('hide', function() {
		$('body').css('overflow-y', 'auto');

	});
	$('#advancedSearchModal').on('show', function() {
		$('body').css('overflow-y', 'hidden');
	});

	$("#keywordTable").hide();
	$("#progressBarFiltering").hide();
	$('#btnWITH').button('toggle');

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

	//Instanciate the autocomplete for normal search
	$("#search-form").autocomplete({
		source : [""],
		search : function(event, ui) {
			$("#search-form").autocomplete({
				source : "../complete_entities/" + $("#search-form").val() + ".json"
			});
		},
		minLength : 4
	}).autocomplete("widget").addClass("span1");

	//Add the tag-source into the semantic source table
	$('.label-source').click(function(event) {
		addSourceItem("RSS", $(this).html());
		showPopoverSource();
		event.preventDefault();
		// Prevent link from following its href
	});

	//Prepare the popover for each tag-label
	$(".label-tag").each(function() {
		dualbutton = "<div class='btn-group'><button class='btn btn-success' onclick='addFilterItem(\"" + $("#reminderAnd").html() + "\",\"" + $(this).html() + "\")'><i class=\"icon-plus\"></i></button><button class='btn btn-info' onclick='addFilterItem(\"" + $("#reminderOr").html() + "\",\"" + $(this).text() + "\")'><b>O</b></button><button class='btn btn-danger' onclick='addFilterItem(\"" + $("#reminderWithout").html() + "\",\"" + $(this).html() + "\")'><i class=\"icon-minus\"></i></button></div>"
		$(this).popover({
			content : dualbutton,
			placement : 'bottom',
			html : 'true',
			delay : {
				show : 1000,
				hide : 100
			}
		});
	});

	//Add the tag-source into the semantic filtering table
	$('.label-tag').click(function(event) {
		$('.label-tag').not(this).popover('hide');
		//$(this).popover();
		event.preventDefault();
	});

	//show the favorite row when entering in an item
	$(".item_container").hover(function() {
		if ($("#btnList").hasClass('active')) {
			$(this).find('.row-favorite').fadeIn();
		}
	}, function() {
		if ($("#btnList").hasClass('active')) {
			$(this).find('.row-favorite').fadeOut();
		}
	});

	//Hide the favorite bar by default
	$(".row-favorite").hide();
	$(".row-list").hide();

	//Show the full link of the item when hover the short link
	$(".sourceHover").mouseenter(function() {
		$(this).hide();
		$(this).next(".sourceHoverFull").fadeIn();
	});

	//Show the short link of the item when leaving the hover of the full link
	$(".sourceHoverFull").mouseleave(function() {
		$(this).hide();
		$(this).prev(".sourceHover").fadeIn();
	});

});

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
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		if ($(this).is(':checked')) {
			motcle += ' , ' + $(this).val();
		}
	});

	var attr = getAttrSelected();

	//Add the line to the table
	if (type == 'and')
		$('#wellAnd').append("<span class='label label-success'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span>");
	else if (type == 'or')
		$('#wellOr').append("<span class='label label-info'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span>");
	else if (type == 'without')
		$('#wellWithout').append("<span class='label label-danger'>" + motcle + " <i class='icon-remove' onclick='$(this).closest(\"span\").remove();'></i></span>");

	//Reset the filtering
	rebootFiltering();
}

//Return the title of the button selected ( WITH, WITHOUT, AND, OR)
function getAttrSelected() {
	if ($('#btnAND').hasClass('active'))
		return $('#btnAND').prop('title');
	if ($('#btnWITH').hasClass('active'))
		return $('#btnWITH').prop('title');
	if ($('#btnOR').hasClass('active'))
		return $('#btnOR').prop('title');
	if ($('#btnWITHOUT').hasClass('active'))
		return $('#btnWITHOUT').prop('title');
}

//Disabled or not the OR button and hide or show the AND/WITH button, depending if it's the first line of filter or not
function checkDropdown() {
	if ($('#filteringTable tr').length > 1) {
		$('#btnOR').prop('disabled', false);
		$('#btnWITH').addClass('hidden');
		$('#btnAND').removeClass('hidden');
		$('#btnOR').button('toggle');
	} else {
		$('#btnOR').prop('disabled', true);
		$('#btnWITH').removeClass('hidden');
		$('#btnAND').addClass('hidden');
		$('#btnWITH').button('toggle');
	}
}

//Reset the dropdown selector and the table of related word
function rebootFiltering() {
	$('#keyword').val('');
	checkDropdown();

	unselect();
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});

}

//Write the filter in the easy-filter box
function getFilter() {
	$("#filteringArea").val('');
	$('#filteringTable tbody > tr').each(function() {
		$("#filteringArea").val($("#filteringArea").val() + $(this).find('td.tdkey').html() + ' ( ' + $(this).find('div.tdval').html() + ' ) ');
	});

}

//Select all the related words in the related table
function select() {
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		$(this).prop('checked', true);
	});
}

//Unselect all the related words in the related table
function unselect() {
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		$(this).prop('checked', false);
	});
}

//Remove one line of the filtering
function removelinefiltrage(line) {
	$('#' + line).remove();
	var usersTable = $("#filteringTable");
	usersTable.trigger("update");

	//Set the filter in the easy-filter box
	getFilter();

	checkDropdown();

	rebootFiltering();
}

//Fill the related keyword table with the right keyword
function updateKeywordTable() {
	rebootKeywordTable();
	if ($('#keyword').val() != "" && $('#keyword').val() != null)
		$.getJSON('../linked_words/' + $('#keyword').val() + '.json', function(data) {
			rebootKeywordTable();
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

/** sources **/

//General ID for the sources that the user pick
idRowSource = 0;

//Remove one line of the sources table
function removeSourceLine(line) {
	$('#' + line).remove();
}

//Add one line in the source table
function addRowSourceTable(Form) {
	if ($(Form + " .inputLogin").val() == "" && $(Form + " .inputSearch").val() == "") {
		return false
	}

	var textefinal = "";

	//Pick the icon corresponding to the source ( twitter, g+, rss,...)
	var tdicone = "";
	if (Form == "#formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.png" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "#formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.png" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "#formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.png" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

	var text = "";
	var tdsource = "";

	//Pick the icon corresponding to the type of the source ( search or people)
	if ($(Form + " .inputLogin").val() != "" && Form != "#formRSS") {
		text = $(Form + " .inputLogin").val();
		tdsource = '<td><i class="icon-user span1" style="padding-top:5px;"></i><div class="span8 breakword" contenteditable>' + text;
		textefinal = "<tr class='user' id='idsource" + idRowSource + "'>";
	} else if ($(Form + " .inputSearch").val() != "") {
		text = $(Form + " .inputSearch").val();
		tdsource = '<td><i class="icon-search span1" style="padding-top:5px;"></i> <div class="span8 breakword" contenteditable>' + text;
		textefinal = "<tr class='search' id='idsource" + idRowSource + "'>";
	}

	//Define the button to delete a line in the table
	tdaction = '</div><button class="btn pull-right btn-danger" onclick="removeSourceLine(\'idsource' + idRowSource + '\')"><i class="icon-remove icon-white" ></i></button></td>';
	idRowSource += 1;

	textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";

	//add the line
	$('#sourceTable').append(textefinal);

	//Reset the input of the source
	rebootForm(Form);
}

//Reset the input of the source
function rebootForm(Form) {
	$(Form + " .inputLogin").val('');
	$(Form + " .inputSearch").val('');
}

//Add one line with all sources in the source table
function addAllSourceTable(Form) {
	var textefinal = "";

	var tdicone = "";

	//Pick the icon corresponding to the source ( twitter, g+, rss,...)
	if (Form == "formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.png" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.png" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.png" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

	var tdsource = "<td><i><div class='breakword' style='float:left'>Toutes les sources</div></i>";

	textefinal = "<tr class='search' id='idsource" + idRowSource + "'>";

	//Define the button to delete a line in the table
	tdaction = '<button class="btn pull-right btn-danger"  onclick="removeSourceLine(\'idsource' + idRowSource + '\')"  ><i class="icon-remove icon-white"></i></button></td>';
	idRowSource += 1;

	textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";

	//add the line
	$('#sourceTable').append(textefinal);

	//Reset the input of the source
	rebootForm(Form);
}

//Reset the source table, Delete all lines
function rebootSourceTable() {
	$('#sourceTable tbody > tr').each(function() {
		$(this).remove();
	});
}

//Reset the filtering table, Delete all lines
function rebootFilteringTable() {
	$('#filteringTable tbody > tr').each(function() {
		$(this).remove();
	});
}

//Reset the related keyword table, Delete all lines
function rebootKeywordTable() {
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});
}

//Reset the semantic search.
function rebootGeneralModal() {
	rebootForm('#formTwitter');
	rebootForm('#formRSS');
	rebootForm('#formGoogle');

	rebootSourceTable();

	rebootFilteringTable();
	rebootKeywordTable();
	rebootFiltering();

	getFilter();

	$('#Sources').addClass('active');
	$('#Filtering').removeClass('active');

	setButtonNextTab();

	$("#keywordTable").hide();
	$("#progressBarFiltering").hide();
}

//Change the button that switch tab ( put everything ready to go to next tab)
function setButtonNextTab() {
	$('#btnTabPrevious').hide();
	$('#btnTabNext').show();
	$('#btnGoReador').hide();
	$('.titleSource').removeClass('hidden');
	$('.titleFiltering').addClass('hidden');
}

//Change the button that switch tab ( put everything ready to go to previous tab)
function setButtonPreviousTab() {
	$('#btnTabNext').hide();
	$('#btnGoReador').show();
	$('#btnTabPrevious').show();
	$('.titleSource').addClass('hidden');
	$('.titleFiltering').removeClass('hidden');
}

//Prepare the input with all data
function movingData() {
	var source = {};
		var filtering = {};
		
		var tabTwitter =[];
		var tabRss =[];
		$("#wellSources").children().each(function() {
			if ($(this).hasClass('twitterSource')) {
				tabTwitter.push($(this).html().substr(0, $(this).html().search('<i') - 1));
			} else if ($(this).hasClass('rssSource')) {
				tabRss.push($(this).html().substr(0, $(this).html().search('<i') - 1));
			}
		});
		
		var tabOr =[];
		var tabAnd =[];
		var tabWithout =[];
		$("#wellOr").children().each(function() {
			tabOr.push($(this).html().substr(0, $(this).html().search('<i') - 1));
		});
		$("#wellAnd").children().each(function() {
			tabAnd.push($(this).html().substr(0, $(this).html().search('<i') - 1));
		});
		$("#wellWithout").children().each(function() {
			tabWithout.push($(this).html().substr(0, $(this).html().search('<i') - 1));
		});
		
		source.twitter=tabTwitter;
		source.rss=tabRss;
		
		filtering.or=tabOr;
		filtering.and=tabAnd;
		filtering.without=tabWithout;
		
		$('#movedData').html($('#movedData').html() 
		+ "<input name='sources' type='hidden' value='" + JSON.stringify(source) + "'>" 
		+ "<input name='filters' type='hidden' value='" + JSON.stringify(filtering) + "'>");
}

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
	}

}

// home/search

//slide down the source tab for the source selected
function slideDown(id) {
	if($(id).is(":visible"))
		$(id).slideUp();
	else
		$(".form").not(id).slideUp('swing', function() {
			$(".form").not(id).hide();
			$(id).slideDown();
		});		
}

//Allow to switch tab in the new semantic search
function switchTab() {
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
		$("#addAllTwitter").attr("disabled", true);
	} else if (table == "RSS") {
		$("#wellSources").append("<span class='label label-warning label-wrap rssSource'>" + value + " <i class='icon-remove' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();$(\"#searchRSS\").attr(\"disabled\",false);checkWell(\"RSS\")'></i></span> ");
		$("#searchRSS").attr("disabled", true);
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
