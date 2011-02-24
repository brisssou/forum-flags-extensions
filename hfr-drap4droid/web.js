var HFR = "http://forum.hardware.fr";
var HFR_MY_DRAPS = HFR + "/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR + "/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";
var HFR_MP = HFR + "/forum1f.php?config=hfr.inc&cat=prive";

var UNREAD_REX = /title="Sujet n°\d+">([^<]+).+>(?:(\d+)<\/a>)*.+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
var MP_REX = /class="red">Vous avez (\d) nouveau/;
//<select name="cat" onchange="document.getElementById('goto').submit()"><option value="1" >Hardware</option><option value="16" >Hardware - Périphériques</option><option value="15" >Ordinateurs portables</option><option value="23" >Technologies Mobiles</option><option value="2" >Overclocking, Cooling &amp; Tuning</option><option value="25" >Apple</option><option value="3" >Video &amp; Son</option><option value="14" >Photo numérique</option><option value="5" >Jeux Video</option><option value="4" >Windows &amp; Software</option><option value="22" >Réseaux grand public / SoHo</option><option value="21" >Systèmes &amp; Réseaux Pro</option><option value="11" >OS Alternatifs</option><option value="10" >Programmation</option><option value="12" >Graphisme</option><option value="6" >Achats &amp; Ventes</option><option value="8" >Emploi &amp; Etudes</option><option value="9" >Seti et projets distribués</option><option value="13" >Discussions</option><option value="prive" >Messages privés</option></select><input type="hidden" name="config" value="hfr.inc" /><input type="submit" value="Go" /></div></form> 
var CATS_MASTER_REX = /<select name="cat"(.+)<\/select>/;
var CATS_REX = /<option value="([^"]+)" >([^"]+)<\/option>/g;

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
          var url = matches[3];
          var urlMatch = ENTRY_URL_REX.exec(url);
          if (urlMatch != null && !isMuted(urlMatch[1], urlMatch[4])) {
            unreadCount++;
            var topicNbPages;
            if (matches[2] == null) {
              topicNbPages = 1;
            } else {
              topicNbPages = matches[2];
            }
            popupContent.add(matches[1], urlMatch[1], urlMatch[4], url, parseInt(matches[2]) - parseInt(matches[4]));
          } else {
            debug("... but a muted one");
          }
        }
        var mps = MP_REX.exec(content);
        if (mps != null) {
          var mpsNb = parseInt(MP_REX.exec(content)[1]);
          if (mpsNb == NaN) mpsNb = 0;
          debug("found "+mpsNb+" private messages");
          unreadCount += mpsNb;
          popupContent.setMps(mpsNb);
        }
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
