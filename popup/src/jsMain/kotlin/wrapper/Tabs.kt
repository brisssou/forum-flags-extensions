@file:JsQualifier("chrome.tabs")

package com.composeweb.chrome.wrapper

import kotlin.js.Promise

external fun query(queryInfo: QueryInfo): Promise<Tab>
external fun create(createProperties: Tab): Promise<Tab>


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