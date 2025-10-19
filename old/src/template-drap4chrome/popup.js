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
        link_id = link_id_base + 'MP';
        var li = document.createElement('li');
        ul.appendChild(li);
        li.id = "mp";
        var a = document.createElement('a');
        a.setAttribute('href', site.getMpsUrl(catCur));
        a.id = link_id;
        li.appendChild(a);
        a.addEventListener('click', function(url) {
            return function(e) {
                e.preventDefault();
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
                var a = document.createElement('a');
                currentCatLi.appendChild(a);
                if (getPref(SHOW_CAT)) {
                    link_id = link_id_base + 'cat_' + i;
                    a.appendChild(document.createTextNode(bgPage.cats[catCur]));
                    if (openCatPref) {
                        a.addEventListener('click', function(cat) {
                                return function(e) {
                                    e.preventDefault();
                                    openCat(cat);
                                    return false;
                                };
                            }(catCur));
                    } else {
                        a.addEventListener('click', function(href) {
                                return function(e) {
                                    e.preventDefault();
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
                a.setAttribute('class', 'mute');
                a.addEventListener('click', function(some_entry) {
                        return function(e) {
                            e.preventDefault();
                            mute(some_entry.cat, some_entry.post, some_entry.title
                                    .replace(/&#039;/g, "\'"));
                            return false;
                        };
                    }(entry));
                var img = document.createElement('img');
                a.appendChild(img);
                img.setAttribute('src', 'images/mute.gif');
                img.setAttribute('title', chrome.i18n.getMessage("mute"));
            }
            link_id = link_id_base + 'thread_' + i;
            li.appendChild(document.createTextNode(' '));
            var a = document.createElement('a');
            li.appendChild(a);
            a.id = link_id;
            a.setAttribute('href', getFullUrl(he.decode(entry.href)));
            a.addEventListener('click', function(href) {
                    return function(e) {
                        e.preventDefault();
                        goToPage(href, true);
                        return false;
                    }
                }(getFullUrl(he.decode(entry.href))));
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

    document.getElementById('goToSite').setAttribute('title', chrome.extension.getBackgroundPage().site.name);


    document.getElementById('openAll').setAttribute('title', chrome.i18n.getMessage("open_all"));
    document.getElementById('refresh').setAttribute('title', chrome.i18n.getMessage("refresh"));
    document.getElementById('options').setAttribute('title', chrome.i18n.getMessage("options"));

}

document.addEventListener(
    'DOMContentLoaded', 
    function(){
        initPopup();

        document.getElementById('openAll').addEventListener('click', openAll);
        document.getElementById('refresh').addEventListener('click', chrome.extension.getBackgroundPage().startRequest);
        document.getElementById('goToSite').addEventListener('click', goToHfr);
        document.getElementById('options').addEventListener('click', function(){goToPage('options.html', false);});
    },
    false);
