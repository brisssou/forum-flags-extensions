function debug(msg) {
	if (getPref(DEBUG_ON)) {
		log("DEBUG",msg);
	}
}

function info(msg) {
	log("INFO", msg);
}

function error(msg) {
	console.error("[ERROR]", msg);
}

function log(lvl, msg) {
	console.log("[" + lvl + "] "+msg);
}