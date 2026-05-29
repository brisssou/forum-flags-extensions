package flags.site

import flags.model.Topic

/**
 * A supported forum. Each shipping artifact bundles every implementation and
 * selects the active one at runtime via [id].
 *
 * All current sites run the same forum engine, so the urls are built here from
 * per-site [host] / [config] / [basePath]; a site only overrides them if its
 * engine ever diverges. `parseMps` arrives once a logged-in MP-page sample is
 * available.
 */
interface Site {
    /** Stable identifier used to pick the active site at startup. */
    val id: String

    /** Forum host, e.g. `forum.hardware.fr`. */
    val host: String

    /** Forum config key passed as the `config` query param, e.g. `hfr.inc`. */
    val config: String

    /** Theme color (the 1.0 `BG_COLOR` default); per-site. */
    val defaultColor: String

    /** Path prefix before the forum scripts (e.g. `/forum` for mesdiscussions). */
    val basePath: String get() = ""

    /** Absolute url of the flagged-topics ("mes drapeaux") page. */
    fun drapsUrl(): String =
        "https://$host$basePath/forum1f.php?$FRAGMENT&config=$config&owntopic=1"

    /** Absolute url of the private-messages ("messagerie", `cat=prive`) page. */
    fun mpsUrl(): String =
        "https://$host$basePath/forum1.php?$FRAGMENT&config=$config&cat=prive"

    /** Flagged topics with unread pages, parsed from the draps page html. */
    fun parseUnread(html: String): List<Topic>
}

/** Marks the request as coming from the extension (kept from 1.0). */
private const val FRAGMENT = "flags4chrome=1"
