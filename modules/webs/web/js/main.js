//var url = "http://fofobar.com/auth";
var url = "/auth";
var $token = $("#token");
var $rememberMe = $("#remember_me");

$("#create_user").click(function() {
    $.ajax({ url: url+"/users",
             type: "POST",
             dataType: 'json',
             contentType: "application/json; charset=utf-8",
             data: JSON.stringify({"firstName": "Marek", "lastName": "Something", "identifier": "marek@foo.bar"})
             })
      .done(function( data ) {
        console.log(data);
        $token.val(data.details.token)
      });
});

$("#create_password").click(function() {
    $.ajax({ url: url+"/tokens/"+$token.val(),
             type: "POST",
             dataType: 'json',
             contentType: "application/json; charset=utf-8",
             data: JSON.stringify({"password": "somestrongpassword123!"})
             })
      .done(function( data ) {
        console.log(data);
        $token.val("data.details.token")
      });
});

$("#verify").click(function() {
    $.get( url+"/verify", function( data ) {
      console.log(data);
    });
});

$("#signout").click(function() {
    $.get( url+"/signout", function( data ) {
      console.log(data);
    });
});

$("#login").click(function() {
    $.ajax({ url: url+"/signin/credentials",
             type: "POST",
             dataType: 'json',
             contentType: "application/json; charset=utf-8",
             data: JSON.stringify({ identifier: "marek@foo.bar",
              password: "somestrongpassword123!", rememberMe: $rememberMe.is(':checked') })
             })
      .done(function( data ) {
        console.log(data)
      });
});
