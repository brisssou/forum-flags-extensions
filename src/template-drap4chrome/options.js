

function setVisible(element, isVisible) {
    if (isVisible) {
        element.style.display = 'block';
    } else {
        element.style.display = 'none';
    }
}
// Restores select box state to saved value from localStorage.
function initControls() {
    var site = chrome.extension.getBackgroundPage().site;
    
    var refTimeLbl = document.getElementById("REFRESH_TIME_label");
    refTimeLbl.innerText = chrome.i18n.getMessage("REFRESH_TIME_label");
    var refTimeInput = document.getElementById("REFRESH_TIME");
    refTimeInput.value = getPref(REFRESH_TIME);
    refTimeInput.onchange = function(event) {
        var refTime = parseInt(this.value);
        var site = chrome.extension.getBackgroundPage().site;
        var error = document.getElementById("REFRESH_TIME.error");
        clear(error);
        if (isNaN(refTime)) {
            error.appendChild(document.createTextNode(chrome.i18n.getMessage("errorOnlyInt")));
        } else if (refTime < site.minRefreshTime) {
            error.appendChild(document.createTextNode(chrome.i18n.getMessage("errorMinRefreshTime", String(site.minRefreshTime))));
        } else {
            setPref(REFRESH_TIME, refTime);
        }
    };
    refTimeInput.onkeyup=refTimeInput.onchange;

    var maxOpenAllLbl = document.getElementById("MAX_OPEN_ALL_label");
    clear(maxOpenAllLbl);
    maxOpenAllLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("MAX_OPEN_ALL_label")));
    maxOpenAllLbl.title = chrome.i18n.getMessage("MAX_OPEN_ALL_label_title");
    var maxOpenAllInput = document.getElementById("MAX_OPEN_ALL");
    maxOpenAllInput.value = getPref(MAX_OPEN_ALL);
    maxOpenAllInput.onchange = function(event) {
        var maxOpenAll = parseInt(this.value);
        var error = document.getElementById("MAX_OPEN_ALL.error");
        clear(error);
        if (isNaN(maxOpenAll)) {
            error.appendChild(document.createTextNode(chrome.i18n.getMessage("errorOnlyInt")));
        } else {
            setPref(MAX_OPEN_ALL, maxOpenAll);
        }
    };
    maxOpenAllInput.onkeyup=maxOpenAllInput.onchange;

    if (site.getSetupUrl) {
    var bgColorLbl = document.getElementById("BG_COLOR_label");
    clear(bgColorLbl);
    bgColorLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("BG_COLOR_label")));
        var bgColorInput = document.getElementById("BG_COLOR");
        bgColorInput.value = getPref(BG_COLOR);
        bgColorInput.onchange = function(event) {
            setPref(BG_COLOR, this.value);
        };
        bgColorInput.onkeyup=bgColorInput.onchange;
    
        var setDefaultColorA = document.getElementById('setDefaultColor');
        clear(setDefaultColorA);
        setDefaultColorA.appendChild(document.createTextNode(chrome.i18n.getMessage("setDefaultColor")));
        setDefaultColorA.onclick = function() {
            bgColorInput.value = PREFS_DEFAULT[BG_COLOR];
            setPref(BG_COLOR, bgColorInput.value);
        };
    } else {
        document.getElementById("BG_COLOR.div").parentNode.removeChild(document.getElementById("BG_COLOR.div"));
    }
    
    var directLinkLbl = document.getElementById("USE_DIRECT_LINK_label");
    clear(directLinkLbl);
    directLinkLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("USE_DIRECT_LINK_label")));
    directLinkLbl.title = chrome.i18n.getMessage("USE_DIRECT_LINK_label_title");
    var directLinkInput = document.getElementById('USE_DIRECT_LINK');
    directLinkInput.checked = getPref(USE_DIRECT_LINK);
    directLinkInput.onchange = function (event) {
        setPref(USE_DIRECT_LINK, this.checked);
        initBehaviour();
    };

    var openCatLbl = document.getElementById("OPEN_CAT_label");
    clear(openCatLbl);
    openCatLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("OPEN_CAT_label")));
    openCatLbl.title = chrome.i18n.getMessage("OPEN_CAT_label_title");
    var openCat = document.getElementById('OPEN_CAT');
    openCat.checked = getPref(OPEN_CAT);
    openCat.onchange = function (event) {
        setPref(OPEN_CAT, this.checked);
        initBehaviour();
    };

    var aniIconLbl = document.getElementById("ANIMATED_ICON_label");
    clear(aniIconLbl);
    aniIconLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("ANIMATED_ICON_label")));
    var aniIconInput = document.getElementById('ANIMATED_ICON');
    aniIconInput.checked = getPref(ANIMATED_ICON);
    aniIconInput.onchange = function (event) {
        setPref(ANIMATED_ICON, this.checked);
    };

    var contextMenuLbl = document.getElementById("USE_CONTEXT_MENU_label");
    clear(contextMenuLbl);
    contextMenuLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("USE_CONTEXT_MENU_label")));
    contextMenuLbl.title = chrome.i18n.getMessage("USE_CONTEXT_MENU_label_title");
    var contextMenuInput = document.getElementById('USE_CONTEXT_MENU');
    contextMenuInput.checked = getPref(USE_CONTEXT_MENU);
    contextMenuInput.onchange = function (event) {
        setPref(USE_CONTEXT_MENU, this.checked);
        initBehaviour();
    };

    var newTabLbl = document.getElementById("NEW_TAB_label");
    clear(newTabLbl);
    newTabLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("NEW_TAB_label")));
    newTabLbl.title = chrome.i18n.getMessage("NEW_TAB_label_title");
    var newTabInput = document.getElementById('NEW_TAB');
    newTabInput.checked = getPref(NEW_TAB);
    newTabInput.onchange = function (event) {
        setPref(NEW_TAB, this.checked);
    };

    var onlyFavsLbl = document.getElementById("ONLY_FAVS_label");
    clear(onlyFavsLbl);
    onlyFavsLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("ONLY_FAVS_label")));
    onlyFavsLbl.title = chrome.i18n.getMessage("ONLY_FAVS_label_title");
    var onlyFavsInput = document.getElementById('ONLY_FAVS');
    onlyFavsInput.checked = getPref(ONLY_FAVS);
    onlyFavsInput.onchange = function (event) {
        setPref(ONLY_FAVS, this.checked);
    };

    var debugOnLbl = document.getElementById("DEBUG_ON_label");
    clear(debugOnLbl);
    debugOnLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("DEBUG_ON_label")));
    debugOnLbl.title = chrome.i18n.getMessage("DEBUG_ON_label_title");
    var debugOnInput = document.getElementById('DEBUG_ON');
    debugOnInput.checked = getPref(DEBUG_ON);
    debugOnInput.onchange = function (event) {
        setPref(DEBUG_ON, this.checked);
    };

    var openToFrontLbl = document.getElementById("OPEN_TO_FRONT_label");
    clear(openToFrontLbl);
    openToFrontLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("OPEN_TO_FRONT_label")));
    //openToFrontLbl.title = chrome.i18n.getMessage("OPEN_TO_FRONT_label_title");
    var openToFrontInput = document.getElementById('OPEN_TO_FRONT');
    openToFrontInput.checked = getPref(OPEN_TO_FRONT);
    openToFrontInput.onchange = function (event) {
        setPref(OPEN_TO_FRONT, this.checked);
    };

    var showCatLbl = document.getElementById("SHOW_CAT_label");
    clear(showCatLbl);
    showCatLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("SHOW_CAT_label")));
    var showCat = document.getElementById('SHOW_CAT');
    showCat.checked = getPref(SHOW_CAT);
    if (showCat.checked) {
        document.getElementById('OPEN_CAT.div').style.visibility = 'visible';
    } else {
        document.getElementById('OPEN_CAT.div').style.visibility = 'hidden';
    }
    showCat.onchange = function (event) {
        setPref(SHOW_CAT, this.checked);
        if (showCat.checked) {
            setVisible(document.getElementById('OPEN_CAT.div'), true);
        } else {
            setVisible(document.getElementById('OPEN_CAT.div'), false);
        }
    };

    var getTopicsLbl = document.getElementById("GET_TOPICS_label");
    clear(getTopicsLbl);
    getTopicsLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("GET_TOPICS_label")));
    var getTopics = document.getElementById('GET_TOPICS');
    getTopics.checked = getPref(GET_TOPICS);
    if (getTopics.checked) {
        setVisible(document.getElementById('SHOW_CAT.div'), true);
    } else {
        setVisible(document.getElementById('SHOW_CAT.div'), false);
    }
    getTopics.onchange = function (event) {
        setPref(GET_TOPICS, this.checked);
        if (getTopics.checked) {
            setVisible(document.getElementById('SHOW_CAT.div'), true);
        } else {
            setVisible(document.getElementById('SHOW_CAT.div'), false);
        }
    };

    var getModoLbl = document.getElementById("MODO_label");
    clear(getModoLbl);
    getModoLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("MODO_label")));
    var getModo = document.getElementById('MODO');
    getModo.checked = getPref(MODO);
    getModo.onchange = function (event) {
        setPref(MODO, this.checked);
    };
    setVisible(document.getElementById("MODO_div"), false);


    var getAvatarLbl = document.getElementById("AVATAR_label");
    clear(getAvatarLbl);
    getAvatarLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("AVATAR_label")));
    var getAvatar = document.getElementById('AVATAR');
    getAvatar.checked = getPref(AVATAR);
    getAvatar.onchange = function (event) {
        setPref(AVATAR, this.checked);
    };
    setVisible(document.getElementById("AVATAR_div"), false);

    var getMpsLbl = document.getElementById("GET_MPS_label");
    clear(getMpsLbl);
    getMpsLbl.appendChild(document.createTextNode(chrome.i18n.getMessage("GET_MPS_label")));
    var getMps = document.getElementById('GET_MPS');
    getMps.checked = getPref(GET_MPS);
    getMps.onchange = function (event) {
        setPref(GET_MPS, this.checked);
    };
    
    document.title = chrome.i18n.getMessage("optionsPageTitle", site.name);
    
    clear(document.getElementById('muted_label'));
    document.getElementById('muted_label').appendChild(document.createTextNode(chrome.i18n.getMessage("muted_label", site.name)));
    initMuted();
}

