function PopupContent () {
  this.entries = new Array(),
  
  PopupContent.prototype.add = function(title, href) {
    this.entries.push({title:title, href:href});
  },
  
  PopupContent.prototype.clear = function() {
    this.entries = new Array();
  }
  
}
