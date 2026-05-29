package flags

import flags.chrome.action.Companion.BadgeColorDetails
import flags.chrome.action.Companion.BadgeTextDetails
import flags.chrome.action.setBadgeBackgroundColor
import flags.chrome.action.setBadgeText
import flags.chrome.alarm.Companion.AlarmCreateInfo
import flags.chrome.alarm.create
import flags.chrome.alarm.get
import flags.chrome.alarm.onAlarm
import flags.net.fetchPage
import flags.site.Hfr
import kotlin.js.Promise

private const val ALARM = "forum-flags-alarm"

/** 1.0 badge backgrounds: red normally, blue when there are new private messages. */
private val RED = arrayOf(255, 0, 0, 255)
private val BLUE = arrayOf(0, 0, 255, 255)

private fun setBadge(text: String, color: Array<Int>) {
    setBadgeBackgroundColor(BadgeColorDetails { this.color = color })
    setBadgeText(BadgeTextDetails { this.text = text })
}

fun createAlarm() {
    get(ALARM).then {
        if (it == null) {
            create(
                ALARM,
                AlarmCreateInfo {
                    periodInMinutes = 1.0
                    delayInMinutes = 1.0
                }
            ).then { console.info("Alarm set") }
        } else {
            console.info("Alarm $ALARM already set")
        }
    }
}

/**
 * The draps page header carries both the flagged topics and the new-MP notice,
 * so one fetch is enough. "x" when logged out; otherwise show (flagged topics +
 * new MPs) as in 1.0 — blue when MPs > 0, red otherwise, blank at 0. The
 * `GET_MPS` gate lands with prefs.
 */
private fun updateBadgeFromDrapsPage(html: String) {
    if (Hfr.isNotLoggedIn(html)) {
        setBadge("x", RED)
        return
    }
    val topics = Hfr.parseUnread(html)
    val mps = Hfr.parseMps(html)
    val total = topics.size + mps
    console.info("forum-flags: ${topics.size} flagged topics, $mps new MPs")
    setBadge(if (total == 0) "" else total.toString(), if (mps > 0) BLUE else RED)
}

fun refreshBadge(): Promise<Unit> =
    fetchPage(Hfr.drapsUrl())
        .then(::updateBadgeFromDrapsPage)
        .catch { e -> console.warn("forum-flags: refresh failed", e) }

fun main() {
    console.info("starting worker")
    createAlarm()
    onAlarm.addListener { refreshBadge() }
    refreshBadge()
    console.info("Listener added")
}
