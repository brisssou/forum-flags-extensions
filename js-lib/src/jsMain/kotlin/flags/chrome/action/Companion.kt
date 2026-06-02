package flags.chrome.action

object Companion {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun BadgeTextDetails(block: BadgeTextDetails.() -> Unit) =
        (js("{}") as BadgeTextDetails).apply(block)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun BadgeColorDetails(block: BadgeColorDetails.() -> Unit) =
        (js("{}") as BadgeColorDetails).apply(block)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun TitleDetails(block: TitleDetails.() -> Unit) =
        (js("{}") as TitleDetails).apply(block)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun PopupDetails(block: PopupDetails.() -> Unit) =
        (js("{}") as PopupDetails).apply(block)
}
