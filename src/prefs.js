//preferences keys
var REFRESH_TIME_PREF = "refresh_time";
var USE_DIRECT_LINK_PREF = "use_direct_link";
var ONLY_FAVS_PREF = "only_favs";
var ANIMATED_ICON_PREF = "animated_icon";
var USE_CONTEXT_MENU_PREF = "use_contest_menu";

var PREFS_DEFAULT = new Object();
PREFS_DEFAULT[USE_DIRECT_LINK_PREF] = false;
PREFS_DEFAULT[REFRESH_TIME_PREF] = 500;
PREFS_DEFAULT[ONLY_FAVS_PREF] = false;
PREFS_DEFAULT[ANIMATED_ICON_PREF] = false;
PREFS_DEFAULT[USE_CONTEXT_MENU_PREF] = false;

function getPref(name) {
  var value = localStorage[name];
  if (value == null) return PREFS_DEFAULT[name];
  return value;
}

function setPref(name, value) {
  localStorage[name] = value;
}
