package flags.snapshot

import flags.model.Topic

/**
 * The last forum poll, cached by the worker for the popup to render instantly.
 * Holds everything the popup shows: login state, the flagged [topics], the new
 * private-message count, the [categories] id→name map used to group topics, and
 * when it was fetched. Persisted under a single `chrome.storage` key by
 * [toRecord] / [fromRecord].
 *
 * @property fetchedAt epoch millis of the poll, or 0 before the first one.
 */
data class Snapshot(
    val loggedIn: Boolean = DEFAULT_LOGGED_IN,
    val topics: List<Topic> = emptyList(),
    val mps: Int = DEFAULT_MPS,
    val categories: Map<String, String> = emptyMap(),
    val fetchedAt: Double = DEFAULT_FETCHED_AT,
) {
    /** Defaults, shared by the constructor above and [SnapshotMapper.fromRecord]. */
    companion object {
        internal const val DEFAULT_LOGGED_IN = true
        internal const val DEFAULT_MPS = 0
        internal const val DEFAULT_FETCHED_AT = 0.0
    }
}
