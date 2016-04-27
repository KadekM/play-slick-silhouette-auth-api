$("#call").click(function() {
    $.get( "http://auth.fofobar.com:9000/verify", function( data ) {
      console.log(data);
    });
});
