//var url = "http://fofobar.com/client";
var url = "/client";

$("#verify").click(function() {
    $.get( url+"/verify", function( data ) {
      console.log(data);
    });
});
