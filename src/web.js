var HFR = "http://forum.hardware.fr";
var HFR_MY_DRAPS = HFR + "/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR + "/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";

var UNREAD_REX= /title="Sujet n�\d+">([^<]+).+sujetCase5"><a href="([^"]+)/g;

var requestFailureCount = 0;  // used for exponential backoff
var requestTimeout = 1000 * 2;  // 2 seconds


var bg = chrome.extension.getBackgroundPage();

function getUsedURL() {
  if (getPref(ONLY_FAVS_PREF)) {
    return HFR_MY_FAVS;
  } else {
    return HFR_MY_DRAPS;
  }
}

function getFullUrl(url) {
  return HFR + url;
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
		    popupContent.clear();
        var matches = null;
        while (matches = UNREAD_REX.exec(content)) {
			    unreadCount++;
          popupContent.add(matches[1], matches[2]);
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

	function scheduleRequest() {
		window.setTimeout(startRequest, getPref(REFRESH_TIME_PREF) * 1000);
	}
	
	function startRequest() {
	  getUnreadCount(
		function(count) {
		  //loadingAnimation.stop();
		  updateBadge(count);
		  scheduleRequest();
		},
		function() {
		  //loadingAnimation.stop();
		  //showLoggedOut();
		  scheduleRequest();
		}
	  );
	}