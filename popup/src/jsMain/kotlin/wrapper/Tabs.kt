@file:JsQualifier("chrome.tabs")

package com.composeweb.chrome.wrapper

external fun query(queryInfo: QueryInfo, callback: (Array<Tab>) -> Unit)
external fun create(createProperties: Tab/*, optional function callback*/)


external interface QueryInfo {
    /** Whether the tabs are active in their windows */
    var active: Boolean?

    // other attributes
}

external interface Tab {
    /** The URL the tab is displaying */
    var active: Boolean? // true
    var index: Int?
    var openerTabId: Int?
    var pinned: Boolean?  // false
    var url: String?
    var windowId: Int?
}