package flags.prefs

import flags.util.isAbsent
import flags.util.orBool
import flags.util.orInt
import flags.util.orString

/**
 * Maps [Prefs] to and from the plain JS records `chrome.storage` stores. Kept
 * out of the model so [Prefs] stays a pure data type; the defaults it reads
 * live on [Prefs.Companion] so the constructor and `fromRecord` share one
 * source of truth. Import the members to use them: `prefs.toRecord()` and
 * `Prefs.fromRecord(record)`.
 */
object Mapper {

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
        r.mutedTopics = mutedTopics.map { it.toRecord() }.toTypedArray()
        return r
    }

    /**
     * Reads [Prefs] from a `chrome.storage` record, defaulting any missing key.
     *
     * The untyped source is a parameter, not a receiver: a call on a `dynamic`
     * receiver is dispatched at runtime as a JS lookup, so a `dynamic` extension
     * (`fun dynamic.toPrefs()`) would never be selected. Hanging it off
     * [Prefs.Companion] keeps the factory reading as `Prefs.fromRecord(record)`
     * and lets it see the companion's `DEFAULT_*` defaults unqualified.
     */
    fun Prefs.Companion.fromRecord(record: dynamic): Prefs =
        Prefs(
            refreshTime = orInt(record.refreshTime, DEFAULT_REFRESH_TIME),
            getTopics = orBool(record.getTopics, DEFAULT_GET_TOPICS),
            getMps = orBool(record.getMps, DEFAULT_GET_MPS),
            onlyFavs = orBool(record.onlyFavs, DEFAULT_ONLY_FAVS),
            useDirectLink = orBool(record.useDirectLink, DEFAULT_USE_DIRECT_LINK),
            animatedIcon = orBool(record.animatedIcon, DEFAULT_ANIMATED_ICON),
            useContextMenu = orBool(record.useContextMenu, DEFAULT_USE_CONTEXT_MENU),
            newTab = orBool(record.newTab, DEFAULT_NEW_TAB),
            debugOn = orBool(record.debugOn, DEFAULT_DEBUG_ON),
            openCat = orBool(record.openCat, DEFAULT_OPEN_CAT),
            showCat = orBool(record.showCat, DEFAULT_SHOW_CAT),
            maxOpenAll = orInt(record.maxOpenAll, DEFAULT_MAX_OPEN_ALL),
            bgColor = orString(record.bgColor, DEFAULT_BG_COLOR),
            mutedTopics = mutedTopicsFromRecord(record.mutedTopics),
        )

    private fun MutedTopic.toRecord(): dynamic {
        val r = js("{}")
        r.categoryId = categoryId
        r.topicId = topicId
        r.title = title
        return r
    }

    private fun mutedTopicsFromRecord(v: dynamic): List<MutedTopic> =
        if (isAbsent(v)) emptyList()
        else v.unsafeCast<Array<dynamic>>().map { m ->
            MutedTopic(
                categoryId = m.categoryId.unsafeCast<String>(),
                topicId = m.topicId.unsafeCast<String>(),
                title = m.title.unsafeCast<String>(),
            )
        }
}
