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
}
