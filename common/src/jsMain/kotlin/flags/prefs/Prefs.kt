package flags.prefs

/**
 * User preferences, ported from 1.0's `PREFS_DEFAULT`. Three 1.0 checkboxes
 * (OPEN_TO_FRONT, MODO, AVATAR) are dropped: they had no default and no
 * behavior ever read them, so they were inert. Defaults match 1.0. Persisted
 * as a flat record under clean camelCase keys via [toRecord] /
 * [prefsFromRecord]; the worker, popup and options page all share this type.
 */
data class Prefs(
    val refreshTime: Int = 500,
    val getTopics: Boolean = true,
    val getMps: Boolean = true,
    val onlyFavs: Boolean = false,
    val useDirectLink: Boolean = false,
    val animatedIcon: Boolean = true,
    val useContextMenu: Boolean = false,
    val newTab: Boolean = true,
    val debugOn: Boolean = false,
    val openCat: Boolean = true,
    val showCat: Boolean = true,
    val maxOpenAll: Int = 10,
    /** Theme color; null means "use the active site's default". */
    val bgColor: String? = null,
    /** Muted topics as `cat#post#title` entries. */
    val mutedTopics: List<String> = emptyList(),
)

/** Serializes to a plain JS object for `chrome.storage`. */
fun Prefs.toRecord(): dynamic {
    val r = js("{}")
    r.refreshTime = refreshTime
    r.getTopics = getTopics
    r.getMps = getMps
    r.onlyFavs = onlyFavs
    r.useDirectLink = useDirectLink
    r.animatedIcon = animatedIcon
    r.useContextMenu = useContextMenu
    r.newTab = newTab
    r.debugOn = debugOn
    r.openCat = openCat
    r.showCat = showCat
    r.maxOpenAll = maxOpenAll
    r.bgColor = bgColor
    r.mutedTopics = mutedTopics.toTypedArray()
    return r
}

/** Reads [Prefs] from a `chrome.storage` record, defaulting any missing key. */
fun prefsFromRecord(record: dynamic): Prefs {
    val d = Prefs()
    return Prefs(
        refreshTime = orInt(record.refreshTime, d.refreshTime),
        getTopics = orBool(record.getTopics, d.getTopics),
        getMps = orBool(record.getMps, d.getMps),
        onlyFavs = orBool(record.onlyFavs, d.onlyFavs),
        useDirectLink = orBool(record.useDirectLink, d.useDirectLink),
        animatedIcon = orBool(record.animatedIcon, d.animatedIcon),
        useContextMenu = orBool(record.useContextMenu, d.useContextMenu),
        newTab = orBool(record.newTab, d.newTab),
        debugOn = orBool(record.debugOn, d.debugOn),
        openCat = orBool(record.openCat, d.openCat),
        showCat = orBool(record.showCat, d.showCat),
        maxOpenAll = orInt(record.maxOpenAll, d.maxOpenAll),
        bgColor = orString(record.bgColor, d.bgColor),
        mutedTopics = orStringList(record.mutedTopics),
    )
}

private fun isAbsent(v: dynamic): Boolean = v == undefined || v == null

private fun orInt(v: dynamic, default: Int): Int =
    if (isAbsent(v)) default else v.unsafeCast<Double>().toInt()

private fun orBool(v: dynamic, default: Boolean): Boolean =
    if (isAbsent(v)) default else v.unsafeCast<Boolean>()

private fun orString(v: dynamic, default: String?): String? =
    if (isAbsent(v)) default else v.unsafeCast<String>()

private fun orStringList(v: dynamic): List<String> =
    if (isAbsent(v)) emptyList() else v.unsafeCast<Array<String>>().toList()
