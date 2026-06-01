package flags.prefs

import flags.model.Topic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MuteTest {

    private val topic = Topic(
        topicId = "200553",
        title = "[TU] VR autonome",
        categoryId = "5",
        href = "/forum2.php?post=200553",
        nbUnread = 132,
    )

    @Test
    fun notMutedByDefault() {
        assertFalse(Prefs().isMuted(topic))
    }

    @Test
    fun muteAddsAStructuredEntry() {
        val muted = Prefs().mute(topic)
        assertEquals(listOf(MutedTopic("5", "200553", "[TU] VR autonome")), muted.mutedTopics)
        assertTrue(muted.isMuted(topic))
    }

    @Test
    fun muteIsIdempotent() {
        val once = Prefs().mute(topic)
        assertEquals(once.mutedTopics, once.mute(topic).mutedTopics)
    }

    @Test
    fun isMutedMatchesOnIdentityIgnoringStoredTitle() {
        val prefs = Prefs(mutedTopics = listOf(MutedTopic("5", "200553", "stale title")))
        assertTrue(prefs.isMuted(topic))
    }

    @Test
    fun sameTopicIdInAnotherCategoryIsNotMuted() {
        val prefs = Prefs(mutedTopics = listOf(MutedTopic("9", "200553", "elsewhere")))
        assertFalse(prefs.isMuted(topic))
    }
}
