package flags.message

/** Message types exchanged between the popup and the service worker. */
object Messages {
    /** Popup asks the worker to re-poll now and reply with the fresh snapshot record. */
    const val REFRESH = "refresh"
}
