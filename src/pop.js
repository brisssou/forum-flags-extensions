function PopupContent () {
  this.entries = new Array(),
  this.mpsNb = 0,
  
  PopupContent.prototype.add = function(title, cat, post, href, nbUnread) {
    this.entries.push({title:title, cat:cat, post:post, href:href, nbUnread:nbUnread});
  },
  
  PopupContent.prototype.setMps = function(mpsNb) {
    this.mpsNb = mpsNb;
  },
  
  PopupContent.prototype.clear = function() {
    this.entries = new Array();
  }
  
}
