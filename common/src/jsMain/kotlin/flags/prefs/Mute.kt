package flags.prefs

import flags.model.Topic

/**
 * Muting hides a topic from the popup. A muted topic is identified by its
 * category + topic id; the stored [MutedTopic.title] is display-only.
 */

/** True when [topic] is muted (matched on category + topic id). */
fun Prefs.isMuted(topic: Topic): Boolean =
    mutedTopics.any { it.categoryId == topic.categoryId && it.topicId == topic.topicId }

/** Returns a copy with [topic] muted; idempotent if it is already muted. */
fun Prefs.mute(topic: Topic): Prefs =
    if (isMuted(topic)) this
    else copy(mutedTopics = mutedTopics + MutedTopic(topic.categoryId, topic.topicId, topic.title))

/** Returns a copy with [topic] unmuted (matched on category + topic id); a no-op if it was not muted. */
fun Prefs.unmute(topic: MutedTopic): Prefs =
    copy(mutedTopics = mutedTopics.filterNot { it.categoryId == topic.categoryId && it.topicId == topic.topicId })
