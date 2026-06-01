package flags.snapshot

import flags.chrome.storage.StorageArea
import flags.chrome.storage.local
import flags.snapshot.Mapper.fromRecord
import flags.snapshot.Mapper.toRecord
import kotlin.js.Promise

/**
 * Loads and saves the [Snapshot] in a `chrome.storage` area under a single
 * [KEY] (kept apart from the flat [flags.prefs.Prefs] keys in the same area).
 * The worker writes it each poll; the popup reads it on open. The area is
 * injectable so it can be swapped or faked in tests.
 */
class SnapshotStore(private val area: StorageArea = local) {

    fun load(): Promise<Snapshot> =
        area.get(KEY).then<Snapshot> { Snapshot.fromRecord(it[KEY]) }

    fun save(snapshot: Snapshot): Promise<Unit> {
        val wrapper = js("{}")
        wrapper[KEY] = snapshot.toRecord()
        return area.set(wrapper)
    }

    private companion object {
        const val KEY = "snapshot"
    }
}
