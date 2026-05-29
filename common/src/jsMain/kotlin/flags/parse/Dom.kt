package flags.parse

/** Result of [parseHTML]; only the document is needed. */
external interface ParseResult {
    val document: DomElement
}

/**
 * The slice of the DOM Element API used by the parsers. linkedom's
 * `querySelectorAll` returns an Array subclass, so it maps directly to
 * `Array<DomElement>`.
 */
external interface DomElement {
    fun querySelector(selectors: String): DomElement?
    fun querySelectorAll(selectors: String): Array<DomElement>
    fun getAttribute(name: String): String?
    val textContent: String?
}
