//preferences keys
var REFRESH_TIME = "refresh_time";
var USE_DIRECT_LINK = "use_direct_link";
var ONLY_FAVS = "only_favs";
var ANIMATED_ICON = "animated_icon";
var USE_CONTEXT_MENU = "use_contest_menu";
var NEW_TAB = "new_tab_menu";
var DEBUG_ON = "debug_on";
var OPEN_CAT = "open_cat";
var MUTED_TOPICS = "muted_topics";

var PREFS_DEFAULT = new Object();
PREFS_DEFAULT[USE_DIRECT_LINK] = false;
PREFS_DEFAULT[REFRESH_TIME] = 500;
PREFS_DEFAULT[ONLY_FAVS] = false;
PREFS_DEFAULT[ANIMATED_ICON] = false;
PREFS_DEFAULT[USE_CONTEXT_MENU] = false;
PREFS_DEFAULT[NEW_TAB] = true;
PREFS_DEFAULT[DEBUG_ON] = false;
PREFS_DEFAULT[OPEN_CAT] = true;
PREFS_DEFAULT[MUTED_TOPICS] = '';

function getPref(name) {
  var value = localStorage[name];
  if (value == null) return PREFS_DEFAULT[name];
  return value;
}

function setPref(name, value) {
  localStorage[name] = value;
}

function _mute(cat, topic, title) {
  if (! isMuted(cat, topic)) {
    setPref(MUTED_TOPICS, getPref(MUTED_TOPICS) + cat + '#' + topic + '#' + title + '|');
  }
}
function _unmute(cat, topic) {
  if (isMuted(cat, topic)) {
    var newMuted = '';
    var muted = null;
    var mutedTopics = getPref(MUTED_TOPICS).split('|');
    for (var i = 0; i < mutedTopics.length; i++) {
      muted = mutedTopics[i];
      if (muted.indexOf('#') != -1) {
        var splitted = muted.split('#');
        if (splitted[0] != cat || splitted[1] != topic)
          newMuted += splitted[0]+"#"+splitted[1]+"#"+splitted[2]+"|";
      }
    }
    setPref(MUTED_TOPICS, newMuted);
  }
}

function isMuted(cat, topic) {
  return getPref(MUTED_TOPICS).indexOf(cat + '#' + topic + '#') != -1;
}