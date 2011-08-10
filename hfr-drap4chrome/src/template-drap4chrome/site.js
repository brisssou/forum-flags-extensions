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
		return this.applyXtor("/forum1f.php?config="+this.config);
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
		return this.getFullUrl(this.getOwnCatUrl('prive'));
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


function Tgufr() {
	this.unreadRexPack = /title="Sujet n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Aller au dernier message lu sur ce sujet \(p.(\d+)\)[\s\S]*sujetCase9[\s\S]*<\/a>/g;
	this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\).*sujetCase9.*<\/a>/g;
	this.entryUrlRex = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
	this.nbPagesRex = /td class="sujetCase9">.*page=(\d+)&/;
	this.mpRex = />Messages priv.s \((\d+)\)<\/a>/;
	this.catsMasterRex = /<select.*name="cat"([\s\S]+)<\/select>/;
	this.catsRex = /<option value="([^"]+)".*>([^"]+)<\/option>/g;
}

Tgufr.prototype = new Site('Tom\'s Guide France', "www.infos-du-net.com", "infosdunet.inc", '#1C92D2', 120, 'xtor=RSS-9990');

Tgufr.prototype.parseUnread = function(content, muted) {
	var matches = null;
	var unreads = Array();
	while (matches_pack = this.unreadRexPack.exec(content)) {
		var pack = matches_pack[0].replace(/[\n\r\t]/g,' ').replace(/<\/tr>/g,'\n</tr>');
				
		while (matches = this.unreadRex.exec(pack)) {
			debug("found one");
			var url = matches[2];
			var urlMatch = this.entryUrlRex.exec(url);
			if (urlMatch != null && !isMuted(urlMatch[1], urlMatch[4])) {
				var topicNbPages = 1;
				var nbPages = null;
				var matched = matches[0];
				if (nbPages = this.nbPagesRex.exec(matched)) {
					topicNbPages = parseInt(nbPages[1]);
				}
				unreads.push({title:matches[1], cat:urlMatch[1], post:urlMatch[4], href:url, nbUnread:topicNbPages - parseInt(matches[3])});
			} else {
				debug("... but a muted one");
			}
		}
	}
	return unreads;
};
Tgufr.prototype.parseCats = Hfr.prototype.parseCats;
Tgufr.prototype.parseMps = Hfr.prototype.parseMps;
Tgufr.prototype.getBaseUrl = function() {
	return "/forum/forum1f.php?config="+this.config;
};
Tgufr.prototype.getOwnCatUrl = function(cat) {
	return this.getFullUrl("/forum/forum1.php?config="+this.config+"&owntopic=1&cat="+cat);
};




function Thfr() {
	this.unreadRexPack = /title="Sujet n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /(\d+) message(?:s)?/;
	this.catsMasterRex = /<select.*name="cat"([\s\S]+)<\/select>/;
	this.catsRex = /<option value="([^"]+)".*>([^"]+)<\/option>/g;
	this.parsableMpsUrl = '/membres/?ctrlr=Info&action=getLoginInfos';
}
Thfr.prototype = new Site('Tom\'s Hardware France', "www.presence-pc.com", "presencepc.inc", '#AF261E', 120, 'xtor=RSS-9990');
Thfr.prototype.parseUnread = Tgufr.prototype.parseUnread;
Thfr.prototype.parseCats = Tgufr.prototype.parseCats;
Thfr.prototype.parseMps = Tgufr.prototype.parseMps;
Thfr.prototype.getBaseUrl = Tgufr.prototype.getBaseUrl;
Thfr.prototype.getOwnCatUrl = Tgufr.prototype.getOwnCatUrl;



function ThfrInterne() {
	this.unreadRexPack = /title="Sujet n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.unreadRex = /title="Sujet n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Aller au dernier message lu sur ce sujet \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = />Messages priv.s \((\d+)\)<\/a>/;
	this.catsMasterRex = /<select.*name="cat"([\s\S]+)<\/select>/;
	this.catsRex = /<option value="([^"]+)".*>([^"]+)<\/option>/g;
	this.parsableMpsUrl = '/membres/?ctrlr=Info&action=getLoginInfos';
}
ThfrInterne.prototype = new Site('Tom\'s Hardware France Interne', "www.presence-pc.com", "interne.inc", '#AF261E', 120, 'xtor=RSS-9990');
ThfrInterne.prototype.parseUnread = Thfr.prototype.parseUnread;
ThfrInterne.prototype.parseCats = Thfr.prototype.parseCats;
ThfrInterne.prototype.parseMps = Thfr.prototype.parseMps;
ThfrInterne.prototype.getBaseUrl = Tgufr.prototype.getBaseUrl;
ThfrInterne.prototype.getOwnCatUrl = Tgufr.prototype.getOwnCatUrl;



function Thuk() {
	this.unreadRexPack = /title="Thread n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Go to the last post read in this thread \(p.(\d+)\)/g;
	this.unreadRex = /title="Thread n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Go to the last post read in this thread \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /(\d+) message(?:s)?/;
	this.catsMasterRex = /<select.*name="cat"([\s\S]+)<\/select>/;
	this.catsRex = /<option value="([^"]+)".*>([^"]+)<\/option>/g;
	this.parsableMpsUrl = '/membres/?ctrlr=Info&action=getLoginInfos';
}
Thuk.prototype = new Site('Tom\'s Hardware UK', "www.tomshardware.co.uk", "tomshardwareuk.inc", '#AF261E', 120, '&xtor=RSS-9990');
Thuk.prototype.parseUnread = Tgufr.prototype.parseUnread;
Thuk.prototype.parseCats = Tgufr.prototype.parseCats;
Thuk.prototype.parseMps = Tgufr.prototype.parseMps;
Thuk.prototype.getBaseUrl = Tgufr.prototype.getBaseUrl;
Thuk.prototype.getOwnCatUrl = Tgufr.prototype.getOwnCatUrl;



