/** filtrage**/


idRowFiltrage = 0;

function changeDropdownKeyword(toto) {
    $('#dropdownKeyword').val(toto);
    $('#dropdownKeyword').html(toto);
}

function addFiltre() {
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
    $('#tablesynonyme tbody > tr > td > label > input').each(function() {
        if ($(this).is(':checked')) {
            motcle += ', ' + $(this).val();
        }
    });
    var attr = $('#dropdownKeyword').html();
    if($('#tablefiltrage tr').length ==1)
        attr="";
    $('#tablefiltrage').append('<tr id="idrow' + idRowFiltrage + '" class="' + classtr + '"><td hidden>' + id + '</td><td class="tdkey">' + attr + '</td><td class="tdval span3">' + motcle + '</td><td><button class="btn"><i class="icon-remove" onclick="removelinefiltrage(\'idrow' + idRowFiltrage + '\')"></i></button></td></tr>');
    idRowFiltrage += 1;

    rebootFiltrage();
//tritable();
    affichfiltre();
}

function rebootFiltrage() {
    $('#keyword').val('');
    $('#dropdownKeyword').text("OU");
    $('#dropdownKeyword').val("OU");
    unselect();
    $('#tablesynonyme tbody > tr').each(function() {
        $(this).remove();
    });
}

function tritable() {
    var rowCount = $('#tablefiltrage >tbody >tr').length;
    if (rowCount != 0) {
        $("#tablefiltrage").tablesorter({
            sortList : [[0, 0]]
        });
    }
}

function affichfiltre() {
    $("#areafiltre").val('');
    var i = 0;
    $('#tablefiltrage tbody > tr').each(function() {
        if (i == 0)
            $("#areafiltre").val($("#areafiltre").val() + $(this).find('td.tdval').html() + ' ');
        else
            $("#areafiltre").val($("#areafiltre").val() + $(this).find('td.tdkey').html() + ' ' + $(this).find('td.tdval').html() + ' ');
        i++;
    });

}

function select() {
    $('#tablesynonyme tbody > tr > td > label > input').each(function() {
        $(this).prop('checked', true);
    });
}

function unselect() {
    $('#tablesynonyme tbody > tr > td > label > input').each(function() {
        $(this).prop('checked', false);
    });
}

function removelinefiltrage(line) {
    $('#' + line).remove();
    var usersTable = $("#tablefiltrage");
    usersTable.trigger("update");

    affichfiltre();
}

function MAJtablesynonyme() {
    if ($('#keyword').val() == "") {
        $('#tablesynonyme tbody > tr').each(function() {
            $(this).remove();
        });
    }
    $('#fakesynonyme tbody > tr').each(function() {
        if ($('#keyword').val() == $(this).find('td.mot').html()) {
            $('#tablesynonyme').append('<tr><td><label class="checkbox"><input type="checkbox" value="' + $(this).find('td.synonyme').html() + '">' + $(this).find('td.synonyme').html() + '</label></td></tr>');
        }
    });
}



/** sources **/
idRowSource = 0;

function removelinesource(line) {
    $('#' + line).remove();
}

function addRow(Form) {
    if (Form.elements[0].value == "" && Form.elements[1].value == "") {
        return false
    }

    var textefinal = "";

    var tdicone ="";
    if (Form.name == "formTwitter")
        tdicone = '<td><img class="imagerondepetite sortable" src="/assets/logotwitter.jpg" width="40" height="40";/></td>';
    else if (Form.name == "formGoogle")
        tdicone =  '<td><img class="imagerondepetite sortable" src="/assets/logog+.jpg" width="40" height="40";/></td>';
    else if (Form.name == "formRSS")
        tdicone = '<td><img class="imagerondepetite sortable" src="/assets/logorss.jpg" width="40" height="40";/></td>';

    var text = "";
    var tdsource ="";
    if (Form.elements[0].value != "" && Form.name != "formRSS") {
        text = coupeText(Form.elements[0].value);
        tdsource = '<td><i class="icon-user"></i>' + text ;
        textefinal = "<tr class='user' id='idsource"+idRowSource+"'>";
    } else if (Form.elements[1].value != "") {
        text = coupeText(Form.elements[1].value);
        tdsource = '<td><i class="icon-search"></i> ' + text ;
        textefinal= "<tr class='search' id='idsource"+idRowSource+"'>";
    }

    tdaction = '<button class="btn pull-right btn-danger"><i class="icon-remove icon-white" onclick="removelinesource(\'idsource' + idRowSource + '\')"></i></button> <a href="#myModal" role="button" class="btn pull-right btn-warning" data-toggle="modal" onclick="prepareModal(\'' + Form.name + '\',this.parentNode.parentNode.rowIndex)"><i class="icon-cog" ></i></a></td>';
    idRowSource +=1;

    textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";
    $('#tablesource').append(textefinal);
    rebootForm(Form);
}

