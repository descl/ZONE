$(document).ready(function() {
	$(".items-box").hide();
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
	
	$('.label-tag').on('click', function () {
		$('.label-tag').not(this).popover('hide');
		$(this).popover('toggle');
		$('.popover').find('.textContent').jTruncate({
			length: 200,  
	        minTrail: 0,  
	        moreText: "(...)",  
	        lessText: "[-]",  
	        ellipsisText: "",  
	        moreAni: "fast",  
	        lessAni: "fast" 
		})
	});
	
	$(".items-box").fadeIn();
});


//Add the tag to the summary panel
function addTag(type, value, uri) {
	var id = getId();
    if (type == 'opt')
        $(".well-info").append('<span id="'+id+'" class="label label-info" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value+ ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span>');
    else if (type == 'must')
        $(".well-success").append('<span id="'+id+'" class="label label-success" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span>');
    else if (type == 'no')
        $(".well-danger").append('<span id="'+id+'" class="label label-danger" draggable="true" ondragstart="drag(event)" filter-uri=\"'+uri+'\">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span>');
    return false;
};

//Close all the popover after clicking on a selction
function closePop() {
    $(".label-tag").popover('hide');
    $('#openReminder').popover('show');
    setTimeout(function() {
        $('#openReminder').popover('hide');
    }, 5000);
    return false;
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
        $('.item-bloc').removeClass('longWell');

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
        $('.item-bloc').addClass('longWell');

        $(".row-list").hide();
        $(".showFavorite").show();
    }

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

//Function that delete a tag
function deleteTag(tag){
	$(".label-tag").popover('hide');
	$('#modalDeleteTag').modal('show');
}

//Function that edit a tag
function editTag(tag){
	$(".label-tag").popover('hide');
	$("#inputEditTag").attr("placeholder",tag);
	$('#modalEditTag').modal('show');
}

function addFavorite(item){
    var url = "/favorites/create?favorite="+item;
    $.ajax({
        url : url,
        success: function(){
            $(".btnFavorite").html = "ttt";
        },
        error: function (xhr, msg, ex)
        {
            alert("Failed: " + msg);
        }
    });
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
