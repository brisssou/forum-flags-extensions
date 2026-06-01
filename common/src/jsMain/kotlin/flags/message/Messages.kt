package flags.message

/** Message types exchanged between the popup and the service worker. */
enum class Messages {
    /** Re-poll now and reply with the fresh snapshot record. */
    REFRESH,

    /** Re-poll a short while from now (no reply), after the popup opens links. */
    REFRESH_SOON,
}
