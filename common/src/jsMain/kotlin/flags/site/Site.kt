package flags.site

import flags.model.Topic
import flags.snapshot.Snapshot

/** The `post=` param (HFR's thread id) in a forum url; shared by all sites' engine. */
private val TOPIC_POST = Regex("[?&]post=(\\d+)")

/**
 * A supported forum. Each shipping artifact bundles every implementation and
 * selects the active one at runtime via [id].
 *
 * All current sites run the same forum engine, so the urls and the
 * logged-out check are built/shared here from per-site [host] / [config] /
 * [basePath]; a site only overrides them if its engine ever diverges.
 */
interface Site {
    /** Stable identifier used to pick the active site at startup. */
    val id: String

    /** Human-readable site name, e.g. `HFR` (shown in the options page title). */
    val displayName: String

    /** Forum host, e.g. `forum.hardware.fr`. */
    val host: String

    /** Forum config key passed as the `config` query param, e.g. `hfr.inc`. */
    val config: String

    /** Theme color (the 1.0 `BG_COLOR` default); per-site. */
    val defaultColor: String

    /** Smallest allowed poll interval in seconds (1.0 used 120 for all sites). */
    val minRefreshTimeSeconds: Int get() = 120

    /** Path prefix before the forum scripts (e.g. `/forum` for mesdiscussions). */
    val basePath: String get() = ""

    /** Query fragment marking the request as coming from the extension (kept from 1.0). */
    val fragment: String get() = "flags4chrome=1"

    /**
     * Message the forum shows in place of the topic list when the session is
     * invalid. Identical across all current sites (same engine), so it defaults
     * here; a site on a different engine can override it.
     */
    val notLoggedInMarker: String get() = "Aucun sujet que vous avez lu n'est connu"

    /** Absolute url of the flagged-topics ("mes drapeaux") page. */
    fun drapsUrl(): String =
        "https://$host$basePath/forum1f.php?$fragment&config=$config&owntopic=1"

    /** Absolute url of the favourites-only page (polled when `onlyFavs` is on). */
    fun favsUrl(): String =
        "https://$host$basePath/forum1f.php?$fragment&config=$config&owntopic=3"

    /** Absolute url of the private-messages page (linked from the popup MP line). */
    fun mpsUrl(): String =
        "https://$host$basePath/forum1.php?config=$config&cat=prive&page=1&owntopic=0"

    /** Absolute url of a single category's flagged topics (popup category link). */
    fun ownCatUrl(categoryId: String): String =
        "https://$host$basePath/forum1.php?config=$config&owntopic=1&cat=$categoryId"

    /**
     * The topic id (`post=` param) carried by a forum [url], or null when it is
     * not a topic url. Lets a tab on the same topic be recognised for reuse
     * regardless of which page/post the url targets.
     */
    fun topicId(url: String): String? = TOPIC_POST.find(url)?.groupValues?.get(1)

    /** Flagged topics with unread pages, parsed from the draps page html. */
    fun parseUnread(html: String): List<Topic>

    /**
     * Category id → display name, parsed from the draps page section headers.
     * Used by the popup to group [parseUnread]'s topics under their category.
     */
    fun parseCategories(html: String): Map<String, String>

    /** Count of new private messages, parsed from the draps page header (0 if none). */
    fun parseMps(html: String): Int

    /** True when [html] is the "not logged in" page rather than real content. */
    fun isNotLoggedIn(html: String): Boolean = html.contains(notLoggedInMarker)

    /**
     * Composes the per-site parsers into the cached [Snapshot] for the draps
     * page (which carries the topics, categories and new-MP notice together).
     * Logged out → an empty, `loggedIn = false` snapshot. [fetchedAt] (epoch
     * millis) is supplied by the caller's clock so this stays pure and testable.
     */
    fun snapshot(html: String, fetchedAt: Double): Snapshot =
        if (isNotLoggedIn(html)) {
            Snapshot(loggedIn = false, fetchedAt = fetchedAt)
        } else {
            Snapshot(
                loggedIn = true,
                topics = parseUnread(html),
                mps = parseMps(html),
                categories = parseCategories(html),
                fetchedAt = fetchedAt,
            )
        }
}
