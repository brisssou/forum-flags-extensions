package flags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import flags.chrome.ensureBrowserNamespace
import flags.chrome.i18n.getMessage
import flags.chrome.storage.onChanged
import flags.prefs.MutedTopic
import flags.prefs.Prefs
import flags.prefs.PrefsStore
import flags.prefs.unmute
import flags.site.Hfr
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.CheckboxInput
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.NumberInput
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import org.jetbrains.compose.web.dom.Ul
import org.jetbrains.compose.web.renderComposable

private val store by lazy { PrefsStore() }

fun main() {
    ensureBrowserNamespace()
    document.title = getMessage("optionsPageTitle", arrayOf(Hfr.displayName))
    var prefs by mutableStateOf(Prefs())
    store.load().then { prefs = it }

    // Keep the muted list live: the popup (or another tab) can mute/unmute while
    // this page is open. Refresh only mutedTopics, so fields being edited here
    // (the text/number inputs) aren't disturbed by an unrelated save.
    onChanged.addListener { changes, areaName ->
        if (areaName == "local" && changes != null && changes.mutedTopics != undefined) {
            store.load().then { prefs = prefs.copy(mutedTopics = it.mutedTopics) }
        }
    }

    // Apply to local state at once (responsive controlled inputs), and persist
    // with a read-modify-write so the save never clobbers a field changed
    // elsewhere — e.g. a mute added in the popup; see PrefsStore.update.
    fun update(transform: (Prefs) -> Prefs) {
        prefs = transform(prefs)
        store.update(transform)
    }

    renderComposable(rootElementId = "root") {
        Div(attrs = { id("prefs") }) {
            PrefRow("REFRESH_TIME") { fieldId ->
                NumberInput(prefs.refreshTime) {
                    id(fieldId)
                    attr("min", Hfr.minRefreshTimeSeconds.toString())
                    onInput { event ->
                        event.value?.toInt()?.let { seconds ->
                            if (seconds >= Hfr.minRefreshTimeSeconds) update { it.copy(refreshTime = seconds) }
                        }
                    }
                }
            }
            if (prefs.refreshTime < Hfr.minRefreshTimeSeconds) {
                Div(attrs = { classes("error") }) {
                    Text(getMessage("errorMinRefreshTime", arrayOf("${Hfr.minRefreshTimeSeconds}")))
                }
            }

            PrefRow("GET_TOPICS") { id -> Checkbox(id, prefs.getTopics) { on -> update { it.copy(getTopics = on) } } }
            if (prefs.getTopics) {
                PrefRow("SHOW_CAT") { id -> Checkbox(id, prefs.showCat) { on -> update { it.copy(showCat = on) } } }
                if (prefs.showCat) {
                    PrefRow("OPEN_CAT") { id -> Checkbox(id, prefs.openCat) { on -> update { it.copy(openCat = on) } } }
                }
            }
            PrefRow("GET_MPS") { id -> Checkbox(id, prefs.getMps) { on -> update { it.copy(getMps = on) } } }
            PrefRow("ONLY_FAVS") { id -> Checkbox(id, prefs.onlyFavs) { on -> update { it.copy(onlyFavs = on) } } }
            PrefRow("NEW_TAB") { id -> Checkbox(id, prefs.newTab) { on -> update { it.copy(newTab = on) } } }
            PrefRow("USE_DIRECT_LINK") { id -> Checkbox(id, prefs.useDirectLink) { on -> update { it.copy(useDirectLink = on) } } }
            PrefRow("USE_CONTEXT_MENU") { id -> Checkbox(id, prefs.useContextMenu) { on -> update { it.copy(useContextMenu = on) } } }
            PrefRow("ANIMATED_ICON") { id -> Checkbox(id, prefs.animatedIcon) { on -> update { it.copy(animatedIcon = on) } } }
            PrefRow("DEBUG_ON") { id -> Checkbox(id, prefs.debugOn) { on -> update { it.copy(debugOn = on) } } }

            PrefRow("MAX_OPEN_ALL") { fieldId ->
                NumberInput(prefs.maxOpenAll) {
                    id(fieldId)
                    onInput { event -> event.value?.toInt()?.let { n -> update { it.copy(maxOpenAll = n) } } }
                }
            }

            PrefRow("MUTED_IN_POPUP") { id -> Checkbox(id, prefs.mutedInPopup) { on -> update { it.copy(mutedInPopup = on) } } }
            if (!prefs.mutedInPopup && prefs.mutedTopics.isNotEmpty()) {
                MutedList(prefs.mutedTopics) { topic -> update { it.unmute(topic) } }
            }

            PrefRow("BG_COLOR") { fieldId ->
                TextInput(prefs.bgColor ?: Hfr.defaultColor) {
                    id(fieldId)
                    onInput { event -> update { it.copy(bgColor = event.value) } }
                }
                Button(attrs = { onClick { update { it.copy(bgColor = Hfr.defaultColor) } } }) {
                    Text(getMessage("setDefaultColor"))
                }
            }
        }
    }
}

/** The muted-topics list with a "réactiver" button each (shown when not managing from the popup). */
@Composable
private fun MutedList(muted: List<MutedTopic>, onUnmute: (MutedTopic) -> Unit) {
    Div(attrs = { id("muted") }) {
        Div(attrs = { id("muted_label") }) { Text(getMessage("muted_label")) }
        Ul {
            muted.forEach { topic ->
                Li {
                    Text(topic.title)
                    Button(attrs = { onClick { onUnmute(topic) } }) {
                        Text(getMessage("options_unmute"))
                    }
                }
            }
        }
    }
}

@Composable
private fun PrefRow(fieldId: String, control: @Composable (String) -> Unit) {
    Div {
        Label(forId = fieldId) { Text(getMessage("${fieldId}_label")) }
        Div(attrs = { classes("control") }) { control(fieldId) }
    }
}

@Composable
private fun Checkbox(id: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    CheckboxInput(checked) {
        id(id)
        onChange { onToggle(it.value) }
    }
}
