package flags.site

import flags.model.Topic
import flags.parse.parseHTML

/**
 * hardware.fr forum. Uses the shared [Site] url builders (no `basePath`).
 */
object Hfr : Site {
    override val id = "hfr"
    override val host = "forum.hardware.fr"
    override val config = "hfr.inc"
    override val defaultColor = "#2F3740"

    /**
     * Each flagged topic is a `tr.sujet` row. The title and total page count
     * are separate `a.cCatTopic` links (distinguished by their `td` cell); the
     * flag link's href carries the cat / post / last-read-page query params.
     * Unread pages = total − last-read, never negative.
     */
    override fun parseUnread(html: String): List<Topic> =
        parseHTML(html).document.querySelectorAll("tr.sujet").mapNotNull { row ->
            val title = row.querySelector("td.sujetCase3 a.cCatTopic")?.textContent
                ?: return@mapNotNull null
            val href = row.querySelector("td.sujetCase5 a")?.getAttribute("href")
                ?: return@mapNotNull null
            val cat = CAT.find(href)?.groupValues?.get(1) ?: return@mapNotNull null
            val post = POST.find(href)?.groupValues?.get(1) ?: return@mapNotNull null
            val lastRead = PAGE.find(href)?.groupValues?.get(1)?.toIntOrNull() ?: 1
            val total = row.querySelector("td.sujetCase4 a.cCatTopic")
                ?.textContent?.trim()?.toIntOrNull() ?: lastRead
            Topic(
                topicId = post,
                title = title.trim(),
                categoryId = cat,
                href = href,
                nbUnread = (total - lastRead).coerceAtLeast(0),
            )
        }
}

private val CAT = Regex("[?&]cat=(\\d+)")
private val POST = Regex("[?&]post=(\\d+)")
private val PAGE = Regex("[?&]page=(\\d+)")
