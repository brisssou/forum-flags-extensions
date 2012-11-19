
function mute(cat, post, title) {
	_mute(cat, post, title);
	initPopup();
}

function initPopup() {
	var innerHtml = "";
	var entry;
	var catPrev = null;
	var catCur = null;
	var bgPage = chrome.extension.getBackgroundPage();
	var site = bgPage.site;
	var popupContent = bgPage.popupContent;
	var bgColor = getPref(BG_COLOR);
	innerHtml += "<ul style=\"background-color: " + bgColor + "\">";
	var openCatPref = getPref(OPEN_CAT);
	var thingsToOpen = false;
	var link_id_base = 'link_';
	var link_id = '';
	var links_actions = {};
	if (popupContent.mpsNb > 0) {
		thingsToOpen = true;
		catCur = 'prive';
		link_id = link_id_base + 'MP';
		innerHtml += "<li id=\"mp\"><a href=\"#\" id=\"" + link_id + "\">";
		links_actions[link_id] = function(url) {
			return function() {
				goToPage(url, true);
				return false;
			};
		}(site.getMpsUrl(catCur));
		innerHtml += popupContent.mpsNb;
		innerHtml += ' ';
		if (popupContent.mpsNb > 1) {
			innerHtml += chrome.i18n.getMessage("private_messages");
		} else {
			innerHtml += chrome.i18n.getMessage("private_message");
		}
		innerHtml += "</a></li>";
	}
	for ( var i = 0; i < popupContent.entries.length; i++) {
		var entry = popupContent.entries[i];
		if (!isMuted(entry.cat, entry.post)) {
			thingsToOpen = true;
			catCur = entry.cat;
			if (catCur != catPrev) {
				if (catPrev != null) {
					innerHtml += "</li></ul>";
				}
				innerHtml += "<li>";
				if (getPref(SHOW_CAT)) {
					link_id = link_id_base + 'cat_' + i;
					innerHtml += "<a href=\"#\" id=\"" + link_id + "\">";
					innerHtml += bgPage.cats[catCur];
					innerHtml += "</a>";
					if (openCatPref) {
						links_actions[link_id] = function(cat) {
							return function() {
								openCat(cat);
								return false;
							}
						}(catCur);
					} else {
						links_actions[link_id] = function(href) {
							return function() {
								goToPage(href, true);
								return false;
							}
						}(site.getOwnCatUrl(catCur));
					}
				}
				innerHtml += "<ul>";
			}
			innerHtml += "<li>";
			if (entry.cat != "modo") {
				link_id = link_id_base + 'mute_' + i;
				innerHtml += "<a href=\"#\" id=\"" + link_id
						+ "\" class=\"mute\">";
				links_actions[link_id] = function(some_entry) {
					return function() {
						mute(some_entry.cat, some_entry.post, some_entry.title
								.replace(/&#039;/g, "\'"));
						return false;
					};
				}(entry);
				innerHtml += "<img src=\"mute.gif\" title=\""
						+ chrome.i18n.getMessage("mute") + "\"/></a>";
			}
			link_id = link_id_base + 'thread_' + i;
			innerHtml += "&nbsp;<a href=\"#\" id=\"" + link_id + "\"";
			links_actions[link_id] = function(href) {
				return function() {
					goToPage(href, true);
					return false;
				}
			}(getFullUrl(htmlDecode(entry.href)));
			var linkTitle = "";
			if (entry.nbUnread > 0) {
				if (entry.nbUnread > 1) {
					linkTitle += chrome.i18n.getMessage("new_pages",
							String(entry.nbUnread));
				} else {
					linkTitle += chrome.i18n.getMessage("new_page");
				}
			} else {
				linkTitle += chrome.i18n.getMessage("no_new_page");
			}
			innerHtml += " title=\"";
			innerHtml += linkTitle;
			innerHtml += "\">";
			innerHtml += entry.title;
			innerHtml += "</a></li>";
			catPrev = catCur;
		}
	}
	innerHtml += "</ul>";
	document.getElementById('entries').innerHTML = innerHtml;
	document.getElementById('entries').style.maxHeight = screen.availHeight * .45;
	if (thingsToOpen) {
		document.getElementById('openAll').style.display = 'inline';
	} else {
		document.getElementById('openAll').style.display = 'none';
	}
	document.getElementById('goToSite').innerText = chrome.extension
			.getBackgroundPage().site.name;

	document.getElementById('openAll').innerText = chrome.i18n.getMessage("open_all");
	document.getElementById('refresh').innerText = chrome.i18n.getMessage("refresh");
	document.getElementById('options').innerText = chrome.i18n.getMessage("options");

	for ( var link_id in links_actions) {
		$("body").off('click', "#"+link_id,	links_actions[link_id]);
		$("#"+link_id).click(links_actions[link_id]);
	}
}

$(document).ready(function(){
	initPopup();

	$("#openAll").click(openAll);
	$("#refresh").click(chrome.extension.getBackgroundPage().startRequest);
	$("#goToSite").click(goToHfr);
	$("#options").click(function(){goToPage('options.html', false);});
});