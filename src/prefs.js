
//preferences keys
var REFRESH_TIME_PREF = "refresh_time";
var USE_DIRECT_LINK_PREF = "use_direct_link";

var PREFS_DEFAULT = new Object();
PREFS_DEFAULT[USE_DIRECT_LINK_PREF] = true;
PREFS_DEFAULT[REFRESH_TIME_PREF] = 5;

function getPref(name) {
  var value = localStorage[name];
  if (value == null) return PREFS_DEFAULT[name];
  return value;
}

function setPref(name, value) {
  localStorage[name] = value;
}
