var HFR = "http://forum.hardware.fr";
var HFR_MY_DRAPS = HFR + "/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR + "/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";



var requestFailureCount = 0;  // used for exponential backoff
var requestTimeout = 1000 * 2;  // 2 seconds

//var UNREAD_QUERY = "['namespace-uri()='http://www.w3.org/1999/xhtml' and name()='td' and @class='sujetCase9']";

var bg = chrome.extension.getBackgroundPage();

function getUsedURL() {
  if (getPref(ONLY_FAVS_PREF)) {
    return HFR_MY_FAVS;
  } else {
    return HFR_MY_DRAPS;
  }
}

function getPatern() {
  if (getPref(ONLY_FAVS_PREF)) {
    return "/favoris.gif\" title=\"Aller au dernier message lu sur ce sujet (";
  } else {
    return "sujetCase5";
  }
}

function getUnreadCount(onSuccess, onError) {
  var xhr = new XMLHttpRequest();
  var abortTimerId = window.setTimeout(function() {
	    xhr.abort();  // synchronously calls onreadystatechange
    }, requestTimeout);

  function handleSuccess(count) {
	  requestFailureCount = 0;
	  window.clearTimeout(abortTimerId);
	  if (onSuccess)
	    onSuccess(count);
  }

  function handleError() {
	  ++requestFailureCount;
	  window.clearTimeout(abortTimerId);
	  if (onError)
	    onError();
  }

  try {
    updateBadge();
	  xhr.onreadystatechange = function(){
	    if (xhr.readyState != 4)
		  return;

	    if (xhr.responseText) {
		    var content = xhr.responseText;
		    var unreadCount = 0;
        var patern = getPatern();
		    var lastIndx = content.indexOf(patern, lastIndx);
        /*if (lastIndx !=-1 && !getPref(ONLY_FAVS_PREF)) {
          alert(content.substring(lastIndx + 21, content.indexOf("\">", lastIndx + 21)));
        }*/
		    while (lastIndx !=-1) {
			    unreadCount++;
			    lastIndx = content.indexOf(patern, lastIndx + 1);
        /*  if (lastIndx !=-1 && !getPref(ONLY_FAVS_PREF)) {
            alert(content.substring(lastIndx + 21, content.indexOf("\">", lastIndx + 21)));
          }*/
		    }
		    handleSuccess(unreadCount);
		    return;
	    }

	    handleError();
	  }

	  xhr.onerror = function(error) {
	    handleError();
	  }

	  xhr.open("GET", getUsedURL(), true);
	  xhr.send(null);
  } catch(e) {
	  console.error(e);
	  handleError();
  }
}
