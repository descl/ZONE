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
    
    changeItemFormat('card');
});


//Add the tag to the summary panel
function addTag(type, value) {
    if (type == 'opt')
        $(".well-info").append('<span class="label label-info">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span> ');
    else if (type == 'must')
        $(".well-success").append('<span class="label label-success">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span> ');
    else if (type == 'no')
        $(".well-danger").append('<span class="label label-danger">' + value + ' <i class="icon-remove" onclick="$(this).closest(&quot;span&quot;).remove();showUpdate()"></i></span> ');
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

        $(".row-list").hide();
        $(".showFavorite").show();
    }

}