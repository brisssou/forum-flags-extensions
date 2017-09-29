
var popupContent = new PopupContent();
var canvas;
var canvasContext;
var iconImage;
var cats = new Array();

var requestTimeoutId = new Array();
var site = new LesNum();

$(document).ready(function() {
	iconImage = document.getElementById('icon')
	canvas = document.getElementById('canvas');
	canvasContext = canvas.getContext('2d');
	chrome.browserAction.onClicked.addListener(goToHfr);
	//chrome.browserAction.onClicked.addListener(startRequest);
	initBehaviour();
	startRequest();
});