//= require jquery
//  require_tree .
//=  require jquery_ujs
//=  require jquery.ui.all
//= require jquery.remotipart
//= require i18n
//= require i18n/translations
//= require jquery.inview.js

//Prepare the input with all data
//Read all the tag selected ( sources and filters ) and create input with the map corresponding to these tags
//Generate one hashmap for sources ( name : source) and one hashmap for filters ( name : filtering )
function movingData(itemId) {
	 window.onbeforeunload = "";
    $("#movedData").html("");
    var source = {};
    var filtering = {};

    var tabTwitter = [];
    var tabRss = [];
    var item = ""
    var url = ""

    //add the not finish added items
    if ($('#searchTwitter').val()!='' && $('#searchTwitter').val() != null) addSource('Twitter','#searchTwitter');
    if ($('#loginTwitter').val()!='' && $('#searchTwitter').val() != null) addSource('Twitter','#loginTwitter');
    if ($('#searchRSS').val()!='' && $('#searchTwitter').val() != null) addSource('RSS','#searchRSS');
    if ($("#keyword").val() != '' && $('#searchTwitter').val() != null)addFilter('and');

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
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
        
        url = $(this).attr("filter-uri");
        if (url != "" && url != "undefined")
        	tabItem.uri =url;
        
        tabOr.push(tabItem);
    });
    $("#summaryOr").children().each(function() {
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
         
        url = $(this).attr("filter-uri");
        if (url != "" && url != "undefined")
        	tabItem.uri = url;
        
        tabOr.push(tabItem);
    });
    $("#wellAnd").children().each(function() {
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
        
       	url = $(this).attr("filter-uri");
       	if (url != "" && url != "undefined")
        	tabItem.uri = url;
        
        tabAnd.push(tabItem);
    });
    $("#summaryAnd").children().each(function() {
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
        
        url = $(this).attr("filter-uri");
        if (url != "" && url != "undefined")
       		tabItem.uri = url;
        
        tabAnd.push(tabItem);
    });
    $("#wellWithout").children().each(function() {
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
        
        url = $(this).attr("filter-uri");
        if (url != "" && url != "undefined")
        	tabItem.value = url;
        
        tabWithout.push(tabItem);
    });
    $("#summaryWithout").children().each(function() {
    	var tabItem ={};
        item = encodeURI($(this).html().substr(0, $(this).html().search('<i') - 1));
        item = item.replace(/'/g, "\\&#39;");
        tabItem.value = item;
        
        url = $(this).attr("filter-uri");
        if (url != "" && url != "undefined")
        	tabItem.uri = url;  
        
        tabWithout.push(tabItem);      
    });

    source.twitter = tabTwitter;
    source.rss = tabRss;

    filtering.or = tabOr;
    filtering.and = tabAnd;
    filtering.without = tabWithout;

    inputDatas = "<input name='sources' type='hidden' value='" + JSON.stringify(source) + "'>" + "<input name='filters' type='hidden' value='" + JSON.stringify(filtering) + "'><input name='itemId' type='hidden' value='" + itemId + "'>";
    $('#movedData').html($('#movedData').html() + inputDatas);

	$('.formModal').submit();
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

    //check if putting the item in another item
    var target = ev.target;
    if($(target).hasClass('label')){
        target = $(target).parent()[0];
    }

	target.appendChild(document.getElementById(data));
	showUpdate();
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
	$(".well-info, .well-success, .well-danger").children().each(function(){
		if ( idbasic <= parseInt($(this).attr("id").substring("filter".length)))
			idbasic = parseInt($(this).attr("id").substring("filter".length)) +1;
	});
	return idbasic;
}

//Extrait filename d'un input file
function extractFilename(path) {
  if (path.substr(0, 12) == "C:\\fakepath\\")
    return path.substr(12); // modern browser
  var x;
  x = path.lastIndexOf('/');
  if (x >= 0) // Unix-based path
    return path.substr(x+1);
  x = path.lastIndexOf('\\');
  if (x >= 0) // Windows-based path
    return path.substr(x+1);
  return path; // just the filename
}

