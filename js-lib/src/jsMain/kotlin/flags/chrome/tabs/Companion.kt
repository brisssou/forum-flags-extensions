package flags.chrome.tabs

object Companion {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun CreateProperties(block: CreateProperties.() -> Unit) =
        (js("{}") as CreateProperties).apply(block)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun UpdateProperties(block: UpdateProperties.() -> Unit) =
        (js("{}") as UpdateProperties).apply(block)

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun QueryInfo(block: QueryInfo.() -> Unit) =
        (js("{}") as QueryInfo).apply(block)
}
