package flags.snapshot

import flags.model.Topic
import flags.prefs.MutedTopic
import flags.prefs.Prefs
import kotlin.test.Test
import kotlin.test.assertEquals

class ForPrefsTest {

    private val snapshot = Snapshot(
        loggedIn = true,
        topics = listOf(
            Topic("100", "A", "5", "/a", 1),
            Topic("200", "B", "5", "/b", 2),
        ),
        mps = 3,
        categories = mapOf("5" to "Jeux Video"),
        fetchedAt = 1.0,
    )

    @Test
    fun defaultPrefsKeepEverything() {
        assertEquals(snapshot, snapshot.forPrefs(Prefs()))
    }

    @Test
    fun getTopicsOffDropsTopicsAndCategories() {
        val shaped = snapshot.forPrefs(Prefs(getTopics = false))
        assertEquals(emptyList(), shaped.topics)
        assertEquals(emptyMap(), shaped.categories)
        assertEquals(3, shaped.mps, "MP count is untouched by getTopics")
    }

    @Test
    fun getMpsOffZeroesTheMpCount() {
        val shaped = snapshot.forPrefs(Prefs(getMps = false))
        assertEquals(0, shaped.mps)
        assertEquals(2, shaped.topics.size, "topics are untouched by getMps")
        assertEquals(mapOf("5" to "Jeux Video"), shaped.categories)
    }

    @Test
    fun mutedTopicsAreRemoved() {
        val shaped = snapshot.forPrefs(Prefs(mutedTopics = listOf(MutedTopic("5", "100", "A"))))
        assertEquals(listOf("200"), shaped.topics.map { it.topicId })
        assertEquals(3, shaped.mps)
        assertEquals(mapOf("5" to "Jeux Video"), shaped.categories)
    }
}