function Thus() {
	this.unreadRexPack = /title="Thread n.\d+">([^<]+)[\s\S]+sujetCase5">[\s\S]+<a href="([^"]+)[\s\S]+Go to the last post read in this thread \(p.(\d+)\)/g;
	this.unreadRex = /title="Thread n.\d+">([^<]+).+sujetCase5">.+<a href="([^"]+).+Go to the last post read in this thread \(p.(\d+)\)/g;
	this.entryUrlRex = /cat=(\d+)&(subcat=(\d+)&)?post=(\d+)&page=(\d+)/;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /(\d+) message(?:s)?/;
	this.catsMasterRex = /<select.*name="cat"([\s\S]+)<\/select>/;
	this.catsRex = /<option value="([^"]+)".*>([^"]+)<\/option>/g;
	this.parsableMpsUrl = '/membres/?ctrlr=Info&action=getLoginInfos';
}
Thus.prototype = new Site('Tom\'s Hardware', "www.tomshardware.com", "tomshardwareus.inc", '#AF261E', 120, 'xtor=RSS-9990');
Thus.prototype.parseUnread = Tgufr.prototype.parseUnread;
Thus.prototype.parseCats = Tgufr.prototype.parseCats;
Thus.prototype.parseMps = Tgufr.prototype.parseMps;
Thus.prototype.getBaseUrl = Tgufr.prototype.getBaseUrl;
Thus.prototype.getOwnCatUrl = Tgufr.prototype.getOwnCatUrl;



function Thde() {
	this.unreadRexPack = /<table class="topicListing tableListing">[\s\S]+<div class="mod2 line">/g;
	this.unreadRex = /<span class="bgc topicPicto to(?:Read|Participate)">.*<a class="topicTitle" href="([^"]+)">([^<]+)<\/a>.*<a href="#" onmouseover="BOM.Utils.decodeLive\('([^']+)', this\);" class="crLink">/g;
	this.entryUrlRex = /\/id-(\d+)\//;
	this.nbPagesRex = /cCatTopic">(\d+)<\/a>/;
	this.mpRex = /Private Nachrichten<\/a>[^<]+<span class="notif or">(\d+)<\/span>/;
	this.catsMasterRex = /<span>Andere Kategorien([\s\S]+)Zone 15/;
	this.catsRex = /<a[^']*'(.*)'.*>(.*)<\/a>/g;
	this.parsableMpsUrl = 'http://www.tomshardware.de/community/updates';
}
Thde.prototype = new Site('Tom\'s Hardware Deutschland', "www.tomshardware.de", "", '#AF261E', 120, 'xtor=RSS-9990');
Thde.prototype.getFullUrl = function(url) {
	return this.applyXtor(url);
};
Thde.prototype.getDrapsUrl = function() {
	return this.applyXtor('http://www.tomshardware.de/foren/contributed.html');
};
Thde.prototype.getFavsUrl = function() {
	return this.applyXtor('http://www.tomshardware.de/foren/read.html');
};
Thde.prototype.parseUnread = function(content, muted) {
	var matches = null;
	var unreads = Array();
	while (matches_pack = this.unreadRexPack.exec(content)) {
		var pack = matches_pack[0].replace(/[\n\r\t]/g,' ').replace(/<\/tr>/g,'\n</tr>');
				
		while (matches = this.unreadRex.exec(pack)) {
			debug("found one");
			/* url = 1
				 title = 2
				 cat_url_hash = 3
			*/
			var catUrl = BOM.Utils.decode(matches[3]);
			var cat = -1;
			var catMatch = null;
			if (catMatch = /\/foren-(\d+)\.html/.exec(catUrl)) {
				cat = catMatch[1];
			}
			var url = matches[1];
			var urlMatch = this.entryUrlRex.exec(url);
			if (urlMatch != null && !isMuted(cat, urlMatch[1])) {
				/*var topicNbPages = 1;
				var nbPages = null;
				var matched = matches[0];
				if (nbPages = this.nbPagesRex.exec(matched)) {
					topicNbPages = parseInt(nbPages[1]);
				}*/
				unreads.push({title:matches[2], cat:cat, post:0, href:url, nbUnread:0});
			} else {
				debug("... but a muted one");
			}
		}
	}
	return unreads;
};
Thde.prototype.parseCats = function(content) {
	var cats = new Array();
	//parseCats(content) #return cats Array
	var matches = this.catsMasterRex.exec(content);
	if (matches!=null) {
		var catsString = matches[0];
		while (matches = this.catsRex.exec(catsString)) {
			var catHash = matches[1];
			var catLbl = matches[2];
			var catUrl = BOM.Utils.decode(catHash);
			var catId = /foren-(\d+)\.html/.exec(catUrl)[1];
			cats[catId] = catLbl;
			debug("new cat :"+catId+"/"+catLbl);
		}
	}
	return cats;
};
Thde.prototype.parseMps = Tgufr.prototype.parseMps;
