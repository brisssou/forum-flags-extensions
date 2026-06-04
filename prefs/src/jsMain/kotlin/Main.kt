package flags

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import flags.chrome.ensureBrowserNamespace
import flags.chrome.i18n.getMessage
import flags.prefs.Prefs
import flags.prefs.PrefsStore
import flags.site.Hfr
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.CheckboxInput
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.NumberInput
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import org.jetbrains.compose.web.renderComposable

private val store by lazy { PrefsStore() }

fun main() {
    ensureBrowserNamespace()
    document.title = getMessage("optionsPageTitle", arrayOf(Hfr.displayName))
    var prefs by mutableStateOf(Prefs())
    store.load().then { prefs = it }

    fun update(next: Prefs) {
        prefs = next
        store.save(next)
    }

    renderComposable(rootElementId = "root") {
        Div(attrs = { id("prefs") }) {
            PrefRow("REFRESH_TIME") { fieldId ->
                NumberInput(prefs.refreshTime) {
                    id(fieldId)
                    attr("min", Hfr.minRefreshTimeSeconds.toString())
                    onInput { event ->
                        event.value?.toInt()?.let {
                            if (it >= Hfr.minRefreshTimeSeconds) update(prefs.copy(refreshTime = it))
                        }
                    }
                }
            }
            if (prefs.refreshTime < Hfr.minRefreshTimeSeconds) {
                Div(attrs = { classes("error") }) {
                    Text(getMessage("errorMinRefreshTime", arrayOf("${Hfr.minRefreshTimeSeconds}")))
                }
            }

            PrefRow("GET_TOPICS") { id -> Checkbox(id, prefs.getTopics) { update(prefs.copy(getTopics = it)) } }
            if (prefs.getTopics) {
                PrefRow("SHOW_CAT") { id -> Checkbox(id, prefs.showCat) { update(prefs.copy(showCat = it)) } }
                if (prefs.showCat) {
                    PrefRow("OPEN_CAT") { id -> Checkbox(id, prefs.openCat) { update(prefs.copy(openCat = it)) } }
                }
            }
            PrefRow("GET_MPS") { id -> Checkbox(id, prefs.getMps) { update(prefs.copy(getMps = it)) } }
            PrefRow("ONLY_FAVS") { id -> Checkbox(id, prefs.onlyFavs) { update(prefs.copy(onlyFavs = it)) } }
            PrefRow("NEW_TAB") { id -> Checkbox(id, prefs.newTab) { update(prefs.copy(newTab = it)) } }
            PrefRow("USE_DIRECT_LINK") { id -> Checkbox(id, prefs.useDirectLink) { update(prefs.copy(useDirectLink = it)) } }
            PrefRow("USE_CONTEXT_MENU") { id -> Checkbox(id, prefs.useContextMenu) { update(prefs.copy(useContextMenu = it)) } }
            PrefRow("ANIMATED_ICON") { id -> Checkbox(id, prefs.animatedIcon) { update(prefs.copy(animatedIcon = it)) } }
            PrefRow("DEBUG_ON") { id -> Checkbox(id, prefs.debugOn) { update(prefs.copy(debugOn = it)) } }

            PrefRow("MAX_OPEN_ALL") { fieldId ->
                NumberInput(prefs.maxOpenAll) {
                    id(fieldId)
                    onInput { event -> event.value?.toInt()?.let { update(prefs.copy(maxOpenAll = it)) } }
                }
            }

            PrefRow("BG_COLOR") { fieldId ->
                TextInput(prefs.bgColor ?: Hfr.defaultColor) {
                    id(fieldId)
                    onInput { event -> update(prefs.copy(bgColor = event.value)) }
                }
                Button(attrs = { onClick { update(prefs.copy(bgColor = Hfr.defaultColor)) } }) {
                    Text(getMessage("setDefaultColor"))
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
