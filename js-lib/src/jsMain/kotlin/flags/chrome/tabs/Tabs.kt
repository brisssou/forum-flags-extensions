@file:JsQualifier("browser.tabs")

package flags.chrome.tabs

import kotlin.js.Promise

/**
 * Subset of the chrome.tabs API used to open forum links from the popup.
 * https://developer.chrome.com/docs/extensions/reference/api/tabs
 */

external interface CreateProperties {
    /** The url to open in the new tab. */
    var url: String?

    /** Whether the new tab becomes the active tab in its window (default true). */
    var active: Boolean?
}

external interface UpdateProperties {
    /** The url to navigate the tab to. */
    var url: String?

    /** Whether the tab becomes the active tab in its window. */
    var active: Boolean?
}

external interface QueryInfo {
    /** Restrict to tabs in the current window. */
    var currentWindow: Boolean?

    /** Restrict to the active tab in each window. */
    var active: Boolean?
}

external interface Tab {
    /** The tab id, used to target [update]. */
    val id: Int?

    /** The tab's current url (populated only with the `tabs` or host permission). */
    val url: String?
}

/** Opens a new tab. */
external fun create(createProperties: CreateProperties): Promise<dynamic>

/** Navigates the current (or given) tab; with no tab id, updates the active tab. */
external fun update(updateProperties: UpdateProperties): Promise<dynamic>

/** Navigates the tab with [tabId]. */
external fun update(tabId: Int, updateProperties: UpdateProperties): Promise<dynamic>

/** Lists open tabs matching [queryInfo]. */
external fun query(queryInfo: QueryInfo): Promise<Array<Tab>>

/** Reloads the tab with [tabId] (forces a refresh even when already on the url). */
external fun reload(tabId: Int): Promise<dynamic>
