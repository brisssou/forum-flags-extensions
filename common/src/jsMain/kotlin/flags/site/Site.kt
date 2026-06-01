package flags.site

import flags.model.Topic

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
}
