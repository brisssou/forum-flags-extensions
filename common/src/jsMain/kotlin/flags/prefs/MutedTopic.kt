package flags.prefs

/**
 * A topic the user has muted, persisted in [Prefs.mutedTopics] and hidden from
 * the popup. Identity is [categoryId] + [topicId]; [title] is kept only to
 * label the entry in the options unmute list.
 */
data class MutedTopic(
    val categoryId: String,
    val topicId: String,
    val title: String,
)
