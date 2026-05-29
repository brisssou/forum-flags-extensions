package flags.net

import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Promise

/** The global `fetch`; in a service worker it lives on the global scope, not `window`. */
private external fun fetch(input: String, init: RequestInit): Promise<Response>

/**
 * GETs [url] with the user's session cookie and resolves to the response body
 * as text. `credentials = "include"` is required for the forum session cookie
 * to be sent — without it the forum responds as if logged out.
 */
fun fetchPage(url: String): Promise<String> =
    fetch(url, RequestInit(credentials = RequestCredentials.INCLUDE))
        .then(onFulfilled = { response: Response -> response.text() })
        .unsafeCast<Promise<String>>()
