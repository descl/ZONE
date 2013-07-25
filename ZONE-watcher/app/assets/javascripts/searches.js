/**
 *Source section
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
                source : "/complete_entities/" + $("#keyword").val() + ".json"
            });
        },
        select : function() {
            askUpdateKeywordTable();
        },
        minLength : 4
    }).autocomplete("widget").addClass("span1");


    $("#addAllRSS").attr("disable", false);
    $("#addAllTwitter").attr("disable", false);
});

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
        $("#wellSources").append("<span class='label label-info twitterSource'>" + text + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();checkWell()'></i></span> ");
        $("#addAllTwitter").attr("disabled", true);
    } else if (table == "RSS") {
        $("#wellSources").append("<span class='label-wrap label label-warning rssSource' >" + $(value).val() + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();checkWell()'></i></span> ");
        $("#addAllRSS").attr("disabled", true);
    }
    $(value).val("");
    $(value).html("");
}

//add "all" sources in the table
function addAllSource(table, value) {
    if (table == "Twitter") {
        $("#wellSources").append("<span class='label label-info twitterSource'>" + value + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();$(\"#loginTwitter\").attr(\"disabled\",false);$(\"#searchTwitter\").attr(\"disabled\",false);checkWell(\"Twitter\")'></i></span> ");
        $("#searchTwitter").attr("disabled", true);
        $("#loginTwitter").attr("disabled", true);
        $("#searchTwitter").val("").html("");
        $("#loginTwitter").val("").html("");
        $("#addAllTwitter").attr("disabled", true);
    } else if (table == "RSS") {
        $("#wellSources").append("<span class='label label-warning label-wrap rssSource'>" + value + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();$(\"#searchRSS\").attr(\"disabled\",false);checkWell(\"RSS\")'></i></span> ");
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
        $('#wellAnd').append("<span class='label label-success'>" + motcle + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");
    else if (type == 'or')
        $('#wellOr').append("<span class='label label-info'>" + motcle + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");
    else if (type == 'without')
        $('#wellWithout').append("<span class='label label-danger'>" + motcle + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");

    $('#keywordTable tbody > tr > td > label > input').each(function() {
        if ($(this).is(':checked')) {
            if (type == 'and')
                $('#wellAnd').append("<span class='label label-success'>" + $(this).val() + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");
            else if (type == 'or')
                $('#wellOr').append("<span class='label label-info'>" + $(this).val() + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");
            else if (type == 'without')
                $('#wellWithout').append("<span class='label label-danger'>" + $(this).val() + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();'></i></span> ");
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
        $.getJSON('/linked_words/' + $('#keyword').val() + '.json', function(data) {
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