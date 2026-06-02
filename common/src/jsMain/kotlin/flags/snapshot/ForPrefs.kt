package flags.snapshot

import flags.prefs.Prefs
import flags.prefs.isMuted

/**
 * The snapshot as the user's prefs want it shown: topics (and their categories)
 * dropped when `getTopics` is off, the MP count zeroed when `getMps` is off, and
 * muted topics removed. The worker applies this before updating the badge and
 * caching, so the badge and the popup always agree.
 */
fun Snapshot.forPrefs(prefs: Prefs): Snapshot = copy(
    topics = if (prefs.getTopics) topics.filterNot { prefs.isMuted(it) } else emptyList(),
    categories = if (prefs.getTopics) categories else emptyMap(),
    mps = if (prefs.getMps) mps else 0,
)
