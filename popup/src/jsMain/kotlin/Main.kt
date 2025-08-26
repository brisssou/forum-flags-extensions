package com.composeweb.chrome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import com.composeweb.chrome.wrapper.QueryInfo
import com.composeweb.chrome.wrapper.Tab
import com.composeweb.chrome.wrapper.create


@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline fun QueryInfo(block: QueryInfo.() -> Unit) = (js("{}") as QueryInfo).apply(block)

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
inline fun Tab(block: Tab.() -> Unit) = (js("{}") as Tab).apply(block)

fun main() {
    var platform by mutableStateOf("Compose Web!")
    var bgColor by mutableStateOf(Color("#003244"))
    var tab: Tab? = null

    renderComposable(rootElementId = "root") {
        // com.composeweb.chrome.main div
        Div({
            style {
                padding(25.px)
                backgroundColor(bgColor)
            }
        }) {


            H1({
                style {
                    color(Color("white")) // White text
                }
            }) {
                Text("Hello $platform")
            }

            Button(attrs = {
                // Click listener
                onClick {
                    platform = "Chrome Extension!" // Changing text
                    bgColor = Color("#00F488") // Changing background color to green
                    create(Tab { url = "https://perdu.com" })
                }
            }) {
                Text("Click Me!")
            }
        }
    }
}
