function Site (name, hostAndBase, config, defaultColor, minRefreshTime, fragment) {
	this.name = name;
	this.hostAndBase = hostAndBase;
	this.config = config;
	this.defaultColor = defaultColor;
	this.minRefreshTime = minRefreshTime;
	this.fragment = fragment;
	this.parsableMpsUrl = undefined;
	
	
	Site.prototype.applyXtor = function(url) {
		debug("applying xtor to: "+url);
		var uriParts = url.split('?');
		var uri = uriParts[0];
		var request = this.fragment;
		debug("fragment: "+request);
		if (uriParts.length > 1) {
			debug("request: "+uriParts[1]);
			request += "&"+uriParts[1];
		}
		debug("full uri: "+uri + '?' + request);
		return uri + '?' + request;
	};
	Site.prototype.getBaseUrl = function() {
		return "/forum1f.php?config="+this.config;
	};
	Site.prototype.getOwnUrl = function(own) {
		return this.getBaseUrl()+"&owntopic="+own;
	};
	Site.prototype.getOwnCatUrl = function(cat) {
		return this.getFullUrl("/forum1.php?config="+this.config+"&owntopic=1&cat="+cat);
	};
	Site.prototype.getDrapsUrl = function() {
		return this.getFullUrl(this.getOwnUrl(1));
	};
	Site.prototype.getFavsUrl = function() {
		return this.getFullUrl(this.getOwnUrl(3));
	};
	Site.prototype.getMpsUrl = function() {
		return this.getFullUrl("/forum1.php?config="+this.config+"&cat=prive");
	};
	/*To Be implemented*/
	Site.prototype.getSetupUrl = function() {
		return null;
	};
	Site.prototype.parseUnread = function(content, muted, popupContent) {
		return null;
	};
	Site.prototype.parseMps = function(content) {
		return null;
	};
	Site.prototype.parseCats = function(content) {
		return null;
	};
	Site.prototype.getFullUrl = function(uri) {
		return this.applyXtor("http://"+this.hostAndBase+uri);
	};
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
Hfr.prototype = new Site('HFR', "forum.hardware.fr", "hfr.inc", '#2F3740', 120, 'flags4chrome=1');
Hfr.prototype.getSetupUrl = function() {
	return this.applyXtor("http://"+this.hostAndBase+"/setperso.php?config="+this.config);
};
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
};
Hfr.prototype.parseMps = function(content) {
	var mps = this.mpRex.exec(content);
	var mpsNb = 0;
	if (mps != null) {
		mpsNb = parseInt(mps[1]);
		if (mpsNb == NaN) mpsNb = 0;
		debug("found "+mpsNb+" private messages");
	}
	return mpsNb;
};

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
};

Hfr.prototype.parseBgColor = function(content) {
	return this.bgColorRex.exec(content);
};

function MesDisc() {
	this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&amp;(subcat=(\d+)&amp;)?post=(\d+)&amp;page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /class="red">Vous avez (\d*) nouveau/;
	this.bgColorRex = /<input name="inputcouleurTabHeader" .* value="(.*)"/;
	this.catsMasterRex = /<select name="cat"(.+)<\/select>/;
	this.catsRex = /<option value="([^"]+)" >([^<]+)/g;
}
MesDisc.prototype = new Site('Mes Discussions', "www.mesdiscussions.net", "md.inc", '#AF261E', 120, 'flags4chrome=1');
MesDisc.prototype.parseUnread = Hfr.prototype.parseUnread;
MesDisc.prototype.parseCats = Hfr.prototype.parseCats;
MesDisc.prototype.parseMps = Hfr.prototype.parseMps;
MesDisc.prototype.getBaseUrl = function() {
	return "/forum/forum1f.php?config="+this.config;
};
MesDisc.prototype.getOwnCatUrl = Hfr.prototype.getOwnCatUrl;

function LesNum() {
	this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5"><a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&amp;(subcat=(\d+)&amp;)?post=(\d+)&amp;page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /class="red">Vous avez (\d*) nouveau/;
	this.bgColorRex = /<input name="inputcouleurTabHeader" .* value="(.*)"/;
	this.catsMasterRex = /<select name="cat"(.+)<\/select>/;
	this.catsRex = /<option value="([^"]+)" >([^<]+)/g;
}
LesNum.prototype = new Site('Les numériques', "www.lesnumeriques.com", "avis.inc", '#AF261E', 120, 'flags4chrome=1');
LesNum.prototype.parseUnread = Hfr.prototype.parseUnread;
LesNum.prototype.parseCats = Hfr.prototype.parseCats;
LesNum.prototype.parseMps = Hfr.prototype.parseMps;
LesNum.prototype.getBaseUrl  = function() {
	return "/legrandforum/forum1f.php?config="+this.config;
};
LesNum.prototype.getOwnCatUrl = Hfr.prototype.getOwnCatUrl;

