class PopupContent () {
  entries: new Array(),
  
  add: function(title, href) {
    this.entries.push({title:title, href:href});
  },
  
  clear: function() {
    this.entries = new Array();
  }
  
}
