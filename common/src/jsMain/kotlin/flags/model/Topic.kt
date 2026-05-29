package flags.model

/**
 * A flagged ("drapalisé") forum topic with unread pages.
 *
 * @property topicId the topic id (the `post` url param — HFR's name for the thread id)
 * @property title the topic title, entity-decoded
 * @property categoryId the forum category id (the `cat` url param)
 * @property href the url to the last-read message on this topic
 * @property nbUnread number of unread pages = totalPages - lastReadPage, never negative
 */
data class Topic(
    val topicId: String,
    val title: String,
    val categoryId: String,
    val href: String,
    val nbUnread: Int,
)
