
function mute(cat, post, title) {
	_mute(cat, post, title);
	initPopup();
}

function initPopup() {
	var entry;
	var catPrev = null;
	var catCur = null;
	var bgPage = chrome.extension.getBackgroundPage();
	var site = bgPage.site;
	var popupContent = bgPage.popupContent;
	var bgColor = getPref(BG_COLOR);
	var entries = document.getElementById('entries');
	clear(entries);
	var ul = document.createElement('ul');
	entries.appendChild(ul);
	ul.style = "background-color: " + bgColor;
	var openCatPref = getPref(OPEN_CAT);
	var thingsToOpen = false;
	var link_id_base = 'link_';
	var link_id = '';
	var links_actions = {};
	if (popupContent.mpsNb > 0) {
		thingsToOpen = true;
		catCur = 'prive';
		link_id = link_id_base + 'MP';
		var li = document.createElement('li');
		li.id = "mp";
		var a = document.createElement('a');
		a.setAttribute('href', '#');
		a.id = link_id;
		li.appendChild(a);
		a.addEventListener('click', function(url) {
			return function() {
				goToPage(url, true);
				return false;
			};
		}(site.getMpsUrl(catCur)));
		var private_msg = popupContent.mpsNb;
		private_msg += ' ';
		if (popupContent.mpsNb > 1) {
			private_msg += chrome.i18n.getMessage("private_messages");
		} else {
			private_msg += chrome.i18n.getMessage("private_message");
		}

		a.appendChild(document.createTextNode(private_msg));
	}
	var currentCatLi = null;
	for ( var i = 0; i < popupContent.entries.length; i++) {
		entry = popupContent.entries[i];
		if (!isMuted(entry.cat, entry.post)) {
			thingsToOpen = true;
			catCur = entry.cat;
			if (catCur != catPrev) {
				currentCatLi = document.createElement('li');
				ul.appendChild(currentCatLi);
				if (getPref(SHOW_CAT)) {
					link_id = link_id_base + 'cat_' + i;
					var a = document.createElement('a');
					a.id = link_id;
					a.setAttribute('href', '#');
					currentCatLi.appendChild(a);
					a.appendChild(document.createTextNode(bgPage.cats[catCur]));
					if (openCatPref) {
						a.addEventListener('click', function(cat) {
								return function() {
									openCat(cat);
									return false;
								};
							}(catCur));
					} else {
						a.addEventListener('click', function(href) {
								return function() {
									goToPage(href, true);
									return false;
								};
							}(site.getOwnCatUrl(catCur)));
					}
				}
			}
			var innerUl = document.createElement('ul');
			currentCatLi.appendChild(innerUl);
			var li = document.createElement('li');
			innerUl.appendChild(li);
			if (entry.cat != "modo") {
				link_id = link_id_base + 'mute_' + i;
				var a = document.createElement('a');
				li.appendChild(a);
				a.id = link_id;
				a.setAttribute('href', '#');
				a.setAttribute('class', 'mute');
				a.addEventListener('click', function(some_entry) {
						return function() {
							mute(some_entry.cat, some_entry.post, some_entry.title
									.replace(/&#039;/g, "\'"));
							return false;
						};
					}(entry));
				var img = document.createElement('img');
				a.appendChild(img);
				img.setAttribute('src', 'mute.gif');
				img.setAttribute('title', chrome.i18n.getMessage("mute"));
			}
			link_id = link_id_base + 'thread_' + i;
			li.appendChild(document.createTextNode(' '));
			var a = document.createElement('a');
			li.appendChild(a);
			a.id = link_id;
			a.addEventListener('click', function(href) {
					return function() {
						goToPage(href, true);
						return false;
					}
				}(getFullUrl(htmlDecode(entry.href))));
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
			a.setAttribute('title', linkTitle);
			a.appendChild(document.createTextNode(he.decode(entry.title)));
			catPrev = catCur;
		}
	}
	document.getElementById('entries').style.maxHeight = screen.availHeight * .45;
	if (thingsToOpen) {
		document.getElementById('openAll').style.display = 'inline';
	} else {
		document.getElementById('openAll').style.display = 'none';
	}
	while (document.getElementById('goToSite').firstChild) {
	    document.getElementById('goToSite').removeChild(document.getElementById('goToSite').firstChild);
	}
	document.getElementById('goToSite').appendChild(document.createTextNode(chrome.extension
			.getBackgroundPage().site.name));


	clear(document.getElementById('openAll'));
	clear(document.getElementById('refresh'));
	clear(document.getElementById('options'));
	document.getElementById('openAll').appendChild(document.createTextNode(chrome.i18n.getMessage("open_all")));
	document.getElementById('refresh').appendChild(document.createTextNode(chrome.i18n.getMessage("refresh")));
	document.getElementById('options').appendChild(document.createTextNode(chrome.i18n.getMessage("options")));

	/*for ( var link_id in links_actions) {
		$("body").off('click', "#"+link_id,	links_actions[link_id]);
		$("#"+link_id).click(links_actions[link_id]);
	}*/
}

$(document).ready(function(){
	initPopup();

	$("#openAll").click(openAll);
	$("#refresh").click(chrome.extension.getBackgroundPage().startRequest);
	$("#goToSite").click(goToHfr);
	$("#options").click(function(){goToPage('options.html', false);});
});