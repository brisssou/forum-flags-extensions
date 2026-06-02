@file:JsQualifier("chrome.contextMenus")

package flags.chrome.contextMenus

import kotlin.js.Promise

/**
 * Subset of the chrome.contextMenus API used for the optional refresh entry.
 * https://developer.chrome.com/docs/extensions/reference/api/contextMenus
 */

external interface CreateProperties {
    /** Stable id, so the entry can be matched in [onClicked]. */
    var id: String?

    /** Label shown in the menu. */
    var title: String?

    /** Where the entry appears (e.g. `["page"]`); defaults to the page context. */
    var contexts: Array<String>?
}

external interface OnClickData {
    /** Id of the clicked menu item (matches [CreateProperties.id]). */
    var menuItemId: dynamic
}

/** Creates a menu item; returns its id. */
external fun create(createProperties: CreateProperties): dynamic

/** Removes every menu item this extension added. */
external fun removeAll(): Promise<Unit>

external object onClicked {
    /** Fired when any of this extension's menu items is clicked. */
    fun addListener(callback: (info: OnClickData, tab: dynamic) -> Unit)
}
