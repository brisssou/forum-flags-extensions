package flags.snapshot

import flags.model.Topic
import flags.snapshot.Mapper.fromRecord
import flags.snapshot.Mapper.toRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class SnapshotTest {

    @Test
    fun absentRecordYieldsDefaults() {
        assertEquals(Snapshot(), Snapshot.fromRecord(undefined))
        assertEquals(Snapshot(), Snapshot.fromRecord(js("{}")))
    }

    @Test
    fun roundTripsThroughRecord() {
        val snapshot = Snapshot(
            loggedIn = true,
            topics = listOf(
                Topic("154877", "Démos audio", "3", "/forum2.php?post=154877", 0),
                Topic("200553", "[TU] VR autonome", "5", "/forum2.php?post=200553", 132),
            ),
            mps = 2,
            categories = mapOf("3" to "Video & Son", "5" to "Jeux Video"),
            fetchedAt = 1748772360000.0,
        )
        assertEquals(snapshot, Snapshot.fromRecord(snapshot.toRecord()))
    }

    @Test
    fun partialRecordDefaultsTheRest() {
        val record = js("{}")
        record.loggedIn = false
        record.mps = 1

        val snapshot = Snapshot.fromRecord(record)
        assertEquals(false, snapshot.loggedIn)
        assertEquals(1, snapshot.mps)
        // untouched keys fall back to defaults
        assertEquals(emptyList(), snapshot.topics)
        assertEquals(emptyMap(), snapshot.categories)
        assertEquals(0.0, snapshot.fetchedAt)
    }
}
