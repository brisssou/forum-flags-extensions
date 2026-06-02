package flags

import flags.chrome.action.Companion.BadgeColorDetails
import flags.chrome.action.Companion.BadgeTextDetails
import flags.chrome.action.setBadgeBackgroundColor
import flags.chrome.action.setBadgeText
import flags.chrome.alarm.Companion.AlarmCreateInfo
import flags.chrome.alarm.create
import flags.chrome.alarm.onAlarm
import flags.chrome.runtime.onMessage
import flags.chrome.storage.onChanged
import flags.message.Messages
import flags.net.fetchPage
import flags.prefs.Prefs
import flags.prefs.PrefsStore
import flags.site.Hfr
import flags.snapshot.Mapper.toRecord
import flags.snapshot.Snapshot
import flags.snapshot.SnapshotStore
import flags.snapshot.forPrefs
import kotlin.js.Date
import kotlin.js.Promise
import kotlin.math.max

private const val ALARM = "forum-flags-alarm"

/** One-shot alarm armed after the popup opens links, to re-poll shortly after. */
private const val SOON_ALARM = "forum-flags-refresh-soon"
private const val SOON_DELAY_MINUTES = 0.1

/** Badge backgrounds: red normally, blue when there are new private messages. */
private val RED = arrayOf(255, 0, 0, 255)
private val BLUE = arrayOf(0, 0, 255, 255)

private val prefsStore = PrefsStore()
private val snapshotStore = SnapshotStore()

private fun setBadge(text: String, color: Array<Int>) {
    setBadgeBackgroundColor(BadgeColorDetails { this.color = color })
    setBadgeText(BadgeTextDetails { this.text = text })
}

/** (Re)arms the periodic poll alarm at the pref's interval (clamped to the site minimum). */
private fun armAlarm(prefs: Prefs) {
    val seconds = max(prefs.refreshTime, Hfr.minRefreshTimeSeconds)
    create(ALARM, AlarmCreateInfo { periodInMinutes = seconds / 60.0 })
}

/**
 * Badge from the snapshot, so it never drifts from what the popup shows. "x"
 * when logged out; otherwise (flagged topics + new MPs) — blue when MPs > 0,
 * red otherwise, blank at 0.
 */
private fun updateBadge(snapshot: Snapshot) {
    if (!snapshot.loggedIn) {
        setBadge("x", RED)
        return
    }
    val total = snapshot.topics.size + snapshot.mps
    setBadge(if (total == 0) "" else total.toString(), if (snapshot.mps > 0) BLUE else RED)
}

/** Polls the page the prefs select, shapes it by the prefs, updates the badge and caches it. */
private fun refreshWith(prefs: Prefs): Promise<Snapshot> =
    fetchPage(if (prefs.onlyFavs) Hfr.favsUrl() else Hfr.drapsUrl()).then { html ->
        val snapshot = Hfr.snapshot(html, Date.now()).forPrefs(prefs)
        if (prefs.debugOn) {
            console.info(
                "forum-flags: ${snapshot.topics.size} flagged topics, " +
                    "${snapshot.mps} new MPs, loggedIn=${snapshot.loggedIn}",
            )
        }
        updateBadge(snapshot)
        snapshotStore.save(snapshot)
        snapshot
    }

/** Loads the prefs, then polls with them. */
fun refresh(): Promise<Snapshot> =
    prefsStore.load().then(::refreshWith).unsafeCast<Promise<Snapshot>>()

/**
 * A storage change is ours to ignore when its only key is the snapshot the
 * worker just wrote — otherwise reacting would loop (save → onChanged → save).
 */
private fun onlySnapshotChanged(changes: dynamic): Boolean =
    js("Object.keys")(changes).unsafeCast<Array<String>>().all { it == SnapshotStore.KEY }

fun main() {
    console.info("starting worker")
    prefsStore.load().then(::armAlarm)
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
    onChanged.addListener { changes, areaName ->
        // Prefs changed (options edit or popup mute): re-time the alarm and re-poll now.
        if (areaName == "local" && !onlySnapshotChanged(changes)) {
            prefsStore.load().then { prefs ->
                armAlarm(prefs)
                refreshWith(prefs).catch { e -> console.warn("forum-flags: refresh failed", e) }
            }
        }
    }
    refresh().catch { e -> console.warn("forum-flags: refresh failed", e) }
    console.info("Listener added")
}