function rebootForm(Form) {
    var key = 0;
    for ( key = 0; key < Form.elements.length; key++) {
        Form.elements[key].value = "";
    }
}

function coupeText(texte) {
    var newTexte = "";
    for (var i = 0; i < texte.length; i++) {
        newTexte += texte.charAt(i);
        if (i % 30 == 0 && i != 0) {
            newTexte += "<br>";
        }
    }
    return newTexte;
}

function prepareModal(nom, nbline, type, valeurtexte) {
    $('#modalparpseudo').prop('visible',true);

    if (nom == "formTwitter") {
        $('#myModalLabel').html('Modification Twitter');
    } else if (nom == "formGoogle") {
        $('#myModalLabel').html('Modification Google');
    } else if (nom == "formRSS") {
        $('#myModalLabel').html('Modification RSS');
        $('#modalpseudo').val('');
        $('#modalparpseudo').css("visibility", "hidden");
    }

    $('#modalnbline').val(nbline);
    var type = document.getElementById("tablesource").rows[nbline].className;

    if (type == "user") {
        $('#modalpseudo').val(document.getElementById("tablesource").rows[nbline].cells[1].innerText);
        $('#modalpseudo').prop('readonly',false);
        $('#modalpseudo').prop('required',true);
        $('#modalrecherche').val('');
        $('#modalrecherche').prop('readonly',true);
        $('#modalrecherche').prop('required',false);
    } else if (type == "search") {
        $('#modalrecherche').val(document.getElementById("tablesource").rows[nbline].cells[1].innerText);
        $('#modalrecherche').prop('readonly',false);
        $('#modalrecherche').prop('required',true);
        $('#modalpseudo').val('');
        $('#modalpseudo').prop('readonly',true);
        $('#modalpseudo').prop('required',false);
    }
}

function modifLine() {
    var nbline = $('#modalnbline').val();
    var table = document.getElementById("tablesource");
    var text = "";

    if($('#modalpseudo').val() != ""){
        text = '<i class="icon-user"></i> ' + $('#modalpseudo').val();
        table.rows[nbline].className = "user";
    } else if ($('#modalrecherche').val() !=""){
        text = '<i class="icon-search"></i> ' + $('#modalrecherche').val();
        table.rows[nbline].className = "search";
    } else {
        return false;
    }
    var tdaction =table.rows[nbline].cells[1].innerHTML.split('<button');
    table.rows[nbline].cells[1].innerHTML = text + "<button " + tdaction[1];
}

function addAll(Form) {
    var textefinal = "";

    var tdicone ="";
    if (Form.name == "formTwitter")
        tdicone = '<td><img class="imagerondepetite sortable" src="/assets/logotwitter.jpg" width="40" height="40";/></td>';
    else if (Form.name == "formGoogle")
        tdicone =  '<td><img class="imagerondepetite sortable" src="/assets/logog+.jpg" width="40" height="40";/></td>';
    else if (Form.name == "formRSS")
        tdicone = '<td><img class="imagerondepetite sortable" src="/assets/logorss.jpg" width="40" height="40";/></td>';

    var tdsource = "<td><i>Toutes les sources</i>";

    textefinal= "<tr class='search' id='idsource"+idRowSource+"'>";

    tdaction = '<button class="btn pull-right btn-danger"><i class="icon-remove icon-white" onclick="removelinesource(\'idsource' + idRowSource + '\')"  ></i></button></td>';
    idRowSource += 1;

    textefinal = textefinal + tdicone + tdsource + tdaction + "/<tr>";
    $('#tablesource').append(textefinal);
    rebootForm(Form);
}