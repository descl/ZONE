/** filtrage**/

//General ID for the filters that the user pick
idRowFiltering = 0;

//Change the dropdown selector for the filtering
function changeDropdownKeyword(keyword) {
	$('#dropdownKeyword').text(keyword);
	$('#dropdownKeyword').val(keyword);
}

//Add a filter to the list of filter
function addFilter() {
	if ($('#keyword').val() == "") {
		return false
	}
	var id = 1;
	var classtr = "";
	//Choose the class of the line of the table ( red for WITHOUT, blue for OR, green for AND)
	if ($('#dropdownKeyword').val() == "ET") {
		id = 1;
		classtr = 'success';
	} else if ($('#dropdownKeyword').val() == "OU" || $('#dropdownKeyword').val() == "AVEC") {
		id = 0;
		classtr = 'info';
	} else if ($('#dropdownKeyword').val() == "SANS") {
		id = 2;
		classtr = 'error';
	}

	//Add the related word to the keyword
	var motcle = $('#keyword').val();
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		if ($(this).is(':checked')) {
			motcle += ', ' + $(this).val();
		}
	});
	
	
	var attr = $('#dropdownKeyword').val();
	
	//Change the dropdwonselector ( take away WITH and WITHOUT and put AND, OR , WITHOUT instead)
	if ($('#filteringTable tr').length == 1)
		$('.dropdownKeywordFiltering').html('<li onclick="changeDropdownKeyword(\'OU\')">OU</li><li onclick="changeDropdownKeyword(\'ET\')">ET</li><li onclick="changeDropdownKeyword(\'SANS\')">SANS</li>');
	
	//Add the line to the table
	$('#filteringTable').append('<tr id="idrow' + idRowFiltering + '" class="' + classtr + '"><td hidden>' + id + '</td><td class="tdkey">' + attr + '</td><td class="span3"><div class="tdval span9 breakword">' + motcle + '</div><button class="btn btn-danger pull-right"  onclick="removelinefiltrage(\'idrow' + idRowFiltering + '\')"><i class="icon-remove"></i></button></td></tr>');
	idRowFiltering += 1;

	//Reset the filtering
	rebootFiltering();
	
	//Write the filter in the easy-box
	getFilter();
}

//Reset the dropdown selector and the table of related word
function rebootFiltering() {
	$('#keyword').val('');
	if ($('#filteringTable tr').length == 1) {
		$('#dropdownKeyword').text("AVEC");
		$('#dropdownKeyword').val("AVEC");
	} else {
		$('#dropdownKeyword').text("OU");
		$('#dropdownKeyword').val("OU");
	}

	unselect();
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});
}

//Write the filter in the easy-filter box
function getFilter() {
	$("#filteringArea").val('');
	$('#filteringTable tbody > tr').each(function() {
		$("#filteringArea").val($("#filteringArea").val() + $(this).find('td.tdkey').html() + ' ' + $(this).find('div.tdval').html() + ' ');
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

	if ($('#filteringTable tr').length == 1)
		$('.dropdownKeywordFiltering').html('<li onclick="changeDropdownKeyword(\'AVEC\')">AVEC</li><li onclick="changeDropdownKeyword(\'SANS\')">SANS</li>');

	rebootFiltering();
}

//Fill the related keyword table with the right keyword
function updateKeywordTable() {
	rebootKeywordTable();
	$('#fakesynonyme tbody > tr').each(function() {
		if ($('#keyword').val() == $(this).find('td.mot').html()) {
			$('#keywordTable').append('<tr><td><label class="checkbox"><input type="checkbox" value="' + $(this).find('td.synonyme').html() + '">' + $(this).find('td.synonyme').html() + '</label></td></tr>');
		}
	});
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
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "#formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "#formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

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
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

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

	rebootFiltering();
	rebootFilteringTable();
	rebootKeywordTable();

	getFilter();

	$('#Sources').addClass('active');
	$('#Filtering').removeClass('active');

	setButtonNextTab();
	
	if ($('#filteringTable tr').length == 1){
		$('.dropdownKeywordFiltering').html('<li onclick="changeDropdownKeyword(\'AVEC\')">AVEC</li><li onclick="changeDropdownKeyword(\'SANS\')">SANS</li>');
		$('#dropdownKeyword').text("AVEC");
		$('#dropdownKeyword').val("AVEC");
	}

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