//Update the filename in the textfield
function updateFilename(path) {
   var name = extractFilename(path);
   	if(name != "")
		$('#fileTextName').html(name);
	else
		$('#fileTextName').html($('#fileTextName').attr('data-initial'));
}

function removeElement(id,e){
    e.stopPropagation();
    $("#filter"+id).remove();
    showUpdate();
}

// truncate the items text. Derived from jTruncate but adapted to Reador.net
// License : GPL. Author : Jeremy Martin.

(function($){
    $.fn.jTruncate=function(h){
        var i={length:300,minTrail:20,moreText:"more",lessText:"less",ellipsisText:"...",moreAni:"",lessAni:""};
        var h=$.extend(i,h);
        return this.each(function(){
            obj=$(this);
            var a=obj.html();
            if(a.length>h.length+h.minTrail){
                var b=a.indexOf(' ',h.length);
                if(b!=-1){
                    var b=a.indexOf(' ',h.length);
                    var c=a.substring(0,b);
                    var d=a.substring(b,a.length-1);
                    obj.html(c+'<span class="truncate_ellipsis">'+h.ellipsisText+'</span>'+'<span class="truncate_more">'+d+'</span>');
                    obj.find('.truncate_more').css("display","none");
                    obj.append('<a href="#" class="truncate_more_link">'+h.moreText+'</a>');
                    var e=$('.truncate_more_link',obj);
                    var f=$('.truncate_more',obj);
                    var g=$('.truncate_ellipsis',obj);
                    e.click(function(){
                        if(e.text()==h.moreText){
                            f.show(h.moreAni);
                            f.css("display","inline");
                            e.text(h.lessText);
                            g.css("display","none")
                        }else{
                            f.fadeOut(h.lessAni);
                            e.text(h.moreText);
                            g.css("display","inline")
                        }return false
                    })
                }
            }
        })
    }
})(jQuery);


function showUpdate(){
    var timer = setInterval(function(){
        if($(".updater").css("opacity")==1)
            $(".updater").css("opacity","0.5");
        else
            $(".updater").css("opacity","1");
    },300);
    setTimeout(function(){
        clearInterval(timer);
        $(".updater").css("opacity","1");
    },2000);
}

//Add a source in the table
function addSource(table, value) {
    var reg = new RegExp('[, ]+');
    text = $(value).val().replace(/^\s+/g,'').replace(/\s+$/g,'');
    text = text.split(reg);

    var eraseInput = true;
    if (table == "Twitter") {
        if (value == "#searchTwitter"){
            $.each(text,function(){
                content = "#" + this;
                $("#wellSources").append("<span class='label label-info twitterSource'>" + content + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();checkWell()'></i></span> ");
            })
        }else{
            $.each(text,function(){
                content = "@" + this;
                $("#wellSources").append("<span class='label label-info twitterSource'>" + content + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").remove();checkWell()'></i></span> ");
            })
        }
        $("#addAllTwitter").attr("disabled", true);
    } else if (table == "RSS") {
        $.each(text,function(){
            if ( ( this.match("^http://") && this != "http://" ) || ( this.match("^https://") && this != "https://" ) ){
                $("#wellSources").append("<span class='label-wrap label label-warning rssSource' >" + this + " <i class='icon-remove pointerMouse' onclick='$(this).closest(\"span\").next(\"br\").remove();$(this).closest(\"span\").remove();checkWell()'></i></span> ");
                $("#addAllRSS").attr("disabled", true);
            } else if(text.length==1){
                //If length==1,meaning that there is only one url in the input, we keep it so that the user can change it
                //If length > 1 , we just skip the url and don't put it in the sources
                eraseInput=false;
                return false;
            }
        });
    }
    if (eraseInput){
        $(value).val("");
        $(value).html("");
    }
}