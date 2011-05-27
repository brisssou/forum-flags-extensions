var HFR = "http://forum.hardware.fr";
var HFR_MY_DRAPS = HFR + "/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR + "/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";
var HFR_MP = HFR + "/forum1f.php?config=hfr.inc&cat=prive";
var HFR_SETUP_THEME = HFR + "/setperso.php?config=hfr.inc";
//</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=165&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">165</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=158&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t627721"><img src="http://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.158)
var UNREAD_REX = /title="Sujet n.\d+">([^<]+).+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
var NB_PAGES_REX = /cCatTopic">(\d+)<\/a>/;
var MP_REX = /class="red">Vous avez (\d*) nouveau/;
var BG_COLOR_REX = /<input name="inputcouleurTabHeader" .* value="(.*)"/

var CATS_MASTER_REX = /<select name="cat"(.+)<\/select>/;
var CATS_REX = /<option value="([^"]+)".*>([^"]+)<\/option>/g;

var ENTRY_URL_REX = /cat=(\d+)&amp;(subcat=(\d+)&amp;)?post=(\d+)&amp;page=(\d+)/;

var requestFailureCount = 0;  // used for exponential backoff
var requestTimeout = 1000 * 2;  // 2 seconds


var bg = chrome.extension.getBackgroundPage();

function getUsedURL() {
  if (getPref(ONLY_FAVS)) {
    return HFR_MY_FAVS;
  } else {
    return HFR_MY_DRAPS;
  }
}

function getFullUrl(url) {
  return HFR + url;
}

function getDefaultColorFromTheme(onSuccess, onError) {
  var xhr = new XMLHttpRequest();
  var abortTimerId = window.setTimeout(function() {
      xhr.abort();
    }, requestTimeout);

  function handleSuccess(color) {
    requestFailureCount = 0;
    window.clearTimeout(abortTimerId);
    if (onSuccess)
      onSuccess(color);
  }

  function handleError() {
    ++requestFailureCount;
    window.clearTimeout(abortTimerId);
    if (onError)
      onError();
  }

  try {
    xhr.onreadystatechange = function(){
      if (xhr.readyState != 4)
      return;

      if (xhr.responseText) {
        var content = xhr.responseText;
        var bgColor = BG_COLOR_REX.exec(content);
        if (bgColor != null) {
          debug("found "+bgColor[1]+" as the theme background color");
        }
        handleSuccess(bgColor[1]);
        return;
      }

      handleError();
    };

    xhr.onerror = function(error) {
      handleError();
    };

    xhr.open("GET", HFR_SETUP_THEME, true);
    xhr.send(null);
  } catch(e) {
    error(e);
    handleError();
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
        var popupContent = chrome.extension.getBackgroundPage().popupContent;
        popupContent.clear();
        var matches = null;
        var muted = getPref(MUTED_TOPICS).split('|');
        while (matches = UNREAD_REX.exec(content)) {
          debug("found one");
          var url = matches[2];
          var urlMatch = ENTRY_URL_REX.exec(url);
          if (urlMatch != null && !isMuted(urlMatch[1], urlMatch[4])) {
            unreadCount++;
            var topicNbPages = 1;
            var nbPages = null;
            if (nbPages = NB_PAGES_REX.exec(matches[0])) {
              topicNbPages = parseInt(nbPages[1]);
            }
            popupContent.add(matches[1], urlMatch[1], urlMatch[4], url, topicNbPages - parseInt(matches[3]));
          } else {
            debug("... but a muted one");
          }
        }
        var mps = MP_REX.exec(content);
        var badgeBackGroundColor;
        if (mps != null) {
          var mpsNb = parseInt(MP_REX.exec(content)[1]);
          if (mpsNb == NaN) mpsNb = 0;
          debug("found "+mpsNb+" private messages");
          unreadCount += mpsNb;
          popupContent.setMps(mpsNb);
          if (mpsNb > 0) {
        	  badgeBackGroundColor = [0,0,255,255];
          } else {
        	  badgeBackGroundColor = [255,0,0,255];
          }
        } else {
      	  badgeBackGroundColor = [255,0,0,255];
        }
        chrome.browserAction.setBadgeBackgroundColor({color:badgeBackGroundColor});
        if (bg.cats == null || bg.cats.length == 0) {
          bg.cats = new Array();
          matches = CATS_MASTER_REX.exec(content);
          if (matches!=null) {
            var catsString = matches[0];
            while (matches = CATS_REX.exec(catsString)) {
              bg.cats[matches[1]] = matches[2];
              debug("new cat :"+matches[1]+"/"+matches[2]);
            }
          }
        }
        handleSuccess(unreadCount);
        return;
      }

      handleError();
    };

    xhr.onerror = function(error) {
      handleError();
    };

    xhr.open("GET", getUsedURL(), true);
    xhr.send(null);
  } catch(e) {
    error(e);
    handleError();
  }
}

function scheduleRequest() {
  var bgPage = chrome.extension.getBackgroundPage();
  if (bgPage.requestTimeoutId.length != 0) {
    for (var i = 0; i < bgPage.requestTimeoutId.length;  bgPage.window.clearTimeout(bgPage.requestTimeoutId[i++]));
    bgPage.requestTimeoutId = new Array();
    debug("requestTimeoutId was not null");
  }
  bgPage.requestTimeoutId.push(bgPage.window.setTimeout(startRequest, getPref(REFRESH_TIME) * 1000));
  debug("request scheduled for " + getPref(REFRESH_TIME) + "s");
}

function startRequest() {
  getUnreadCount(
	function(count) {
	  //loadingAnimation.stop();
	  updateBadge(count);
	  // si initPopup existe, ça veut dire que la fonction a été appellée depuis la popup, il est de bon aloi de mettre à jour son contenu
	  var popup = chrome.extension.getViews({type:'popup'})[0];
	  if (popup != null) popup.initPopup();
	  scheduleRequest();
	},
	function() {
	  //loadingAnimation.stop();
	  //showLoggedOut();
	  scheduleRequest();
	}
  );
}
