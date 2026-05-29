package flags.site

/**
 * A supported forum. Each shipping artifact bundles every implementation and
 * selects the active one at runtime via [id].
 *
 * The identity and url surface is settled here so it stays stable as parsing
 * lands incrementally on top:
 *  - `parseUnread` arrives with the draps-parser milestone,
 *  - `parseMps` arrives once a logged-in MP-page sample is available
 *    ([mpsUrl] is declared now so the shape doesn't churn).
 */
interface Site {
    /** Stable identifier used to pick the active site at startup. */
    val id: String

    /** Forum host, e.g. `forum.hardware.fr`. */
    val host: String

    /** Theme color (the 1.0 `BG_COLOR` default); per-site. */
    val defaultColor: String

    /** Absolute url of the flagged-topics ("mes drapeaux") page. */
    fun drapsUrl(): String

    /** Absolute url of the private-messages ("messagerie", `cat=prive`) page. */
    fun mpsUrl(): String
}
