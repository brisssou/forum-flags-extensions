package flags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import flags.chrome.ensureBrowserNamespace
import flags.chrome.i18n.getMessage
import flags.chrome.runtime.openOptionsPage
import flags.chrome.runtime.sendMessage
import flags.chrome.tabs.Companion.CreateProperties
import flags.chrome.tabs.Companion.UpdateProperties
import flags.chrome.tabs.create
import flags.chrome.tabs.update
import flags.message.Messages
import flags.model.Topic
import flags.prefs.Prefs
import flags.prefs.PrefsStore
import flags.prefs.isMuted
import flags.prefs.mute
import flags.site.Hfr
import flags.snapshot.Mapper.fromRecord
import flags.snapshot.Snapshot
import flags.snapshot.SnapshotStore
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.jetbrains.compose.web.renderComposable

private val snapshotStore by lazy { SnapshotStore() }
private val prefsStore by lazy { PrefsStore() }

fun main() {
    ensureBrowserNamespace()
    var snapshot by mutableStateOf(Snapshot())
    var prefs by mutableStateOf(Prefs())
    var refreshing by mutableStateOf(false)

    // Render the cached snapshot immediately, then refine as the stores resolve.
    snapshotStore.load().then { snapshot = it }
    prefsStore.load().then { prefs = it }

    fun refresh() {
        if (refreshing) return
        refreshing = true
        sendMessage(message(Messages.REFRESH))
            .then<Unit> { record ->
                if (record != null) snapshot = Snapshot.fromRecord(record)
                refreshing = false
            }
            .catch { e ->
                console.warn("forum-flags: refresh failed", e)
                refreshing = false
            }
    }

    fun toggleMute(topic: Topic) {
        prefs = prefs.mute(topic)
        prefsStore.save(prefs)
    }

    renderComposable(rootElementId = "root") {
        Popup(snapshot, prefs, refreshing, onRefresh = ::refresh, onMute = ::toggleMute)
    }
}

