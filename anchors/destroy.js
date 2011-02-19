$("a[name]").each(function() {
  var url;
  var regexS = "(^[^#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    url = "";
  else
    url = results[1];
  
  
  $(this).qtip("destroy")
});



$("*[id]").each(function() {
  var url;
  var regexS = "(^[^#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    url = "";
  else
    url = results[1];
  
  
  $(this).qtip("destroy")
});