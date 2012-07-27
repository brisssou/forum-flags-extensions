hfr-drap4ff.onFirefoxLoad = function(event) {
  document.getElementById("contentAreaContextMenu")
          .addEventListener("popupshowing", function (e){ hfr-drap4ff.showFirefoxContextMenu(e); }, false);
};

hfr-drap4ff.showFirefoxContextMenu = function(event) {
  // show or hide the menuitem based on what the context menu is on
  document.getElementById("context-hfr-drap4ff").hidden = gContextMenu.onImage;
};

window.addEventListener("load", function () { hfr-drap4ff.onFirefoxLoad(); }, false);
