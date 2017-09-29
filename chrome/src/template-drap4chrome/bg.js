var popupContent = new PopupContent();
var canvas;
var canvasContext;
var iconImage;
var cats = new Array();

var requestTimeoutId = new Array();
var site = new Hfr();
var notification = webkitNotifications.createHTMLNotification('notification.html');

$(document).ready(function() {
	iconImage = document.getElementById('icon')
	canvas = document.getElementById('canvas');
	canvasContext = canvas.getContext('2d');
	browser.browserAction.onClicked.addListener(goToHfr);
	//browser.browserAction.onClicked.addListener(startRequest);
	initBehaviour();
	startRequest();
});