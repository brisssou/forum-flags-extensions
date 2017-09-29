function waitAndRefresh() {
	browser.extension.getBackgroundPage().window.setTimeout(browser.extension.getBackgroundPage().startRequest, 700);
}

function htmlDecode(input){
	var e = document.createElement('div');
	e.innerHTML = input;
	return e.childNodes[0].nodeValue;
}

function initBehaviour() {
	if (getPref(USE_DIRECT_LINK)) {
		browser.browserAction.setPopup({popup:""});
	} else {
		browser.browserAction.setPopup({popup:"pop.html"});
	}
	if (getPref(USE_CONTEXT_MENU)) {
		browser.contextMenus.create({title:browser.i18n.getMessage("refresh_menu_label", browser.extension.getBackgroundPage().site.name), onclick:startRequest});
	} else {
		browser.contextMenus.removeAll();
	}
}

function initBrowserActionTitle() {
	var tooltip;
	if (getPref(ONLY_FAVS)) {
		tooltip = browser.i18n.getMessage('bookmarks');
	} else {
		tooltip = browser.i18n.getMessage('cyan_flags');
	}
	browser.browserAction.setTitle({title:tooltip});
}

function goToPage(url, readNewTabPref) {
	if (readNewTabPref == null) readNewTabPref = false;
		browser.tabs.query({currentWindow: true}, function(tabs){

		for (var i = 0, tab; tab = tabs[i]; i++) {
			if (tab.url && tab.url == url) {
				browser.tabs.update(tab.id, {selected: true, url:url});
				return;
			}
		}
		if (readNewTabPref && !getPref(NEW_TAB)) {
			browser.tabs.getSelected(undefined, function(tab){
				browser.tabs.update(tab.id, {selected: true, url:url});
			});
		} else {
			browser.tabs.create({url: url});
		}
	});
	waitAndRefresh();
}

function goToHfr(){
	goToPage(getUsedURL(), true);
}

function openAll() {
	var popupContent = browser.extension.getBackgroundPage().popupContent;
	var entry;
	if (popupContent.entries.length < getPref(MAX_OPEN_ALL) || confirm(browser.i18n.getMessage('too_many_new_tabs', String(popupContent.entries.length)))) {
		for (var i = 0; i < popupContent.entries.length; i++) {
			entry = popupContent.entries[i];
			goToPage(getFullUrl(htmlDecode(entry.href)), false);
		}
	}
	waitAndRefresh();
}

function openCat(cat) {
	if (getPref(OPEN_CAT)) {
		var popupContent = browser.extension.getBackgroundPage().popupContent;
		var entry;
		var entries = new Array();
		for (var i = 0; i < popupContent.entries.length; i++) {
			entry = popupContent.entries[i];
			if (entry.cat == cat) {
				entries.push(getFullUrl(htmlDecode(entry.href)));
			}
		}
		
		if (entries.length < getPref(MAX_OPEN_ALL) || confirm(browser.i18n.getMessage('too_many_new_tabs', String(entries.length)))) {
			for (var i = 0; i < entries.length; i++) {
				goToPage(entries[i], false);
			}
		}
		waitAndRefresh();
	} else {
		var bgPage = browser.extension.getBackgroundPage();
		var site = bgPage.site;
		goToPage(site.getOwnCatUrl(cat), false);
	}
}

function updateBadge(nbUnread) {
	if (nbUnread && nbUnread != null && parseInt(nbUnread) != NaN && parseInt(nbUnread) > 0) {
		browser.browserAction.setBadgeText({text:""+nbUnread});
		if (getPref(ANIMATED_ICON)) animateFlip();
	} else if (nbUnread != null && parseInt(nbUnread) == 0){
		browser.browserAction.setBadgeText({text:""});
	} else if (nbUnread != null && nbUnread.length > 0){
		browser.browserAction.setBadgeText({text:nbUnread});
	} else {
		browser.browserAction.setBadgeText({text:"..."});
	}
}

/*to rotate the browser action icon, (not so) shamelessly copied from google Examples*/
	
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
	var bgPage = browser.extension.getBackgroundPage();
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
		browser.browserAction.setIcon({imageData: imageData});
}
