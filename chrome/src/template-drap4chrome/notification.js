$(document).ready(function () {
		bg = browser.extension.getBackgroundPage();
		site = bg.site;
		bg.debug("Initiating notification");
		document.getElementById('title').innerText = browser.i18n
				.getMessage("extName");
		bg.debug("title set");
		message = document.getElementById('message');
		message.innerText = browser.i18n
				.getMessage("not_connected");
		bg.debug("message set");
		message.href = site.getFullUrl(site.getForumRoot()+'/');
		message.target = "_blank";
		bg.debug("href set");
		setTimeout(function() {
			bg.debug("about to die");
			window.close();
		}, 15000);
	});