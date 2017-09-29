function waitAndRefresh() {
	chrome.extension.getBackgroundPage().window.setTimeout(chrome.extension.getBackgroundPage().startRequest, 700);
}

function htmlDecode(input){
	var e = document.createElement('div');
	e.innerHTML = input;
	return e.childNodes[0].nodeValue;
}

function initBehaviour() {
	if (getPref(USE_DIRECT_LINK)) {
		chrome.browserAction.setPopup({popup:""});
	} else {
		chrome.browserAction.setPopup({popup:"pop.html"});
	}
	if (getPref(USE_CONTEXT_MENU)) {
		chrome.contextMenus.create({title:chrome.i18n.getMessage("refresh_menu_label", chrome.extension.getBackgroundPage().site.name), onclick:startRequest});
	} else {
		chrome.contextMenus.removeAll();
	}
}

function initBrowserActionTitle() {
	var tooltip;
	if (getPref(ONLY_FAVS)) {
		tooltip = chrome.i18n.getMessage('bookmarks');
	} else {
		tooltip = chrome.i18n.getMessage('cyan_flags');
	}
	chrome.browserAction.setTitle({title:tooltip});
}

function goToPage(url, readNewTabPref) {
	if (readNewTabPref == null) readNewTabPref = false;
		chrome.tabs.query({currentWindow: true}, function(tabs){

		for (var i = 0, tab; tab = tabs[i]; i++) {
			if (tab.url && tab.url == url) {
				chrome.tabs.update(tab.id, {selected: true, url:url});
				return;
			}
		}
		if (readNewTabPref && !getPref(NEW_TAB)) {
			chrome.tabs.getSelected(undefined, function(tab){
				chrome.tabs.update(tab.id, {selected: true, url:url});
			});
		} else {
			chrome.tabs.create({url: url});
		}
	});
	waitAndRefresh();
}

function goToHfr(){
	goToPage(getUsedURL(), true);
}

function openAll() {
	var popupContent = chrome.extension.getBackgroundPage().popupContent;
	var entry;
	if (popupContent.entries.length < getPref(MAX_OPEN_ALL) || confirm(chrome.i18n.getMessage('too_many_new_tabs', String(popupContent.entries.length)))) {
		for (var i = 0; i < popupContent.entries.length; i++) {
			entry = popupContent.entries[i];
			goToPage(getFullUrl(htmlDecode(entry.href)), false);
		}
	}
	waitAndRefresh();
}

function openCat(cat) {
	if (getPref(OPEN_CAT)) {
		var popupContent = chrome.extension.getBackgroundPage().popupContent;
		var entry;
		var entries = new Array();
		for (var i = 0; i < popupContent.entries.length; i++) {
			entry = popupContent.entries[i];
			if (entry.cat == cat) {
				entries.push(getFullUrl(htmlDecode(entry.href)));
			}
		}
		
		if (entries.length < getPref(MAX_OPEN_ALL) || confirm(chrome.i18n.getMessage('too_many_new_tabs', String(entries.length)))) {
			for (var i = 0; i < entries.length; i++) {
				goToPage(entries[i], false);
			}
		}
		waitAndRefresh();
	} else {
		var bgPage = chrome.extension.getBackgroundPage();
		var site = bgPage.site;
		goToPage(site.getOwnCatUrl(cat), false);
	}
}

function updateBadge(nbUnread) {
	if (nbUnread && nbUnread != null && parseInt(nbUnread) != NaN && parseInt(nbUnread) > 0) {
		chrome.browserAction.setBadgeText({text:""+nbUnread});
		if (getPref(ANIMATED_ICON)) animateFlip();
	} else if (nbUnread != null && parseInt(nbUnread) == 0){
		chrome.browserAction.setBadgeText({text:""});
	} else if (nbUnread != null && nbUnread.length > 0){
		chrome.browserAction.setBadgeText({text:nbUnread});
	} else {
		chrome.browserAction.setBadgeText({text:"..."});
	}
}

/*to rotate the chrome action icon, (not so) shamelessly copied from google Examples*/
	
var rotation = 0;
var animationFrames = 36;
var animationSpeed = 10; // ms


function ease(x) {
	return (1-Math.sin(Math.PI/2+x*Math.PI))/2;
}

function animateFlip() {
	rotation += 1/animationFrames;
	drawIconAtRotation();

	if (rotation <= 1) {
		setTimeout(animateFlip, animationSpeed);
	} else {
		rotation = 0;
		drawIconAtRotation();
	}
}

function drawIconAtRotation() {
	var bgPage = chrome.extension.getBackgroundPage();
	var canvas = bgPage.canvas;
	var canvasContext = bgPage.canvasContext;
	var iconImage = bgPage.iconImage;
	canvasContext.save();
	canvasContext.clearRect(0, 0, canvas.width, canvas.height);
	canvasContext.translate(
			Math.ceil(canvas.width/2),
			Math.ceil(canvas.height/2));
	canvasContext.rotate(2*Math.PI*ease(rotation));
	canvasContext.drawImage(iconImage,
			-Math.ceil(canvas.width/2),
			-Math.ceil(canvas.height/2));
	canvasContext.restore();

	var imageData = canvasContext.getImageData(0, 0, canvas.width,canvas.height);
	if (imageData instanceof ImageData)
		chrome.browserAction.setIcon({imageData: imageData});
}
