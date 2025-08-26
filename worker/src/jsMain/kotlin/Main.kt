package com.composeweb.chrome

import com.composeweb.chrome.alarm.AlarmCreateInfo
import com.composeweb.chrome.alarm.create
import com.composeweb.chrome.alarm.onAlarm
import kotlin.js.Date

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline fun AlarmCreateInfo(block: AlarmCreateInfo.() -> Unit) = (js("{}") as AlarmCreateInfo).apply(block)

fun main() {
    println("starting worker")
    val create = create(
        "test",
        AlarmCreateInfo {
            periodInMinutes = 1
            delayInMinutes = 1
        }
    )

    create.then { println("Alarm set") }
    create.catch { println(it) }
    create.finally { println("Done with alarm") }

    onAlarm.addListener { println("${it.name} ${Date.now()}") }
//    js("""chrome.alarms.onAlarm.addListener((alarm) => {
//  appendToLog('alarms.onAlarm -- name: ${'$'}{alarm.name}, scheduledTime: ${'$'}{alarm.scheduledTime}');
//})""")
    println("Listener added")
}