function initMuted() {
    var mutedTopicsValue = getPref(MUTED_TOPICS);
    setVisible(document.getElementById('muted_div'), mutedTopicsValue && mutedTopicsValue.length > 0);
    if (mutedTopicsValue) {
        var mutedTopics = mutedTopicsValue.split('|');
        
        clear(document.getElementById('muted_topics'));
        var mutedTopicsElement = document.createElement('ul');
        mutedTopicsElement.id = 'muted';
        document.getElementById('muted_topics').appendChild(mutedTopicsElement);

        var topic = null;
        

        var btn_id_base = 'btn_';
        var btn_id = '';
        var btns_actions = {};
        for (var i = 0; i < mutedTopics.length; i++) {
            topic = mutedTopics[i];
            if (topic.indexOf('#') != -1) {
                btn_id = btn_id_base + i;
                var li = document.createElement('li');
                mutedTopicsElement.appendChild(li);
                var splitted = topic.split('#');
                var cat = splitted.shift();
                var postId = splitted.shift();
                var title = he.decode(splitted.join('#'));
                li.appendChild(document.createTextNode(title + ' '));
                var button = document.createElement('button');
                li.appendChild(button);
                button.id=btn_id
                button.appendChild(document.createTextNode(chrome.i18n.getMessage("options_unmute")));
                btns_actions[btn_id] = function(one, two){return function(){unmute(one, two);};}(cat, postId);
            }
        }
        for (var btn_id in btns_actions) {
            document.getElementById(btn_id).removeEventListener('click', btns_actions[btn_id]);
            document.getElementById(btn_id).addEventListener('click', btns_actions[btn_id]);
        }
    }
}

function unmute(cat, topic) {
    _unmute(cat, topic);
    initMuted();
}

konami = new Konami();
konami.code = function() {
    if (chrome.extension.getBackgroundPage().site.avatarsUrl && chrome.extension.getBackgroundPage().site.alertsUrl) {
        setVisible(document.getElementById("MODO_div"), true);
        setVisible(document.getElementById("AVATAR_div"), true);
    }
};

konami.load();


window.addEventListener("load", initControls);

window.addEventListener("unload", waitAndRefresh);


