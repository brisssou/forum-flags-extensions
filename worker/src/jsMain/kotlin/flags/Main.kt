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

/** 1.0 badge background; blue (MP) is added with the private-messages milestone. */
private val RED = arrayOf(255, 0, 0, 255)

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
 * Fetches the draps page, counts flagged topics and reflects it on the badge.
 * As in 1.0 the count is the number of flagged topics; an empty count blanks
 * the badge. Logged-out ("x") and private-message handling land later.
 */
fun refreshBadge(): Promise<Unit> =
    fetchPage(Hfr.drapsUrl()).then { html ->
        val topics = Hfr.parseUnread(html)
        console.info("forum-flags: parsed ${topics.size} flagged topics")
        setBadgeBackgroundColor(BadgeColorDetails { color = RED })
        setBadgeText(BadgeTextDetails { text = if (topics.isEmpty()) "" else topics.size.toString() })
    }.catch { e ->
        console.warn("forum-flags: refresh failed", e)
    }

fun main() {
    console.info("starting worker")
    createAlarm()
    onAlarm.addListener { refreshBadge() }
    refreshBadge()
    console.info("Listener added")
}
