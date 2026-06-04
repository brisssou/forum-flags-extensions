@file:JsQualifier("browser.i18n")

package flags.chrome.i18n

/**
 * Subset of the chrome.i18n API used to localize UI strings from `_locales`.
 * https://developer.chrome.com/docs/extensions/reference/api/i18n
 */

/** The message for [messageName] in the current locale, or "" if missing. */
external fun getMessage(messageName: String): String

/**
 * The message for [messageName], with positional `$1`..`$9` placeholders
 * filled from [substitutions] (max 9).
 */
external fun getMessage(messageName: String, substitutions: Array<String>): String
