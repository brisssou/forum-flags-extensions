@file:JsQualifier("chrome.action")

package flags.chrome.action

import kotlin.js.Promise

/**
 * Subset of the chrome.action API used to drive the toolbar button badge.
 * https://developer.chrome.com/docs/extensions/reference/api/action
 */

external interface BadgeTextDetails {
    /** Any number of characters; only ~4 fit. An empty string clears the badge. */
    var text: String?
}

external interface BadgeColorDetails {
    /** A CSS color string (e.g. "#FF0000") or an [r, g, b, a] array. */
    var color: dynamic
}

external interface TitleDetails {
    /** The tooltip shown on hover. */
    var title: String
}

external fun setBadgeText(details: BadgeTextDetails): Promise<Unit>

external fun setBadgeBackgroundColor(details: BadgeColorDetails): Promise<Unit>

external fun setTitle(details: TitleDetails): Promise<Unit>
