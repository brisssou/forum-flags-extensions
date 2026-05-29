package flags.prefs

import kotlin.test.Test
import kotlin.test.assertEquals

class PrefsTest {

    @Test
    fun emptyRecordYieldsDefaults() {
        assertEquals(Prefs(), prefsFromRecord(js("{}")))
    }

    @Test
    fun roundTripsThroughRecord() {
        val prefs = Prefs(
            refreshTime = 90,
            getMps = false,
            onlyFavs = true,
            maxOpenAll = 25,
            bgColor = "#123456",
            mutedTopics = listOf("3#154877#Demos", "5#200553#VR"),
        )
        assertEquals(prefs, prefsFromRecord(prefs.toRecord()))
    }

    @Test
    fun partialRecordDefaultsTheRest() {
        val record = js("{}")
        record.refreshTime = 120
        record.getTopics = false

        val prefs = prefsFromRecord(record)
        assertEquals(120, prefs.refreshTime)
        assertEquals(false, prefs.getTopics)
        // untouched keys fall back to defaults
        assertEquals(Prefs().getMps, prefs.getMps)
        assertEquals(Prefs().maxOpenAll, prefs.maxOpenAll)
        assertEquals(emptyList(), prefs.mutedTopics)
    }
}
