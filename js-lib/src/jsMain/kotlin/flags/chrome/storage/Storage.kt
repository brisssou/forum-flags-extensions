@file:JsQualifier("chrome.storage")

package flags.chrome.storage

import kotlin.js.Promise

/**
 * Subset of the chrome.storage StorageArea API.
 * https://developer.chrome.com/docs/extensions/reference/api/storage
 */
external interface StorageArea {
    /**
     * Reads one or more items. `keys` may be `null` (return everything), a
     * single key string, an array of keys, or an object whose keys are the
     * names to fetch and values are defaults to return when the key is
     * missing. Resolves to an object containing the requested entries.
     */
    fun get(keys: dynamic = definedExternally): Promise<dynamic>

    /** Stores one or more items. `items` is an object map of key→value. */
    fun set(items: dynamic): Promise<Unit>

    /** Removes one or more items. `keys` is a single key or an array. */
    fun remove(keys: dynamic): Promise<Unit>

    /** Removes every item from this area. */
    fun clear(): Promise<Unit>
}

external val local: StorageArea
external val sync: StorageArea
external val session: StorageArea

external object onChanged {

    /**
     * Fired when one or more items change in any area. `changes` is an object
     * keyed by the changed item names (each value carries `oldValue`/`newValue`);
     * `areaName` is the area that changed (e.g. `"local"`).
     */
    fun addListener(callback: (changes: dynamic, areaName: String) -> Unit)
}
