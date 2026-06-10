package flags

import flags.chrome.action.Companion.BadgeColorDetails
import flags.chrome.action.Companion.BadgeTextDetails
import flags.chrome.action.Companion.PopupDetails
import flags.chrome.action.Companion.TitleDetails
import flags.chrome.action.onClicked as actionClicked
import flags.chrome.action.setBadgeBackgroundColor
import flags.chrome.action.setBadgeText
import flags.chrome.action.setPopup
import flags.chrome.action.setTitle
import flags.chrome.alarm.Companion.AlarmCreateInfo
import flags.chrome.alarm.create
import flags.chrome.alarm.onAlarm
import flags.chrome.contextMenus.Companion.CreateProperties as MenuCreateProperties
import flags.chrome.contextMenus.create as createMenu
import flags.chrome.contextMenus.onClicked as menuClicked
import flags.chrome.contextMenus.removeAll
import flags.chrome.ensureBrowserNamespace
import flags.chrome.i18n.getMessage
import flags.chrome.runtime.onMessage
import flags.chrome.storage.onChanged
import flags.chrome.tabs.Companion.CreateProperties as TabCreateProperties
import flags.chrome.tabs.create as createTab
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

/**
 * Delay before the catch-up re-poll after the popup opens links. Driven by
 * setTimeout, NOT chrome.alarms: alarms clamp sub-minute delays to ~30–60s
 * (far too late to feel live), whereas the worker is still alive right after
 * the message, so a short timeout fires on time.
 */
private const val SOON_DELAY_MS = 1000

private external fun setTimeout(handler: () -> Unit, timeoutMs: Int): Int

/** Id of the optional context-menu refresh entry. */
private const val MENU_ID = "forum-flags-refresh"

/** Badge backgrounds: red normally, blue when there are new private messages. */
private val RED = arrayOf(255, 0, 0, 255)
private val BLUE = arrayOf(0, 0, 255, 255)

/** Badge text shown while a poll is in flight, until the result overwrites it. */
private const val REFRESHING = "…"

private val prefsStore by lazy { PrefsStore() }
private val snapshotStore by lazy { SnapshotStore() }

private fun setBadge(text: String, color: Array<Int>) {
    setBadgeBackgroundColor(BadgeColorDetails { this.color = color })
    setBadgeText(BadgeTextDetails { this.text = text })
}

/** The forum page the badge tracks: favourites when `onlyFavs`, else all flagged. */
private fun forumUrl(prefs: Prefs): String = if (prefs.onlyFavs) Hfr.favsUrl() else Hfr.drapsUrl()

/** (Re)arms the periodic poll alarm at the pref's interval (clamped to the site minimum). */
private fun armAlarm(prefs: Prefs) {
    val seconds = max(prefs.refreshTime, Hfr.minRefreshTimeSeconds)
    create(ALARM, AlarmCreateInfo { periodInMinutes = seconds / 60.0 })
}

/**
 * Applies the click-behavior prefs: with `useDirectLink` the icon has no popup
 * (so clicking it fires [actionClicked], which opens the forum); the
 * `useContextMenu` refresh entry is (re)created or removed.
 */
private fun applyBehavior(prefs: Prefs) {
    setPopup(PopupDetails { popup = if (prefs.useDirectLink) "" else "popup.html" })
    removeAll().then {
        if (prefs.useContextMenu) {
            createMenu(
                MenuCreateProperties {
                    id = MENU_ID
                    title = getMessage("refresh_menu_label", arrayOf(Hfr.displayName))
                },
            )
        }
    }
}

/**
 * The hover tooltip mirrors the popup: the new-MP count line (when any),
 * then one flagged-topic title per line. Empty when logged out / nothing new.
 */
private fun titleFor(snapshot: Snapshot): String {
    val lines = mutableListOf<String>()
    if (snapshot.mps > 0) {
        val label = if (snapshot.mps > 1) getMessage("private_messages") else getMessage("private_message")
        lines += "${snapshot.mps} $label"
    }
    snapshot.topics.forEach { lines += it.title }
    return lines.joinToString("\n")
}

/**
 * Badge + tooltip from the snapshot, so neither drifts from what the popup
 * shows. Badge: "x" when logged out; otherwise (flagged topics + new MPs) —
 * blue when MPs > 0, red otherwise, blank at 0. Tooltip: [titleFor].
 */
private fun updateBadge(snapshot: Snapshot) {
    setTitle(TitleDetails { title = titleFor(snapshot) })
    if (!snapshot.loggedIn) {
        setBadge("x", RED)
        return
    }
    val total = snapshot.topics.size + snapshot.mps
    setBadge(if (total == 0) "" else total.toString(), if (snapshot.mps > 0) BLUE else RED)
}

/** Polls the page the prefs select, shapes it by the prefs, updates the badge and caches it. */
private fun refreshWith(prefs: Prefs): Promise<Snapshot> {
    // Mark the badge as refreshing while the poll is in flight; the result
    // overwrites it. Text only, so the colour persists from the last poll.
    setBadgeText(BadgeTextDetails { text = REFRESHING })
    return fetchPage(forumUrl(prefs)).then { html ->
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
    ensureBrowserNamespace()
    console.info("starting worker")
    prefsStore.load().then { prefs ->
        armAlarm(prefs)
        applyBehavior(prefs)
    }
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
                // Re-poll shortly after, no reply needed (see SOON_DELAY_MS).
                setTimeout({ refresh().catch { e -> console.warn("forum-flags: refresh failed", e) } }, SOON_DELAY_MS)
                false
            }
            else -> false
        }
    }
    actionClicked.addListener {
        // Fires only when the popup is disabled (direct-link on): open the forum.
        prefsStore.load().then { prefs -> createTab(TabCreateProperties { url = forumUrl(prefs) }) }
    }
    menuClicked.addListener { info, _ ->
        if (info.menuItemId == MENU_ID) {
            refresh().catch { e -> console.warn("forum-flags: refresh failed", e) }
        }
    }
    onChanged.addListener { changes, areaName ->
        // Prefs changed (options edit or popup mute): re-time the alarm, re-apply
        // the click behaviour, and re-poll now.
        if (areaName == "local" && !onlySnapshotChanged(changes)) {
            prefsStore.load().then { prefs ->
                armAlarm(prefs)
                applyBehavior(prefs)
                refreshWith(prefs).catch { e -> console.warn("forum-flags: refresh failed", e) }
            }
        }
    }
    refresh().catch { e -> console.warn("forum-flags: refresh failed", e) }
    console.info("Listener added")
}
