//preferences keys
var REFRESH_TIME = "refresh_time";
var USE_DIRECT_LINK = "use_direct_link";
var ONLY_FAVS = "only_favs";
var ANIMATED_ICON = "animated_icon";
var USE_CONTEXT_MENU = "use_contest_menu";
var NEW_TAB = "new_tab_menu";
var DEBUG_ON = "debug_on";

var PREFS_DEFAULT = new Object();
PREFS_DEFAULT[USE_DIRECT_LINK] = false;
PREFS_DEFAULT[REFRESH_TIME] = 500;
PREFS_DEFAULT[ONLY_FAVS] = false;
PREFS_DEFAULT[ANIMATED_ICON] = false;
PREFS_DEFAULT[USE_CONTEXT_MENU] = false;
PREFS_DEFAULT[NEW_TAB] = true;
PREFS_DEFAULT[DEBUG_ON] = false;

function getPref(name) {
  var value = localStorage[name];
  if (value == null) return PREFS_DEFAULT[name];
  return value;
}

function setPref(name, value) {
  localStorage[name] = value;
}
