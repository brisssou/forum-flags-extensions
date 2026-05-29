package flags.site

/**
 * hardware.fr forum.
 *
 * The 1.0 extension built urls through a `Site.applyXtor` helper that
 * prepended a `flags4chrome=1` fragment to the query string; that indirection
 * is collapsed here into simple url templates.
 */
object Hfr : Site {
    override val id = "hfr"
    override val host = "forum.hardware.fr"
    override val defaultColor = "#2F3740"

    private const val CONFIG = "hfr.inc"

    /** Marks the request as coming from the extension (kept from 1.0). */
    private const val FRAGMENT = "flags4chrome=1"

    /** Flagged topics live on forum1f.php; private messages on forum1.php. */
    override fun drapsUrl() =
        "https://$host/forum1f.php?$FRAGMENT&config=$CONFIG&owntopic=1"

    override fun mpsUrl() =
        "https://$host/forum1.php?$FRAGMENT&config=$CONFIG&cat=prive"
}
