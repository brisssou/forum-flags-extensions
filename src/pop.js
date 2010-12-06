var cats = new Object();
cats[1] = 'Hardware';
cats[16] = 'Hardware - P&eacute;riph&eacute;riques';
cats[15] = 'Ordinateurs portables';
cats[23] = 'Technologies Mobiles';
cats[2] = 'Overclocking, Cooling &amp; Tuning';
cats[25] = 'Apple';
cats[3] = 'Vid&eacute;o &amp; Son';
cats[14] = 'Photo num&eacute;rique';
cats[5] = 'Jeux Vid&eacute;o';
cats[4] = 'Windows &amp; Software';
cats[22] = 'R&eacute;seaux grand public / SoHo';
cats[21] = 'Syst&egrave;mes &amp; R&eacute;seaux Pro';
cats[11] = 'OS Alternatifs';
cats[10] = 'Programmation';
cats[12] = 'Graphisme';
cats[6] = 'Achats &amp; Ventes';
cats[8] = 'Emploi &amp; &Eacute;tudes';
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
