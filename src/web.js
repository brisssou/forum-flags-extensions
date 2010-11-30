var HFR_MY_DRAPS = "http://forum.hardware.fr/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = "http://forum.hardware.fr/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";

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
    return "sujetCase7";
  }
}

function goToPage(url) {
  chrome.tabs.getAllInWindow(undefined, function(tabs){

  for (var i = 0, tab; tab = tabs[i]; i++) {
      if (tab.url && tab.url == url) {
        chrome.tabs.update(tab.id, {selected: true});
        return;
      }
    }
    chrome.tabs.create({url: url});
  });
}

function goToHfr(){
  goToPage(getUsedURL());
}

function updateBadge(nbUnread) {
  if (nbUnread && nbUnread != null && parseInt(nbUnread) != NaN && parseInt(nbUnread) > 0) {
    chrome.browserAction.setBadgeText({text:""+nbUnread});
  } else {
    chrome.browserAction.setBadgeText({text:""});
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
		    while (lastIndx !=-1) {
			    unreadCount++;
			    lastIndx = content.indexOf(patern, lastIndx + 1);
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

function init() {
  if (getPref(USE_DIRECT_LINK_PREF)) {
  	chrome.browserAction.setPopup({popup:""});
  } else {
  	chrome.browserAction.setPopup({popup:"pop.html"});
  }
}