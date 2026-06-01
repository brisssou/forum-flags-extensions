@file:JsQualifier("chrome.runtime")

package flags.chrome.runtime

import kotlin.js.Promise

/**
 * Subset of the chrome.runtime API: the popup sends a message and the service
 * worker answers it (e.g. "refresh now, give me the fresh snapshot"), plus
 * opening the options page from the popup.
 * https://developer.chrome.com/docs/extensions/reference/api/runtime
 */

/** Opens the extension's options page (as configured by `options_ui`). */
external fun openOptionsPage(): Promise<Unit>

external interface MessageSender {
    /** Extension id of the sender (same extension, for our own messages). */
    var id: String?
}

/** Sends a single message to the extension's listeners; resolves with their response. */
external fun sendMessage(message: dynamic): Promise<dynamic>

external object onMessage {

    /**
     * Fired when a message is received. Return `true` to keep the channel open
     * and answer asynchronously via [sendResponse]; return `false` if no
     * response will be sent.
     */
    fun addListener(
        callback: (message: dynamic, sender: MessageSender, sendResponse: (dynamic) -> Unit) -> Boolean,
    )
}
