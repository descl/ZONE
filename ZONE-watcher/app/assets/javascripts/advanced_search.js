/** filtrage**/

idRowFiltrage = 0;

function changeDropdownKeyword(toto) {
	$('#dropdownKeyword').val(toto);
	$('#dropdownKeyword').html(toto);
}

function addFilter() {
	if ($('#keyword').val() == "") {
		return false
	}
	var id = 1;
	var classtr = "";
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

	var motcle = $('#keyword').val();
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		if ($(this).is(':checked')) {
			motcle += ', ' + $(this).val();
		}
	});
	var attr = $('#dropdownKeyword').val();
	if ($('#filteringTable tr').length == 1)
		$('.dropdownKeywordFiltering').html('<li onclick="changeDropdownKeyword(\'OU\')">OU</li><li onclick="changeDropdownKeyword(\'ET\')">ET</li><li onclick="changeDropdownKeyword(\'SANS\')">SANS</li>');
	$('#filteringTable').append('<tr id="idrow' + idRowFiltrage + '" class="' + classtr + '"><td hidden>' + id + '</td><td class="tdkey">' + attr + '</td><td class="span3"><div class="tdval span9 breakword">' + motcle + '</div><button class="btn btn-danger pull-right"  onclick="removelinefiltrage(\'idrow' + idRowFiltrage + '\')"><i class="icon-remove"></i></button></td></tr>');
	idRowFiltrage += 1;

	rebootFiltering();
	getFilter();
}

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

function getFilter() {
	$("#filteringArea").val('');
	$('#filteringTable tbody > tr').each(function() {
		$("#filteringArea").val($("#filteringArea").val() + $(this).find('td.tdkey').html() + ' ' + $(this).find('div.tdval').html() + ' ');
	});

}

function select() {
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		$(this).prop('checked', true);
	});
}

function unselect() {
	$('#keywordTable tbody > tr > td > label > input').each(function() {
		$(this).prop('checked', false);
	});
}

function removelinefiltrage(line) {
	$('#' + line).remove();
	var usersTable = $("#filteringTable");
	usersTable.trigger("update");

	getFilter();

	if ($('#filteringTable tr').length == 1)
		$('.dropdownKeywordFiltering').html('<li onclick="changeDropdownKeyword(\'AVEC\')">AVEC</li><li onclick="changeDropdownKeyword(\'SANS\')">SANS</li>');

	rebootFiltering();
}

function updateKeywordTable() {
	rebootKeywordTable();
	$('#fakesynonyme tbody > tr').each(function() {
		if ($('#keyword').val() == $(this).find('td.mot').html()) {
			$('#keywordTable').append('<tr><td><label class="checkbox"><input type="checkbox" value="' + $(this).find('td.synonyme').html() + '">' + $(this).find('td.synonyme').html() + '</label></td></tr>');
		}
	});
}

/** sources **/
idRowSource = 0;

function removeSourceLine(line) {
	$('#' + line).remove();
}

function addRowSourceTable(Form) {
	if ($(Form + " .inputLogin").val() == "" && $(Form + " .inputSearch").val() == "") {
		return false
	}

	var textefinal = "";

	var tdicone = "";
	if (Form == "#formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "#formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "#formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

	var text = "";
	var tdsource = "";
	if ($(Form + " .inputLogin").val() != "" && Form != "#formRSS") {
		text = $(Form + " .inputLogin").val();
		tdsource = '<td><i class="icon-user span1" style="padding-top:5px;"></i><div class="span8 breakword" contenteditable>' + text;
		textefinal = "<tr class='user' id='idsource" + idRowSource + "'>";
	} else if ($(Form + " .inputSearch").val() != "") {
		text = $(Form + " .inputSearch").val();
		tdsource = '<td><i class="icon-search span1" style="padding-top:5px;"></i> <div class="span8 breakword" contenteditable>' + text;
		textefinal = "<tr class='search' id='idsource" + idRowSource + "'>";
	}

	tdaction = '</div><button class="btn pull-right btn-danger" onclick="removeSourceLine(\'idsource' + idRowSource + '\')"><i class="icon-remove icon-white" ></i></button></td>';
	idRowSource += 1;

	textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";
	$('#sourceTable').append(textefinal);
	rebootForm(Form);
}

function rebootForm(Form) {
	$(Form + " .inputLogin").val('');
	$(Form + " .inputSearch").val('');
}

function addAllSourceTable(Form) {
	var textefinal = "";

	var tdicone = "";
	if (Form == "formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="twitter"></td>';
	else if (Form == "formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="google"></td>';
	else if (Form == "formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/><input class="valSource" type="hidden" value="rss"></td>';

	var tdsource = "<td><i><div class='breakword'>Toutes les sources</div></i>";

	textefinal = "<tr class='search' id='idsource" + idRowSource + "'>";

	tdaction = '<button class="btn pull-right btn-danger"  onclick="removeSourceLine(\'idsource' + idRowSource + '\')"  ><i class="icon-remove icon-white"></i></button></td>';
	idRowSource += 1;

	textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";
	$('#sourceTable').append(textefinal);
	rebootForm(Form);
}

function rebootSourceTable() {
	$('#sourceTable tbody > tr').each(function() {
		$(this).remove();
	});
}

function rebootFilteringTable() {
	$('#filteringTable tbody > tr').each(function() {
		$(this).remove();
	});
}

function rebootKeywordTable() {
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});
}

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

function setButtonNextTab() {
	$('#btnTabSource').hide();
	$('#btnTabFiltrage').show();
	$('#btnGoReador').hide();
	$('#myModalLabel').text('Recherche sémantique : Sources');
}

function setButtonPreviousTab() {
	$('#btnTabFiltrage').hide();
	$('#btnGoReador').show();
	$('#btnTabSource').show();
	$('#myModalLabel').text('Recherche sémantique : Filtres');
}

function movingData() {
	var arraySourceAdded = new Array();
	var arrayFilteringAdded = new Array();
	var littleArray = new Array();
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
	$('#movedData').html($('#movedData').html() + "<input name='arraySource' type='hidden' value='" + arraySourceAdded + "'>" + "<input name='arrayFiltering' type='hidden' value='" + arrayFilteringAdded + "'>");
	return false;
}
