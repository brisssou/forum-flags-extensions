var requestFailureCount = 0;    // used for exponential backoff
var requestTimeout = 1000 * 2;    // 2 seconds


var bg = chrome.extension.getBackgroundPage();

function clear(element) {
    while (element.firstChild) {
        element.removeChild(element.firstChild);
    }
}

function getUsedURL() {
    return getPref(ONLY_FAVS) ? bg.site.getFavsUrl():bg.site.getDrapsUrl();
}

function getFullUrl(url) {
    return bg.site.getFullUrl(url);
}

function getDefaultColorFromTheme(onSuccess, onError) {
    var xhr = new XMLHttpRequest();
    var abortTimerId = bg.window.setTimeout(function() {
            xhr.abort();
        }, requestTimeout);

    function handleSuccess(color) {
        requestFailureCount = 0;
        bg.window.clearTimeout(abortTimerId);
        if (onSuccess)
            onSuccess(color);
    }

    function handleError() {
        ++requestFailureCount;
        bg.window.clearTimeout(abortTimerId);
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
    debug("requesting unread");
    var xhr = new XMLHttpRequest();
    var abortTimerId = bg.window.setTimeout(function() {
            xhr.abort();    // synchronously calls onreadystatechange
        }, requestTimeout);

    function handleSuccess(count) {
        debug("Handling success");
        requestFailureCount = 0;
        bg.window.clearTimeout(abortTimerId);
        if (onSuccess) {
            debug("Should now really go to success");
            onSuccess(count);
        }
    }

    function handleError() {
        debug("Handling error");
        ++requestFailureCount;
        bg.window.clearTimeout(abortTimerId);
        if (onError)
            onError();
    }

    try {
        updateBadge();
        xhr.onreadystatechange = function(){
            if (xhr.readyState != 4) {
                debug("state was not 4, it was "+xhr.readyState);
                return;
            }
            debug("state was 4");
            var site = bg.site;
            if (xhr.responseText) {
                debug("There is a responseText  - size="+xhr.responseText.length);
                var content = xhr.responseText;
                if (site.notConnectedRex.exec(content)) {
                    updateBadge('x');
                    debug("nUser not connected");
                    handleError();
                } else {

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
                        debug("parsing topics");
                        var unreads = site.parseUnread(content, getPref(MUTED_TOPICS).split('|'));
                        debug("done parsing topics");
                        popupContent.addAll(unreads);
                        unreadCount = unreads.length;
                    }
                    if (getPref(GET_MPS)) {
                        debug("parsing mps");
                        if (site.parsableMpsUrl) {
                            debug("using specific mp url");
                            var synch = new XMLHttpRequest();
                            synch.open("GET", site.getFullUrl(site.parsableMpsUrl), false);
                            synch.send(null);
                            content = synch.responseText;
                        }
                        var mpsNb = site.parseMps(content);
                        debug("done parsing mps");
                        unreadCount += mpsNb;
                        popupContent.setMps(mpsNb);
                        if (mpsNb > 0) {
                            badgeBackGroundColor = [0,0,255,255];
                        } else {
                            badgeBackGroundColor = [255,0,0,255];
                        }
                    }
                    chrome.browserAction.setBadgeBackgroundColor({color:badgeBackGroundColor});
                    debug("should now handle success");
                    handleSuccess(unreadCount);
                }
                return;
            }
            debug("no response text...");
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

function scheduleRequest(when) {
    debug("Scheduling request");
    var bgPage = bg;
    if (bgPage.requestTimeoutId.length != 0) {
        for (var i = 0; i < bgPage.requestTimeoutId.length; bgPage.window.clearTimeout(bgPage.requestTimeoutId[i++]));
        bgPage.requestTimeoutId = new Array();
        debug("requestTimeoutId was not null");
    }
    if (!when) {
        when = getPref(REFRESH_TIME);
    }
    bgPage.requestTimeoutId.push(bgPage.window.setTimeout(startRequest, when * 1000));
    debug("request scheduled for " + when + "s");
    debug("Next request will be on "+ new Date(new Date().getTime() + when * 1000));
}

function startRequest() {
    debug("Starting request");
    scheduleRequest();
    getUnreadCount(
        function(count) {
            debug("Everything was successful");
            //loadingAnimation.stop();
            updateBadge(count);
            // si initPopup existe, ça veut dire que la fonction a été appellée depuis la popup, il est de bon aloi de mettre à jour son contenu
            var popup = chrome.extension.getViews({type:'popup'})[0];
            if (popup != null) popup.initPopup();
        },
        function() {
            debug("something REALLY bad happened");
            scheduleRequest(5);
            //loadingAnimation.stop();
            //showLoggedOut();
        }
    );
}