@Composable
private fun Popup(
    snapshot: Snapshot,
    prefs: Prefs,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onMute: (Topic) -> Unit,
) {
    val visible = snapshot.topics.filter { !prefs.isMuted(it) }

    Div(attrs = { id("popup") }) {
        Header(
            showOpenAll = snapshot.loggedIn && visible.isNotEmpty(),
            refreshing = refreshing,
            onOptions = { openOptionsPage() },
            onOpenAll = { openAll(visible, prefs) },
            onRefresh = onRefresh,
            onGoToSite = { openTab(Hfr.drapsUrl(), prefs.newTab) },
        )

        Div(attrs = { id("entries") }) {
            if (!snapshot.loggedIn) {
                Div(attrs = { classes("notice") }) { Text(getMessage("not_connected")) }
            } else {
                Ul(attrs = { style { backgroundColor(Color(prefs.bgColor ?: Hfr.defaultColor)) } }) {
                    if (snapshot.mps > 0) MpLine(snapshot.mps, prefs)
                    if (prefs.showCat) {
                        visible.groupBy { it.categoryId }.forEach { (categoryId, topics) ->
                            CategorySection(categoryId, snapshot.categories[categoryId] ?: categoryId, topics, prefs, onMute)
                        }
                    } else if (visible.isNotEmpty()) {
                        Li { Ul { visible.forEach { TopicRow(it, prefs, onMute) } } }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    showOpenAll: Boolean,
    refreshing: Boolean,
    onOptions: () -> Unit,
    onOpenAll: () -> Unit,
    onRefresh: () -> Unit,
    onGoToSite: () -> Unit,
) {
    Div(attrs = { id("headbar") }) {
        Ul(attrs = { id("left") }) {
            Li { IconButton("images/icons8-settings-filled.png", getMessage("options"), onOptions) }
        }
        Ul(attrs = { id("right") }) {
            if (showOpenAll) Li { IconButton("images/icons8-chevron-filled.png", getMessage("open_all"), onOpenAll) }
            Li { IconButton("images/icons8-refresh.png", getMessage("refresh"), onRefresh, disabled = refreshing) }
            Li { IconButton("images/favicon.ico", Hfr.displayName, onGoToSite) }
        }
    }
}

@Composable
private fun IconButton(src: String, title: String, action: () -> Unit, disabled: Boolean = false) {
    Img(src = src, attrs = {
        attr("title", title)
        if (disabled) classes("disabled") else onClick { action() }
    })
}

@Composable
private fun MpLine(mps: Int, prefs: Prefs) {
    val url = Hfr.mpsUrl()
    val label = if (mps > 1) getMessage("private_messages") else getMessage("private_message")
    Li(attrs = { classes("mp") }) {
        A(href = url, attrs = { onClick { it.preventDefault(); openLink(url, prefs.newTab) } }) {
            Text("$mps $label")
        }
    }
}

@Composable
private fun CategorySection(
    categoryId: String,
    name: String,
    topics: List<Topic>,
    prefs: Prefs,
    onMute: (Topic) -> Unit,
) {
    Li {
        A(href = "#", attrs = {
            classes("category")
            onClick { it.preventDefault(); openCategory(categoryId, topics, prefs) }
        }) {
            Text(name)
        }
        Ul { topics.forEach { TopicRow(it, prefs, onMute) } }
    }
}

@Composable
private fun TopicRow(topic: Topic, prefs: Prefs, onMute: (Topic) -> Unit) {
    val url = "https://${Hfr.host}${topic.href}"
    Li {
        A(attrs = {
            classes("mute")
            onClick { it.preventDefault(); onMute(topic) }
        }) {
            Img(src = "images/mute.gif", alt = "", attrs = { attr("title", getMessage("mute")) })
        }
        Text(" ")
        A(href = url, attrs = {
            attr("title", unreadLabel(topic.nbUnread))
            onClick { it.preventDefault(); openLink(url, prefs.newTab) }
        }) {
            Text(topic.title)
        }
    }
}

private fun unreadLabel(nbUnread: Int): String = when {
    nbUnread > 1 -> getMessage("new_pages", arrayOf(nbUnread.toString()))
    nbUnread == 1 -> getMessage("new_page")
    else -> getMessage("no_new_page")
}

/** Opens [url] honoring the new-tab pref: a fresh tab, or the current one. */
private fun openTab(url: String, newTab: Boolean) {
    if (newTab) create(CreateProperties { this.url = url })
    else update(UpdateProperties { this.url = url })
}

/** Asks the worker to re-poll shortly, so the badge catches up once a page is read. */
private fun scheduleRefresh() {
    sendMessage(message(Messages.REFRESH_SOON))
}

/** Opens a single clicked link and schedules one catch-up re-poll. */
private fun openLink(url: String, newTab: Boolean) {
    openTab(url, newTab)
    scheduleRefresh()
}

/**
 * Opens every topic in fresh tabs (confirming first past the maxOpenAll
 * threshold), then schedules a single catch-up re-poll for the whole batch.
 */
private fun openAll(topics: List<Topic>, prefs: Prefs) {
    if (topics.size < prefs.maxOpenAll ||
        window.confirm(getMessage("too_many_new_tabs", arrayOf(topics.size.toString())))
    ) {
        topics.forEach { openTab("https://${Hfr.host}${it.href}", newTab = true) }
        scheduleRefresh()
    }
}

/** Clicking a category opens all its topics, or navigates to it, per the openCat pref. */
private fun openCategory(categoryId: String, topics: List<Topic>, prefs: Prefs) {
    if (prefs.openCat) openAll(topics, prefs)
    else openLink(Hfr.ownCatUrl(categoryId), prefs.newTab)
}

private fun message(type: Messages): dynamic {
    val message = js("{}")
    message.type = type.name
    return message
}
