
function initBehaviour() {
  if (getPref(USE_DIRECT_LINK_PREF)) {
  	chrome.browserAction.setPopup({popup:""});
  } else {
  	chrome.browserAction.setPopup({popup:"pop.html"});
  }
  if (getPref(USE_CONTEXT_MENU_PREF)) {
    chrome.contextMenus.create({title:"Rafraîssement HFR", onclick:startRequest});
  }
}

function initBrowserActionTitle() {
  var tooltip;
  if (getPref(ONLY_FAVS_PREF)) {
    tooltip = "Favoris";
  } else {
    tooltip = "Drapeaux cyans";
  }
  chrome.browserAction.setTitle({title:tooltip});
}

function goToPage(url) {
  chrome.tabs.getAllInWindow(undefined, function(tabs){

  for (var i = 0, tab; tab = tabs[i]; i++) {
      if (tab.url && tab.url == url) {
        chrome.tabs.update(tab.id, {selected: true});
        return;
      }
    }
    chrome.tabs.create({url: url});
  });
}

function goToHfr(){
  goToPage(getUsedURL());
}

function updateBadge(nbUnread) {
  if (nbUnread && nbUnread != null && parseInt(nbUnread) != NaN && parseInt(nbUnread) > 0) {
    chrome.browserAction.setBadgeText({text:""+nbUnread});
    if (getPref(ANIMATED_ICON_PREF)) animateFlip();
  } else {
    chrome.browserAction.setBadgeText({text:""});
  }
}

/*to rotate the browser action icon, (not so) shamelessly copied from google Examples*/
  
var rotation = 0;
var animationFrames = 36;
var animationSpeed = 10; // ms


function ease(x) {
  return (1-Math.sin(Math.PI/2+x*Math.PI))/2;
}

function animateFlip() {
  rotation += 1/animationFrames;
  drawIconAtRotation();

  if (rotation <= 1) {
    setTimeout("animateFlip()", animationSpeed);
  } else {
    rotation = 0;
    drawIconAtRotation();
  }
}

function drawIconAtRotation() {
  var bgPage = chrome.extension.getBackgroundPage();
  var canvas = bgPage.canvas;
  var canvasContext = bgPage.canvasContext;
  var iconImage = bgPage.iconImage;
  canvasContext.save();
  canvasContext.clearRect(0, 0, canvas.width, canvas.height);
  canvasContext.translate(
      Math.ceil(canvas.width/2),
      Math.ceil(canvas.height/2));
  canvasContext.rotate(2*Math.PI*ease(rotation));
  canvasContext.drawImage(iconImage,
      -Math.ceil(canvas.width/2),
      -Math.ceil(canvas.height/2));
  canvasContext.restore();

  var imageData = canvasContext.getImageData(0, 0, canvas.width,canvas.height);
  if (imageData instanceof ImageData)
    chrome.browserAction.setIcon({imageData: imageData});
}
