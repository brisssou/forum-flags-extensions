var requestFailureCount = 0;	// used for exponential backoff
var requestTimeout = 1000 * 2;	// 2 seconds


var bg = chrome.extension.getBackgroundPage();

function getUsedURL() {
	return getPref(ONLY_FAVS) ? bg.site.getFavsUrl():bg.site.getDrapsUrl();
}

function getFullUrl(url) {
	return bg.site.getFullUrl(url);
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
				var site = bg.site;
				var content = xhr.responseText;
				var bgColor = site.parseBgColor(content);
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

		var site = bg.site;
		xhr.open("GET", site.getSetupUrl(), true);
		xhr.send(null);
	} catch(e) {
		error(e);
		handleError();
	}
}

function getUnreadCount(onSuccess, onError) {
	var xhr = new XMLHttpRequest();
	var abortTimerId = window.setTimeout(function() {
			xhr.abort();	// synchronously calls onreadystatechange
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
			var site = bg.site;
			if (xhr.responseText) {
				var content = xhr.responseText;
				//parse cats if needed
				if (bg.cats == null || bg.cats.length < 3) {
					bg.cats = site.parseCats(content);
				}
				// do nbunread
				var unreadCount = 0;
				var popupContent = bg.popupContent;
				popupContent.clear();
				var badgeBackGroundColor = [255,0,0,255];
				if (getPref(GET_TOPICS)) {
					var unreads = site.parseUnread(content, getPref(MUTED_TOPICS).split('|'));
					popupContent.addAll(unreads);
					unreadCount = unreads.length;
				}
				if (getPref(GET_MPS)) {
					
					if (site.parsableMpsUrl) {
						var synch = new XMLHttpRequest();
						synch.open("GET", site.getFullUrl(site.parsableMpsUrl), false);                             
						synch.send(null);
						content = synch.responseText;
					}
					var mpsNb = site.parseMps(content);
					unreadCount += mpsNb;
					popupContent.setMps(mpsNb);
					if (mpsNb > 0) {
						badgeBackGroundColor = [0,0,255,255];
					} else {
						badgeBackGroundColor = [255,0,0,255];
					}
				}
				chrome.browserAction.setBadgeBackgroundColor({color:badgeBackGroundColor});
				handleSuccess(unreadCount);
				return;
			} else {
				return;
			}

			handleError();
		};

		xhr.onerror = function(error) {
			handleError();
		};
		var url = getPref(ONLY_FAVS) ? bg.site.getFavsUrl():bg.site.getDrapsUrl();
		xhr.open("GET", url, true);
		xhr.send(null);
	} catch(e) {
		error(e.message);
		handleError();
	}
}

function scheduleRequest() {
	var bgPage = bg;
	if (bgPage.requestTimeoutId.length != 0) {
		for (var i = 0; i < bgPage.requestTimeoutId.length;	bgPage.window.clearTimeout(bgPage.requestTimeoutId[i++]));
		bgPage.requestTimeoutId = new Array();
		debug("requestTimeoutId was not null");
	}
	bgPage.requestTimeoutId.push(bgPage.window.setTimeout(startRequest, getPref(REFRESH_TIME) * 1000));
	debug("request scheduled for " + getPref(REFRESH_TIME) + "s");
	debug("Next request will be on "+ new Date(new Date().getTime() + getPref(REFRESH_TIME) * 1000));
}

function startRequest() {
	getUnreadCount(
		function(count) {
			scheduleRequest();
			//loadingAnimation.stop();
			updateBadge(count);
			// si initPopup existe, ça veut dire que la fonction a été appellée depuis la popup, il est de bon aloi de mettre à jour son contenu
			var popup = chrome.extension.getViews({type:'popup'})[0];
			if (popup != null) popup.initPopup();
		},
		function() {
			//loadingAnimation.stop();
			//showLoggedOut();
			scheduleRequest();
		}
	);
}
