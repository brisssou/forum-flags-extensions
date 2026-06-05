package flags.prefs

import flags.chrome.storage.StorageArea
import flags.chrome.storage.local
import flags.prefs.Mapper.fromRecord
import flags.prefs.Mapper.toRecord
import kotlin.js.Promise

/**
 * Loads and saves [Prefs] in a `chrome.storage` area. Defaults to
 * `chrome.storage.local` (universal cross-browser, no per-browser config); the
 * area is injectable so it can be swapped for `sync` or faked in tests.
 */
class PrefsStore(private val area: StorageArea = local) {

    fun load(): Promise<Prefs> =
        area.get().then<Prefs> { Prefs.fromRecord(it) }

    fun save(prefs: Prefs): Promise<Unit> =
        area.set(prefs.toRecord())

    /**
     * Loads the latest stored prefs, applies [transform], saves the result, and
     * resolves to it. Saves merge onto current storage rather than a possibly
     * stale in-memory snapshot — without this, a surface saving an out-of-date
     * copy (e.g. the options page before its load resolved, or while the popup
     * just added a mute) would overwrite fields it never touched, such as
     * `mutedTopics`.
     */
    fun update(transform: (Prefs) -> Prefs): Promise<Prefs> =
        load().then { current ->
            val next = transform(current)
            save(next).then { next }
        }.unsafeCast<Promise<Prefs>>()
}
