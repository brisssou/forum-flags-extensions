@file:JsQualifier("chrome.runtime")

package flags.chrome.runtime

import kotlin.js.Promise

/**
 * Subset of the chrome.runtime messaging API: the popup sends a message and the
 * service worker answers it (e.g. "refresh now, give me the fresh snapshot").
 * https://developer.chrome.com/docs/extensions/reference/api/runtime
 */

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
