function debug(msg) {
	if (getPref(DEBUG_ON)) {
		log("DEBUG",msg);
	}
}

function info(msg) {
	log("INFO", msg);
}

function error(msg) {
	log("ERROR", msg);
}

function log(lvl, msg) {
	console.log(new Date() + " [" + lvl + "] "+msg);
}
