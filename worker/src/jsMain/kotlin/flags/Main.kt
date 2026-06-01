package flags

import flags.chrome.action.Companion.BadgeColorDetails
import flags.chrome.action.Companion.BadgeTextDetails
import flags.chrome.action.setBadgeBackgroundColor
import flags.chrome.action.setBadgeText
import flags.chrome.alarm.Companion.AlarmCreateInfo
import flags.chrome.alarm.create
import flags.chrome.alarm.get
import flags.chrome.alarm.onAlarm
import flags.chrome.runtime.onMessage
import flags.message.Messages
import flags.net.fetchPage
import flags.site.Hfr
import flags.snapshot.Mapper.toRecord
import flags.snapshot.Snapshot
import flags.snapshot.SnapshotStore
import kotlin.js.Date
import kotlin.js.Promise

private const val ALARM = "forum-flags-alarm"

/** One-shot alarm armed after the popup opens links, to re-poll shortly after. */
private const val SOON_ALARM = "forum-flags-refresh-soon"
private const val SOON_DELAY_MINUTES = 0.1

/** Badge backgrounds: red normally, blue when there are new private messages. */
private val RED = arrayOf(255, 0, 0, 255)
private val BLUE = arrayOf(0, 0, 255, 255)

private val store = SnapshotStore()

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
 * Badge from the snapshot, so it never drifts from what the popup shows. "x"
 * when logged out; otherwise (flagged topics + new MPs) — blue when MPs > 0,
 * red otherwise, blank at 0. The `GET_MPS` gate lands with prefs.
 */
private fun updateBadge(snapshot: Snapshot) {
    if (!snapshot.loggedIn) {
        setBadge("x", RED)
        return
    }
    val total = snapshot.topics.size + snapshot.mps
    setBadge(if (total == 0) "" else total.toString(), if (snapshot.mps > 0) BLUE else RED)
}

/** Polls the forum, updates the badge, caches the snapshot, and returns it. */
fun refresh(): Promise<Snapshot> =
    fetchPage(Hfr.drapsUrl()).then { html ->
        val snapshot = Hfr.snapshot(html, Date.now())
        console.info(
            "forum-flags: ${snapshot.topics.size} flagged topics, " +
                "${snapshot.mps} new MPs, loggedIn=${snapshot.loggedIn}",
        )
        updateBadge(snapshot)
        store.save(snapshot)
        snapshot
    }

fun main() {
    console.info("starting worker")
    createAlarm()
    onAlarm.addListener { refresh().catch { e -> console.warn("forum-flags: refresh failed", e) } }
    onMessage.addListener { message, _, sendResponse ->
        when (message?.type) {
            Messages.REFRESH.name -> {
                refresh()
                    .then { sendResponse(it.toRecord()) }
                    .catch { e ->
                        console.warn("forum-flags: refresh failed", e)
                        sendResponse(null)
                    }
                true // keep the channel open for the asynchronous sendResponse above
            }
            Messages.REFRESH_SOON.name -> {
                // Re-poll a little later (one-shot), no reply needed.
                create(SOON_ALARM, AlarmCreateInfo { delayInMinutes = SOON_DELAY_MINUTES })
                false
            }
            else -> false
        }
    }
    refresh().catch { e -> console.warn("forum-flags: refresh failed", e) }
    console.info("Listener added")
}
