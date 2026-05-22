package flags

import flags.chrome.alarm.Alarm
import flags.chrome.alarm.Companion.AlarmCreateInfo
import flags.chrome.alarm.create
import flags.chrome.alarm.get
import flags.chrome.alarm.onAlarm
import kotlin.js.Date
import kotlin.js.Promise


private const val ALARM = "forum-flags-alarm"

fun createAlarm() {
    get(ALARM).then {
        if (it == null) {
            create(
                ALARM,
                AlarmCreateInfo {
                    periodInMinutes = 1.0
                    delayInMinutes = 1.0
                }
            ).then { console.info("Alarm set") }
        } else {
            console.info("Alarm $ALARM already set")
        }
    }
}

fun alarmListener(alarm: Alarm): Promise<Nothing?> {
    console.info("${alarm.name} ${Date.now()}")
    return Promise.resolve<Nothing?>(null)
}

fun main() {
    console.info("starting worker")
    createAlarm()

    onAlarm.addListener { alarmListener(it) }
    console.info("Listener added")
}