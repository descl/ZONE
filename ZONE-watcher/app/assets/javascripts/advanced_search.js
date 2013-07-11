/** filtrage**/

//General ID for the filters that the user pick
idRowFiltering = 0;
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
	}).autocomplete( "widget" ).addClass( "span1" );
	
	//Instanciate the autocomplete for normal search
	$("#search-form").autocomplete({
		source : [""],
		search : function(event, ui) {
			$("#search-form").autocomplete({
				source : "../complete_entities/" + $("#search-form").val() + ".json"
			});
		},
		minLength : 4
	}).autocomplete( "widget" ).addClass( "span1" );

	//Add the tag-source into the semantic source table
	$('.label-source').click(function (event) {
	  addSourceItem("RSS",$(this).html());
	  showPopoverSource();
	  event.preventDefault(); // Prevent link from following its href
	});
	
	//Prepare the popover for each tag-label
	$(".label-tag").each(function(){
		dualbutton = "<div class='btn-group'><button class='btn btn-success' onclick='addFilterItem(\""+$("#reminderAnd").html()+"\",\""+$(this).html()+"\")'><i class=\"icon-plus\"></i></button><button class='btn btn-info' onclick='addFilterItem(\""+$("#reminderOr").html()+"\",\""+$(this).text()+"\")'><b>O</b></button><button class='btn btn-danger' onclick='addFilterItem(\""+$("#reminderWithout").html()+"\",\""+$(this).html()+"\")'><i class=\"icon-minus\"></i></button></div>"
		$(this).popover({ content: dualbutton ,placement: 'bottom',html: 'true',delay: { 
                     show: 1000, 
                     hide: 100
                  }});
	});
	
	//Add the tag-source into the semantic filtering table
	$('.label-tag').click(function (event) {
		$('.label-tag').not(this).popover('hide');
		//$(this).popover();
	   	event.preventDefault();
	});
	
	//show the favorite row when entering in an item
	$(".item_container").hover(function(){
		if ($("#btnList").hasClass('active')){
			$(this).find('.row-favorite').fadeIn();
		}
	},function(){
		if ($("#btnList").hasClass('active')){
			$(this).find('.row-favorite').fadeOut();
		}
	});
	
	//Hide the favorite bar by default
	$(".row-favorite").hide();
	$(".row-list").hide();
	
	//Show the full link of the item when hover the short link
	$(".sourceHover").mouseenter(function(){
		$(this).hide();
		$(this).next(".sourceHoverFull").fadeIn();
	});
	
	//Show the short link of the item when leaving the hover of the full link
	$(".sourceHoverFull").mouseleave(function(){
		$(this).hide();
		$(this).prev(".sourceHover").fadeIn();
	});

});

//Add a filter to the list of filter
function addFilter() {
	$("#progressBarFiltering").hide();
	$("#keywordTable").hide();
	clearTimeout(timer);
	if ($('#keyword').val() == "") {
		return false
	}
	var id = 1;
	var classtr = "";
	//Choose the class of the line of the table ( red for WITHOUT, blue for OR, green for AND)
	if ($('#btnAND').hasClass('active') ) {
		id = 1;
		classtr = 'success';
	} else if ($('#btnOR').hasClass('active') || $('#btnWITH').hasClass('active') ) {
		id = 0;
		classtr = 'info';
	} else if ($('#btnWITHOUT').hasClass('active')) {
		id = 2;
		classtr = 'error';
	} else {
		return false;
	}

	//Add the related word to the keyword
	var motcle = $('#keyword').val();
	var linkedKeyword = $('#btnOR').attr("title");
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		if ($(this).is(':checked')) {
			motcle += ' '+linkedKeyword.toLowerCase()+' ' + $(this).val();
		}
	});

	var attr = getAttrSelected();

	//Add the line to the table
	$('#filteringTable').append('<tr id="idrow' + idRowFiltering + '" class="' + classtr + '"><td hidden>' + id + '</td><td class="tdkey">' + attr + '</td><td class="span3"><div class="tdval span9 breakword">' + motcle + '</div><button class="btn btn-danger pull-right"  onclick="removelinefiltrage(\'idrow' + idRowFiltering + '\')"><i class="icon-remove"></i></button></td></tr>');
	idRowFiltering += 1;

	//Reset the filtering
	rebootFiltering();

	//Write the filter in the easy-box
	getFilter();

	//Change the dropdwonselector ( take away WITH and WITHOUT and put AND, OR , WITHOUT instead)
	checkDropdown();
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

	var arraySourceAdded = new Array();
	var arrayFilteringAdded = new Array();
	var littleArray = new Array();

	//Prepare the array that will contain the sources data
	$('#sourceTable tbody > tr ').each(function() {
		if ($(this).find('input.valSource').val()) {
			littleArray.push($(this).find('input.valSource').val());
			if ($(this).find('div.breakword').html() != null) {
				littleArray.push($(this).find('div.breakword').html());
			}
			arraySourceAdded.push(littleArray);
			littleArray = new Array();
		}
	});

	//Prepare the array that will contain the filtering data
	$('#filteringTable tbody > tr ').each(function() {
		if ($(this).find('td.tdkey').html()) {
			littleArray.push($(this).find('td.tdkey').html());
			if ($(this).find('div.breakword').html() != null) {
				littleArray.push($(this).find('div.breakword').html());
			}
			arrayFilteringAdded.push(littleArray);
			littleArray = new Array();
		}
	});

	//create the input with the data
	$('#movedData').html($('#movedData').html() + "<input name='arraySource' type='hidden' value='" + arraySourceAdded + "'>" + "<input name='arrayFiltering[]' type='hidden' value='" + arrayFilteringAdded + "'>");
}

