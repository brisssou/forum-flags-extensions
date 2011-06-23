function Site () {
  this.name = '',
  /*
var HFR_MY_DRAPS = HFR + "/forum1f.php?config=hfr.inc&owntopic=1&new=0&nojs=0";
var HFR_MY_FAVS = HFR + "/forum1f.php?config=hfr.inc&owntopic=3&new=0&nojs=0";
var HFR_MP = HFR + "/forum1f.php?config=hfr.inc&cat=prive";
var HFR_SETUP_THEME = HFR + "/setperso.php?config=hfr.inc";
//</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=165&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">165</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=23&amp;subcat=529&amp;post=21184&amp;page=158&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t627721"><img src="http://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.158)
var UNREAD_REX = /title="Sujet n.\d+">([^<]+).+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
var NB_PAGES_REX = /cCatTopic">(\d+)<\/a>/;
var MP_REX = /class="red">Vous avez (\d*) nouveau/;
var BG_COLOR_REX = /<input name="inputcouleurTabHeader" .* value="(.*)"/

var CATS_MASTER_REX = /<select name="cat"(.+)<\/select>/;
var CATS_REX = /<option value="([^"]+)" >([^<]+)/g;

var ENTRY_URL_REX = /cat=(\d+)&amp;(subcat=(\d+)&amp;)?post=(\d+)&amp;page=(\d+)/;
  */
  this.mpsNb = 0
  
  PopupContent.prototype.add = function(title, cat, post, href, nbUnread) {
    this.entries.push({title:title, cat:cat, post:post, href:href, nbUnread:nbUnread});
  },
  
  PopupContent.prototype.setMps = function(mpsNb) {
    this.mpsNb = mpsNb;
  },
  
  PopupContent.prototype.clear = function() {
    this.entries = new Array();
    this.mpsNb = 0;
  }
  
}
