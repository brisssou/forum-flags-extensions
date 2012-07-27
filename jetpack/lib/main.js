// Import the APIs we need.
var contextMenu = require("context-menu");
var request = require("request");
var selection = require("selection");
var panel = require("panel");
var self = require("self");
const data = self.data;
 
exports.main = function(options, callbacks) {
  console.log(options.loadReason);
 
 /*  var popUp = panel.Panel({
    contentURL: data.url("pop.html")
  });
   
  popUp.show(); */
  
  const widgets = require("widget");
 
  // A basic click-able image widget.
  widgets.Widget({
    id: "google-link",
    label: "Widget with an image and a click handler",
    contentURL: "http://www.google.com/favicon.ico",
    onClick: function() {
      require("tabs").activeTab.url = "http://www.google.com/";
    }
  });
   
  // A widget that loads a random Flickr photo every 5 minutes.
  widgets.Widget({
    id: "random-flickr",
    label: "Random Flickr Photo Widget",
    contentURL: "http://www.flickr.com/explore/",
    contentScriptWhen: "ready",
    contentScript: 'postMessage(document.querySelector(".pc_img").src);' +
                   'setTimeout(function() {' +
                   '  document.location = "http://www.flickr.com/explore/";' +
                   '}, 5 * 60 * 1000);',
    onMessage: function(imgSrc) {
      this.contentURL = imgSrc;
    },
    onClick: function() {
      require("tabs").activeTab.url = this.contentURL;
    }
  });
   
  // A widget communicating bi-directionally with a content script.
  let widget = widgets.Widget({
    id: "message-test",
    label: "Bi-directional communication!",
    content: "<foo>bar</foo>",
    contentScriptWhen: "ready",
    contentScript: 'on("message", function(message) {' +
                   '  alert("Got message: " + message);' +
                   '});' +
                   'postMessage("ready");',
    onMessage: function(message) {
      if (message == "ready")
        widget.postMessage("me too");
    }
  });
  
  // Create a new context menu item.
  var menuItem = contextMenu.Item({
    label: "Translate Selection",
 
    // Show this item when a selection exists.
 
    context: contextMenu.SelectionContext(),
 
    // When this item is clicked, post a message to the item with the
    // selected text and current URL.
    contentScript: 'on("click", function () {' +
                   '  var text = window.getSelection().toString();' +
                   '  postMessage(text);' +
                   '});',
 
    // When we receive the message, call the Google Translate API with the
    // selected text and replace it with the translation.
    onMessage: function (text) {
      if (text.length == 0) {
        throw ("Text to translate must not be empty");
      }
      console.log("input: " + text)
      var req = request.Request({
        url: "http://ajax.googleapis.com/ajax/services/language/translate",
        content: {
          v: "1.0",
          q: text,
          langpair: "|en"
        },
        onComplete: function (response) {
          translated = response.json.responseData.translatedText;
          console.log("output: " + translated)
          selection.text = translated;
        }
      });
      req.get();
    }
  });
};
 
exports.onUnload = function (reason) {
  console.log(reason);
};