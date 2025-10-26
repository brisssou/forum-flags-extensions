package flags.chrome.alarm

object Companion {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    inline fun AlarmCreateInfo(block: AlarmCreateInfo.() -> Unit) = (js("{}") as AlarmCreateInfo).apply(block)

}