@file:JsQualifier("browser.alarms")

package flags.chrome.alarm

import kotlin.js.Promise

external interface AlarmCreateInfo {
    /**
     *
     * Length of time in minutes after which the onAlarm event should fire.
     */
    var delayInMinutes: Double?

    /**
     *
     * If set, the onAlarm event should fire every periodInMinutes minutes after the initial event specified by when or delayInMinutes. If not set, the alarm will only fire once.
     * when
     */
    var periodInMinutes: Double?

    /**
     * Time at which the alarm should fire, in milliseconds past the epoch (e.g. Date.now() + n).
     *
     */
    var `when`: Double?
}

external interface Alarm {
    /**
     * Name of this alarm.
     */
    var name: String

    /**
     * If not null, the alarm is a repeating alarm and will fire again in periodInMinutes minutes.
     */
    var periodInMinutes: Double?

    /**
     * Time at which this alarm was scheduled to fire, in milliseconds past the epoch (e.g. Date.now() + n). For performance reasons, the alarm may have been delayed an arbitrary amount beyond this.
     */
    var scheduledTime: Double
}

/**
 * Creates an alarm. Near the time(s) specified by alarmInfo, the onAlarm event is fired. If there is another alarm with the same name (or no name if none is specified), it will be cancelled and replaced by this alarm.
 *
 * In order to reduce the load on the user's machine, Chrome limits alarms to at most once every 30 seconds but may delay them an arbitrary amount more. That is, setting delayInMinutes or periodInMinutes to less than 0.5 will not be honored and will cause a warning. when can be set to less than 30 seconds after "now" without warning but won't actually cause the alarm to fire for at least 30 seconds.
 *
 * To help you debug your app or extension, when you've loaded it unpacked, there's no limit to how often the alarm can fire.
 */
external fun create(
    name: String?,
    alarmInfo: AlarmCreateInfo,
): Promise<Boolean>

/**
 * Clears the alarm with the given name.
 */
external fun clear(
    name: String?,
): Promise<Boolean>

/**
 * Retrieves details about the specified alarm.
 */
external fun get(
    name: String?,
): Promise<Alarm?>

external object onAlarm {

    /**
     * Fired when an alarm has elapsed. Useful for event pages.
     */
    fun addListener(callBack: (Alarm) -> Unit)
}
