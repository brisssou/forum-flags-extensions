package flags.prefs

import flags.prefs.Mapper.fromRecord
import flags.prefs.Mapper.toRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefsTest {

    @Test
    fun emptyRecordYieldsDefaults() {
        assertEquals(Prefs(), Prefs.fromRecord(js("{}")))
    }

    @Test
    fun roundTripsThroughRecord() {
        val prefs = Prefs(
            refreshTime = 90,
            getMps = false,
            onlyFavs = true,
            maxOpenAll = 25,
            mutedInPopup = true,
            bgColor = "#123456",
            mutedTopics = listOf(
                MutedTopic("3", "154877", "Demos"),
                MutedTopic("5", "200553", "VR"),
            ),
        )
        assertEquals(prefs, Prefs.fromRecord(prefs.toRecord()))
    }

    @Test
    fun roundTripsThroughStructuredClone() {
        // chrome.storage clones values with the structured-clone algorithm before
        // persisting; this reproduces that to catch nested-structure loss (the
        // muted-topics array) the way a real browser store would.
        val prefs = Prefs(
            mutedInPopup = true,
            mutedTopics = listOf(
                MutedTopic("3", "154877", "Demos"),
                MutedTopic("5", "200553", "VR"),
            ),
        )
        val record = prefs.toRecord()
        val cloned = js("typeof structuredClone !== 'undefined' ? structuredClone(record) : JSON.parse(JSON.stringify(record))")
        assertEquals(prefs, Prefs.fromRecord(cloned))
    }

    @Test
    fun partialRecordDefaultsTheRest() {
        val record = js("{}")
        record.refreshTime = 120
        record.getTopics = false

        val prefs = Prefs.fromRecord(record)
        assertEquals(120, prefs.refreshTime)
        assertEquals(false, prefs.getTopics)
        // untouched keys fall back to defaults
        assertEquals(Prefs().getMps, prefs.getMps)
        assertEquals(Prefs().maxOpenAll, prefs.maxOpenAll)
        assertEquals(emptyList(), prefs.mutedTopics)
    }
}
