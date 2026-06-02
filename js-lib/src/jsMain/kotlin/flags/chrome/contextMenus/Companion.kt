package flags.chrome.contextMenus

object Companion {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun CreateProperties(block: CreateProperties.() -> Unit) =
        (js("{}") as CreateProperties).apply(block)
}
