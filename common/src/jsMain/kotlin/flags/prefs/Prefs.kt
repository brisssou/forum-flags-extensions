package flags.prefs

/**
 * User preferences, shared by the worker, popup and options page. Persisted as
 * a flat record under camelCase keys by [toRecord] / [fromRecord].
 */
data class Prefs(
    val refreshTime: Int = DEFAULT_REFRESH_TIME,
    val getTopics: Boolean = DEFAULT_GET_TOPICS,
    val getMps: Boolean = DEFAULT_GET_MPS,
    val onlyFavs: Boolean = DEFAULT_ONLY_FAVS,
    val useDirectLink: Boolean = DEFAULT_USE_DIRECT_LINK,
    val animatedIcon: Boolean = DEFAULT_ANIMATED_ICON,
    val useContextMenu: Boolean = DEFAULT_USE_CONTEXT_MENU,
    val newTab: Boolean = DEFAULT_NEW_TAB,
    val debugOn: Boolean = DEFAULT_DEBUG_ON,
    val openCat: Boolean = DEFAULT_OPEN_CAT,
    val showCat: Boolean = DEFAULT_SHOW_CAT,
    val maxOpenAll: Int = DEFAULT_MAX_OPEN_ALL,
    /** Theme color; null means "use the active site's default". */
    val bgColor: String? = DEFAULT_BG_COLOR,
    /** Topics the user muted in the popup; hidden from the list. */
    val mutedTopics: List<MutedTopic> = emptyList(),
) {
    /** Defaults, shared by the constructor above and [PrefsMapper.fromRecord]. */
    companion object {
        internal const val DEFAULT_REFRESH_TIME = 500
        internal const val DEFAULT_GET_TOPICS = true
        internal const val DEFAULT_GET_MPS = true
        internal const val DEFAULT_ONLY_FAVS = false
        internal const val DEFAULT_USE_DIRECT_LINK = false
        internal const val DEFAULT_ANIMATED_ICON = true
        internal const val DEFAULT_USE_CONTEXT_MENU = false
        internal const val DEFAULT_NEW_TAB = true
        internal const val DEFAULT_DEBUG_ON = false
        internal const val DEFAULT_OPEN_CAT = true
        internal const val DEFAULT_SHOW_CAT = true
        internal const val DEFAULT_MAX_OPEN_ALL = 10

        /** `null` can't be a `const`, so the nullable default is a plain `val`. */
        internal val DEFAULT_BG_COLOR: String? = null
    }
}
