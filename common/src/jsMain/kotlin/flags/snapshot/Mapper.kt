package flags.snapshot

import flags.model.Topic
import flags.util.isAbsent
import flags.util.orBool
import flags.util.orDouble
import flags.util.orInt

/**
 * Maps [Snapshot] (and its nested [Topic]s and category map) to and from the
 * plain JS records `chrome.storage` stores. Kept out of the model so [Snapshot]
 * stays a pure data type; the defaults it reads live on [Snapshot.Companion].
 * Import the members to use them: `snapshot.toRecord()` and
 * `Snapshot.fromRecord(record)`.
 */
object Mapper {

    /** Serializes to a plain JS object for `chrome.storage`. */
    fun Snapshot.toRecord(): dynamic {
        val r = js("{}")
        r.loggedIn = loggedIn
        r.topics = topics.map { it.toRecord() }.toTypedArray()
        r.mps = mps
        r.categories = categoriesToRecord(categories)
        r.fetchedAt = fetchedAt
        return r
    }

    /**
     * Reads a [Snapshot] from a `chrome.storage` record, defaulting if absent.
     *
     * The untyped source is a parameter, not a receiver: a call on a `dynamic`
     * receiver is dispatched at runtime as a JS lookup, so a `dynamic` extension
     * (`fun dynamic.toSnapshot()`) would never be selected. Hanging it off
     * [Snapshot.Companion] keeps the factory reading as `Snapshot.fromRecord(record)`
     * and lets it see the companion's `DEFAULT_*` defaults unqualified.
     */
    fun Snapshot.Companion.fromRecord(record: dynamic): Snapshot {
        if (isAbsent(record)) return Snapshot()
        return Snapshot(
            loggedIn = orBool(record.loggedIn, DEFAULT_LOGGED_IN),
            topics = topicsFromRecord(record.topics),
            mps = orInt(record.mps, DEFAULT_MPS),
            categories = categoriesFromRecord(record.categories),
            fetchedAt = orDouble(record.fetchedAt, DEFAULT_FETCHED_AT),
        )
    }

    private fun Topic.toRecord(): dynamic {
        val r = js("{}")
        r.topicId = topicId
        r.title = title
        r.categoryId = categoryId
        r.href = href
        r.nbUnread = nbUnread
        return r
    }

    private fun topicsFromRecord(v: dynamic): List<Topic> =
        if (isAbsent(v)) emptyList()
        else v.unsafeCast<Array<dynamic>>().map { t ->
            Topic(
                topicId = t.topicId.unsafeCast<String>(),
                title = t.title.unsafeCast<String>(),
                categoryId = t.categoryId.unsafeCast<String>(),
                href = t.href.unsafeCast<String>(),
                nbUnread = t.nbUnread.unsafeCast<Double>().toInt(),
            )
        }

    private fun categoriesToRecord(categories: Map<String, String>): dynamic {
        val o = js("{}")
        for ((id, name) in categories) o[id] = name
        return o
    }

    private fun categoriesFromRecord(v: dynamic): Map<String, String> {
        if (isAbsent(v)) return emptyMap()
        val keys = js("Object.keys")(v).unsafeCast<Array<String>>()
        return keys.associateWith { v[it].unsafeCast<String>() }
    }
}
