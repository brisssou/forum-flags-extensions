var cats = new Object();
cats[1] = 'Hardware';
cats[16] = 'Hardware - P&eacute;riph&eacute;riques';
cats[23] = 'Technologies Mobiles';
cats[3] = 'Video &amp; Son';
cats[5] = 'Jeux Video';
cats[4] = 'Windows &amp; Software';
cats[22] = 'R&eacute;seaux grand public / SoHo';
cats[11] = 'OS Alternatifs';
cats[10] = 'Programmation';
cats[6] = 'Achats &amp; Ventes';
cats[9] = 'Seti et projets distribu&eacute;s';
cats[13] = 'Discussions';

function PopupContent () {
  this.entries = new Array(),
  
  PopupContent.prototype.add = function(title, href) {
    var cat = href.substring(href.indexOf('&amp;cat=')+'&amp;cat='.length,href.indexOf('&amp;subcat='));
    this.entries.push({title:title, cat:cat, href:href});
  },
  
  PopupContent.prototype.clear = function() {
    this.entries = new Array();
  }
  
}