//Function that change the disposition of the items, used in /items
function changeItemFormat(type){
	if (type=='card'){
		$("#btnCard").addClass('active');
		$("#btnList").removeClass('active');
		
		$(".item-bloc:even").addClass('span6 pull-left');
		$(".item-bloc:even").addClass('clear-left');
		$(".item-bloc:odd").addClass('span6 pull-right');
		$(".item-bloc:odd").addClass('clear-right');
		
		$(".row-favorite").hide();
		$(".row-list").show();
	}else{
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
function slideDown(id){
$(".form").not(id).slideUp('swing', function() {
		$(".form").hide();
    	$(id).slideDown();
  });
}

//Allow to switch tab in the new semantic search
function switchTab(){
	if ($('#sourcesFirst').hasClass("active")){
		$('#sourcesFirst').removeClass('active');
		$('#breadcrumbSourcesOne').removeClass('active');
		$('#breadcrumbSourcesOne').hide();
		
		prepareResumeSource();
		
		$('#sourcesSecond').addClass('active');
		$('#breadcrumbSourcesTwo').addClass('active');
		$('#breadcrumbSourcesTwo').show();
	}
	else if ($('#sourcesSecond').hasClass("active")){
		$('#sourcesSecond').removeClass('active');
		$('#breadcrumbSourcesTwo').removeClass('active');
		$('#breadcrumbSourcesTwo').show();
		
		$('#filteringFirst').addClass('active');
		$('#breadcrumbFilteringOne').addClass('active');
		$('#breadcrumbFilteringOne').show();
	}
	else if ($('#filteringFirst').hasClass("active")){
		$('#filteringFirst').removeClass('active');
		$('#breadcrumbFilteringOne').removeClass('active');
		$('#breadcrumbFilteringOne').hide();
		
		$('#filteringSecond').addClass('active');
		$('#breadcrumbFilteringTwo').addClass('active');
		$('#breadcrumbFilteringTwo').show();
	}	
}

//Allow to reverse switch tab in the new semantic search
function reverseSwitchTab(){
	if ($('#sourcesSecond').hasClass("active")){
		$('#sourcesSecond').removeClass('active');
		$('#breadcrumbSourcesTwo').removeClass('active');
		$('#breadcrumbSourcesTwo').hide();
		
		$('#sourcesFirst').addClass('active');
		$('#breadcrumbSourcesOne').addClass('active');
		$('#breadcrumbSourcesOne').show();
	}
	else if ($('#filteringFirst').hasClass("active")){
		$('#filteringFirst').removeClass('active');
		$('#breadcrumbFilteringOne').removeClass('active');
		$('#breadcrumbFilteringOne').show();
		
		$('#sourcesSecond').addClass('active');
		$('#breadcrumbSourcesTwo').addClass('active');
		$('#breadcrumbSourcesTwo').show();
	}
	else if ($('#filteringSecond').hasClass("active")){
		$('#filteringSecond').removeClass('active');
		$('#breadcrumbFilteringTwo').removeClass('active');
		$('#breadcrumbFilteringTwo').hide();
		
		$('#filteringFirst').addClass('active');
		$('#breadcrumbFilteringOne').addClass('active');
		$('#breadcrumbFilteringOne').show();
	}	
}

//Add a source in the table
function addSource(table,value){
	if (table=="#wellTwitter"){
		text = $(value).val();
		if (value=="#searchTwitter")
			text = "#"+text;
		else
			text="@"+text;
		$(table).append("<span class='label label-info'>"+text+" <i class='icon-remove' onclick='$(this).closest(\"span\").remove()'></i></span> ");
	} else if (table=="#wellRSS"){
		$(table).append("<span class='label label-warning span12' style='white-space: pre-wrap;min-height: 0px;width: auto;margin-left:5px;word-break:break-all'>"+$(value).val()+" <i class='icon-remove' onclick='$(this).closest(\"span\").remove()'></i></span><br>");
	}
	$(value).val("");
	$(value).html("");
}

//add "all" sources in the table
function addAllSource(table,value){
	if (table=="#wellTwitter"){
		$(table).empty();
		$(table).append("<span class='label label-info'>"+value+" <i class='icon-remove' onclick='$(this).closest(\"span\").remove()'></i></span> ");
	} else if (table=="#wellRSS"){
		$(table).empty();
		$(table).append("<span class='label label-warning span12' style='white-space: pre-wrap;min-height: 0px;width: auto;margin-left:5px;word-break:break-all'>"+value+" <i class='icon-remove' onclick='$(this).closest(\"span\").remove()'></i></span>");
	}
}

//Put all the sources in the resume
function prepareResumeSource(){
	$("#resumeWellTwitter").empty();
	$("#wellTwitter").children().each(function(index){
		$("#resumeWellTwitter").append("<span class='label label-info'>"+$(this).html()+"</span> ");
	});
	
	$("#resumeWellRSS").empty();
	$("#wellRSS").children().not("br").each(function(index){
			$("#resumeWellRSS").append("<span class='label label-warning span12' style='white-space: pre-wrap;min-height: 0px;width: auto;margin-left:5px;word-break:break-all'>"+$(this).html()+"</span><br>");
	});
}







