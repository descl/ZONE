$(document).ready(function() {
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

//Initiate the truncate of all text
$(window).load(function(){
	
	$(".contentArticle").jTruncate({
		length: 300,  
        minTrail: 0,  
        moreText: "(...)",  
        lessText: "[-]",  
        ellipsisText: "",  
        moreAni: "fast",  
        lessAni: "fast" 
	});
});

//Add the tag to the summary panel
function addTag(type, value, uri) {
    var searchId = "";
    var formZone = $($(".searchItem.active").attr("data-content"))
	var id = getId();
    if (type == 'opt')
        formZone.find(".well-info").append('<span id="filter'+id+'" class="label label-info" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value+ ' <i class="icon-remove pointerMouse" onclick="removeElement('+id+',event);"></i></span>');
    else if (type == 'must')
        formZone.find(".well-success").append('<span id="filter'+id+'" class="label label-success" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value + ' <i class="icon-remove pointerMouse" onclick="removeElement('+id+',event);"></i></span>');
    else if (type == 'no')
        formZone.find(".well-danger").append('<span id="filter'+id+'" class="label label-danger" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value + ' <i class="icon-remove pointerMouse" onclick="removeElement('+id+',event);"></i></span>');

    var formContent = $("<form/>").append(formZone.clone()).html()
    $(".searchItem.active").attr("data-content",formContent)

    showUpdate();
    return false;
};
function saveFormToHtml(doc){
    var contentToSave = $('.popover-content').children('p').html();
    var searchId = $(contentToSave).attr("id").substring("form-".length);
    $("#searchBox"+searchId).attr("data-content",contentToSave)
}

//Close all the popover after clicking on a selection
function openReminderOnChange() {
    var defaultItem = $('.searchItem.active');
    defaultItem.removeClass('active');

    var timer = setInterval(function(){
        if(defaultItem.hasClass('active'))
            defaultItem.removeClass('active')
        else
            defaultItem.addClass('active')
    },300);
    setTimeout(function(){
        clearInterval(timer);
        defaultItem.addClass('active')
    },2100);
    defaultItem.click();
    return false;
}

//Function that show the delete a tag pophover
function deleteTag(tag,tagUri,item){
	$(".label-tag").popover('hide');
	$('#modalDeleteTag').modal('show');

    $('#modalDeleteTagButton').attr('item', item);
    $('#modalDeleteTagButton').attr('tag', tag);
    $('#modalDeleteTagButton').attr('tagUri', tagUri);
}

//Function that delete a tag
function doDeleteTag(context){
    var item = context.attr("item");

    var tag = context.attr("tag");
    var tagUri = context.attr("tagUri");

    var url = "/items/deleteTag?item="+item+"&tag="+tag+"&tagUri="+tagUri;
    $.ajax({
        url : url,
        success: function(){
            htmlTag = $(".titleItem[href='"+item+"']").parent().children("div.btn-toolbar").children("div:contains('"+tag+"')");
            htmlTag.remove();
        },
        error: function (xhr, msg, ex)
        {
            alert("Failed to remove the tag");
        }
    });
    $('#modalDeleteTag').modal('hide');
}

function addFavorite(item,context){
    if($(context).hasClass('btnFavorite-on')){
        var url = "/favorites/delete?favorite="+item;
        $.ajax({
            url : url,
            success: function(){
                $(context).removeClass('btnFavorite-on');
            },
            error: function (xhr, msg, ex)
            {
                alert("Failed: " + msg);
            }
        });
    }else{
        var url = "/favorites/create?favorite="+item;
        $.ajax({
            url : url,
            success: function(){
                $(context).addClass('btnFavorite-on');
            },
            error: function (xhr, msg, ex)
            {
                alert("Failed: " + msg);
            }
        });
    }
+  $("#"+data).remove();
}

//check the opacity of an item
function checkOpacity(item){
	var parent = $(item).parents(".item-bloc");
	if ($(parent).css("opacity")=='0.5')
		$(parent).css("opacity",1);
	else
		$(parent).css("opacity",0.5);
}

//Add a tag to the item
function addItemTag(item){
	if ($(item).children('.addtag').is(":visible")){
        //hide
		$(item).children('.addtag').hide();
        $(item).children('.btn-addTag').show();
        $(item).show();

    }else{
        //show
		$(item).children('.addtag').show();
        $(item).children('.btn-addTag').hide();
    }
}

//do the add of a tag
function doAddItemTag(item,context){
    value = $(context).children(".tag").val();
    var url = "/items/addTag?item="+item+"&tag="+value;
    $.ajax({
        url : url,
        success: function(data){
            $(context).children(".tag").val("");
            $(context).parent().children('.addtag').hide();
            $(context).parent().children('.btn-addTag').show();
            $(context).parent().show();
            console.log(data);
            console.log($(context).parent().parent().children('.tags-toolbar'));
            $(context).parent().parent().children('.tags-toolbar').append(data);
        },
        error: function (xhr, msg, ex)
        {
            alert("Failed: " + msg);
        }
    });
}