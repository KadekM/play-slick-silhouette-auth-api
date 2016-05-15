//var url = "http://fofobar.com/client";
var url = "/client";

var $xauth = $("#xauth");

$("#verify").click(function() {
    $.get( url+"/verify", function( data ) {
      console.log(data);
    });
});

$("#verify-x").click(function() {
    $.ajax({
             url: url+"/verify",
             type: "GET",
             beforeSend: function(xhr){xhr.setRequestHeader('X-Auth-Token', $xauth.val());},
          }).done(function( msg ) {
                console.log(msg);
              });
        });
