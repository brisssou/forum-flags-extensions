function Site (name, hostAndBase, config, defaultColor, minRefreshTime, fragment) {
  this.name = name;
  this.hostAndBase = hostAndBase;
  this.config = config;
  this.defaultColor = defaultColor;
  this.minRefreshTime = minRefreshTime;
  this.fragment = fragment;
  
  Site.prototype.getBaseUrl = function() {
    return "/forum1f.php?config="+this.config;
  }
  Site.prototype.getOwnUrl = function(own) {
    return this.getBaseUrl()+"&owntopic="+own;
  }
  Site.prototype.getOwnCatUrl = function(cat) {
    return this.getFullUrl("/forum1.php?config="+this.config+"&owntopic=1&cat="+cat);
  }
  Site.prototype.getDrapsUrl = function() {
    return this.getFullUrl(this.getOwnUrl(1));
  }
  Site.prototype.getFavsUrl = function() {
    return this.getFullUrl(this.getOwnUrl(3));
  }
  Site.prototype.getMpsUrl = function() {
    return this.getFullUrl(this.getOwnCatUrl('prive'));
  }
  /*To Be implemented*/
  Site.prototype.getSetupUrl = function() {
    return null;
  }
  Site.prototype.parseUnread = function(content, muted, popupContent) {
    return null;
  }
  Site.prototype.parseMps = function(content) {
    return null;
  }
  Site.prototype.parseCats = function(content) {
    return null;
  }
  Site.prototype.getFullUrl = function(uri) {
    return "http://"+this.hostAndBase+uri+this.fragment;
  }
}

function Hfr() {
  this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
  this.entryUrlRex = /cat=(\d+)&amp;(subcat=(\d+)&amp;)?post=(\d+)&amp;page=(\d+)/;
  this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
  this.mpRex = /class="red">Vous avez (\d*) nouveau/;
  this.bgColorRex = /<input name="inputcouleurTabHeader" .* value="(.*)"/;
  this.catsMasterRex = /<select name="cat"(.+)<\/select>/;
  this.catsRex = /<option value="([^"]+)" >([^<]+)/g;
}
Hfr.prototype = new Site('HFR', "forum.hardware.fr", "hfr.inc", '#2F3740', 120, '&flags4chrome=1');
Hfr.prototype.getSetupUrl = function() {
  return "http://"+this.hostAndBase+"/setperso.php?config="+this.config;
}
Hfr.prototype.parseUnread = function(content, muted) {
  var matches = null;
  var unreads = Array();
  while (matches = this.unreadRex.exec(content)) {
    debug("found one");
    var url = matches[2];
    var urlMatch = this.entryUrlRex.exec(url);
    if (urlMatch != null && !isMuted(urlMatch[1], urlMatch[4])) {
      var topicNbPages = 1;
      var nbPages = null;
      if (nbPages = this.nbPagesRex.exec(matches[0])) {
        topicNbPages = parseInt(nbPages[1]);
      }
      unreads.push({title:matches[1], cat:urlMatch[1], post:urlMatch[4], href:url, nbUnread:topicNbPages - parseInt(matches[3])});
    } else {
      debug("... but a muted one");
    }
  }
  return unreads;
}
Hfr.prototype.parseMps = function(content) {
  var mps = this.mpRex.exec(content);
  var mpsNb = 0;
  if (mps != null) {
    mpsNb = parseInt(mps[1]);
    if (mpsNb == NaN) mpsNb = 0;
    debug("found "+mpsNb+" private messages");
  }
  return mpsNb;
}

Hfr.prototype.parseCats = function(content) {
  cats = new Array();
  //parseCats(content) #return cats Array
  matches = this.catsMasterRex.exec(content);
  if (matches!=null) {
    var catsString = matches[0];
    while (matches = this.catsRex.exec(catsString)) {
      cats[matches[1]] = matches[2];
      debug("new cat :"+matches[1]+"/"+matches[2]);
    }
  }
  return cats;
}

Hfr.prototype.parseBgColor = function(content) {
  return this.bgColorRex.exec(content);
}
  /*
  var HFR = "http://www.infos-du-net.com";
var HFR_MY_DRAPS = HFR + "/forum/forum1f.php?config=infosdunet.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR +  "/forum/forum1f.php?config=infosdunet.inc&owntopic=3&new=0&nojs=0";
var HFR_MP = HFR + "/forum/forum1.php?config=infosdunet.inc&cat=prive";
var HFR_SETUP_THEME = HFR + "/forum/setperso.php?config=infosdunet.inc";
var DIRECT_CAT_LINK = HFR + "/forum/forum1.php?config=infosdunet.inc&owntopic=1&cat=";

//</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=165&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">165</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=158&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t627721"><img src="http://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.158)
var UNREAD_REX_PACK = /title="Sujet n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
var UNREAD_REX = /title="Sujet n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
var NB_PAGES_REX = /cCatTopic">(\d+)<\/a>/;
var MP_REX = />Messages priv.s \((\d+)\)<\/a>/;
var BG_COLOR_REX = /<input name="inputcouleurTabHeader" [\s\S]* value="([\s\S]*)"/

var CATS_MASTER_REX = /<select.*name="cat"([\s\S]+)<\/select>/;
var CATS_REX = /<option value="([^"]+)".*>([^"]+)<\/option>/g;

var ENTRY_URL_REX = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
  */
