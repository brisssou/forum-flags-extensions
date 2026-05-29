@file:JsModule("linkedom")
@file:JsNonModule

package flags.parse

/**
 * `import { parseHTML } from "linkedom"`.
 *
 * linkedom is a dependency-free DOM parser that works in a service worker
 * (which has no built-in DOMParser), exposing a standard `querySelector` API.
 */
external fun parseHTML(html: String): ParseResult
