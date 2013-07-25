//When the document is ready, prepare and initiate different function to the object of the modal
$(document).ready(function() {
    //Reminder box in the item page
    $("#reminder").hover(function() {
    }, function() {
        $("#openReminder").fadeIn();
        $("#reminder").css("left", "-37%");
    });

    $("#openReminder").hover(function(){
        $("#openReminder").fadeOut();
        $("#reminder").css("transition", "all 0.5s ease-in");
        $("#reminder").css("left", "-1%");
    },function(){});
});

function showUpdate(){
	var timer = setInterval(function(){
		if($("#updateSearch").css("opacity")==1)
			$("#updateSearch").css("opacity","0");
		else
			$("#updateSearch").css("opacity","1");
	},500);
	setTimeout(function(){
		clearInterval(timer);
		$("#updateSearch").css("opacity","1");
	},2000);
}