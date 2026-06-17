package flags.chrome.tabs

import flags.chrome.tabs.Companion.CreateProperties
import flags.chrome.tabs.Companion.QueryInfo
import flags.chrome.tabs.Companion.UpdateProperties
import kotlin.js.Promise

/**
 * Opens [url]. When [reuse] is true the current window is scanned first: the
 * first tab whose [matchKey] equals the target's is focused and refreshed —
 * this wins over [newTab], so a tab already showing the page is reused even when
 * a fresh tab was requested. The refresh is unconditional: a tab on a different
 * url is navigated to [url] (which reloads), one already on [url] is reloaded
 * explicitly (navigating to the same url would not). With no match (or when
 * [reuse] is false, so the window is not scanned at all) [newTab] decides:
 * a fresh tab when true, the active tab when false.
 *
 * [reuse] is given explicitly by every caller: true when the target is worth
 * reusing for — an always single destination (the forum page) or the same topic
 * on any page — false otherwise. [matchKey] normalizes a url to the identity
 * tabs match on, applied to both [url] and each tab's url; the default drops the
 * `#fragment` (a no-op when there is none). Shared by the worker (icon click)
 * and the popup.
 */
fun openOrReuse(
    url: String,
    newTab: Boolean,
    reuse: Boolean,
    matchKey: (url: String) -> String = { it.substringBefore('#') },
): Promise<dynamic> {
    if (!reuse) return if (newTab) create(CreateProperties { this.url = url }) else updateActive(url)
    val key = matchKey(url)
    return query(QueryInfo { currentWindow = true }).then { tabs ->
        val existing = tabs.firstOrNull { it.url?.let(matchKey) == key }
        val existingId = existing?.id
        when {
            existingId == null -> if (newTab) create(CreateProperties { this.url = url }) else updateActive(url)
            existing?.url == url -> {
                // Already on the target: focus it, and reload — navigating to the same url would not refresh.
                update(existingId, UpdateProperties { active = true })
                reload(existingId)
            }
            else -> update(existingId, UpdateProperties { active = true; this.url = url })
        }
    }
}

private fun updateActive(url: String): Promise<dynamic> = update(UpdateProperties { this.url = url })
