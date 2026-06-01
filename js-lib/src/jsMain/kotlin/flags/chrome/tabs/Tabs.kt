@file:JsQualifier("chrome.tabs")

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

/** Opens a new tab. */
external fun create(createProperties: CreateProperties): Promise<dynamic>

/** Navigates the current (or given) tab; with no tab id, updates the active tab. */
external fun update(updateProperties: UpdateProperties): Promise<dynamic>
