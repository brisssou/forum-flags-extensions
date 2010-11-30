public class PopupContentEntry {
  public var entries = new Array();
  
  function addEntry(title, href) {
    entries.push({title:title, href:href});
  }
  
  function getNext(){
    return entries.shift();
  }
  
  function clear() {
    entries = new Array();
  }
  
}