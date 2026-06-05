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

    @Test
    fun unmuteRemovesTheEntry() {
        val muted = Prefs().mute(topic)
        val restored = muted.unmute(MutedTopic("5", "200553", "[TU] VR autonome"))
        assertEquals(emptyList(), restored.mutedTopics)
        assertFalse(restored.isMuted(topic))
    }

    @Test
    fun unmuteMatchesOnIdentityIgnoringStoredTitle() {
        val muted = Prefs(mutedTopics = listOf(MutedTopic("5", "200553", "[TU] VR autonome")))
        val restored = muted.unmute(MutedTopic("5", "200553", "stale title"))
        assertEquals(emptyList(), restored.mutedTopics)
    }

    @Test
    fun unmuteLeavesOtherMutedTopicsAlone() {
        val keep = MutedTopic("9", "111", "elsewhere")
        val muted = Prefs(mutedTopics = listOf(MutedTopic("5", "200553", "[TU] VR autonome"), keep))
        val restored = muted.unmute(MutedTopic("5", "200553", "[TU] VR autonome"))
        assertEquals(listOf(keep), restored.mutedTopics)
    }

    @Test
    fun unmuteIsANoOpWhenNotMuted() {
        val prefs = Prefs(mutedTopics = listOf(MutedTopic("9", "111", "elsewhere")))
        assertEquals(prefs.mutedTopics, prefs.unmute(MutedTopic("5", "200553", "absent")).mutedTopics)
    }
}
