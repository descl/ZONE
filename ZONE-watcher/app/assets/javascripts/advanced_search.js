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
	} else if ($('#dropdownKeyword').val() == "OU") {
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
	var attr = $('#dropdownKeyword').html();
	if ($('#filteringTable tr').length == 1)
		attr = "";
	$('#filteringTable').append('<tr id="idrow' + idRowFiltrage + '" class="' + classtr + '"><td hidden>' + id + '</td><td class="tdkey">' + attr + '</td><td class="span3"><div class="tdval span9 breakword">' + motcle + '</div><button class="btn btn-danger pull-right"  onclick="removelinefiltrage(\'idrow' + idRowFiltrage + '\')"><i class="icon-remove"></i></button></td></tr>');
	idRowFiltrage += 1;

	rebootFiltering();
	getFilter();
}

function rebootFiltering() {
	$('#keyword').val('');
	$('#dropdownKeyword').text("OU");
	$('#dropdownKeyword').val("OU");
	unselect();
	$('#keywordTable tbody > tr').each(function() {
		$(this).remove();
	});
}

function getFilter() {
	$("#filteringArea").val('');
	var i = 0;
	$('#filteringTable tbody > tr').each(function() {
		if (i == 0)
			$("#filteringArea").val($("#filteringArea").val() + $(this).find('div.tdval').html() + ' ');
		else
			$("#filteringArea").val($("#filteringArea").val() + $(this).find('td.tdkey').html() + ' ' + $(this).find('div.tdval').html() + ' ');
		i++;
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
	if (Form.elements[0].value == "" && Form.elements[1].value == "") {
		return false
	}

	var textefinal = "";

	var tdicone = "";
	if (Form.name == "formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/></td>';
	else if (Form.name == "formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/></td>';
	else if (Form.name == "formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/></td>';

	var text = "";
	var tdsource = "";
	if (Form.elements[0].value != "" && Form.name != "formRSS") {
		text = Form.elements[0].value;
		tdsource = '<td><i class="icon-user span1" style="padding-top:5px;"></i><div class="span8 breakword" contenteditable>' + text;
		textefinal = "<tr class='user' id='idsource" + idRowSource + "'>";
	} else if (Form.elements[1].value != "") {
		text = Form.elements[1].value;
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
	var key = 0;
	for ( key = 0; key < Form.elements.length; key++) {
		Form.elements[key].value = "";
	}
}

function addAllSourceTable(Form) {
	var textefinal = "";

	var tdicone = "";
	if (Form.name == "formTwitter")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundTwitter.jpg" width="40" height="40";/></td>';
	else if (Form.name == "formGoogle")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundGoogle.jpg" width="40" height="40";/></td>';
	else if (Form.name == "formRSS")
		tdicone = '<td><img class="littleCircleImage sortable" src="/assets/foregroundRSS.jpg" width="40" height="40";/></td>';

	var tdsource = "<td><i>Toutes les sources</i>";

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
	rebootForm(formTwitter);
	rebootForm(formRSS);
	rebootForm(formGoogle);
	rebootSourceTable();
	
	rebootFiltering();
	rebootFilteringTable();
	rebootKeywordTable();
	
	getFilter();
	
	$('#Sources').addClass('active');
	$('#Filtering').removeClass('active');
	
	setButtonNextTab();
}

function setButtonNextTab(){
	$('#btnTabSource').hide();
	$('#btnTabFiltrage').show();
	$('#btnGoReador').hide();
	$('#myModalLabel').text('Recherche sémantique : Sources');
}

function setButtonPreviousTab(){
	$('#btnTabFiltrage').hide();
	$('#btnGoReador').show();
	$('#btnTabSource').show();
	$('#myModalLabel').text('Recherche sémantique : Filtres');
}
