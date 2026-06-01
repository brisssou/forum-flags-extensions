package flags.util

/**
 * Helpers for reading values out of the plain JS objects that `chrome.storage`
 * hands back, defaulting any key that is missing (`undefined`) or `null`.
 * Shared by the [flags.prefs.Prefs] and [flags.snapshot.Snapshot] codecs.
 */

fun isAbsent(v: dynamic): Boolean = v == undefined || v == null

fun orInt(v: dynamic, default: Int): Int =
    if (isAbsent(v)) default else v.unsafeCast<Double>().toInt()

fun orDouble(v: dynamic, default: Double): Double =
    if (isAbsent(v)) default else v.unsafeCast<Double>()

fun orBool(v: dynamic, default: Boolean): Boolean =
    if (isAbsent(v)) default else v.unsafeCast<Boolean>()

fun orString(v: dynamic, default: String?): String? =
    if (isAbsent(v)) default else v.unsafeCast<String>()

fun orStringList(v: dynamic): List<String> =
    if (isAbsent(v)) emptyList() else v.unsafeCast<Array<String>>().toList()
