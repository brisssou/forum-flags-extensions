package flags.site

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Composes verbatim fragments of hfr-draps-sample.html — a `cat=5` section
 * header, its `post=200553` topic row (603 pages, last read 471 -> 132 unread)
 * and a new-MP notice — into a logged-in draps page. The individual parsers are
 * covered elsewhere; this checks [flags.site.Site.snapshot] wires them together
 * and stamps login state / fetchedAt.
 */
private val LOGGED_IN_PAGE = """<table>
<tr class="cBackHeader fondForum1fCat"><th class="padding" colspan="10"><a href="#1" onclick="return false;">reset</a><a href="/forum1.php?config=hfr.inc&amp;cat=5&amp;page=1&amp;owntopic=1&amp;nojs=0&amp;subcatgroup=0" class="cHeader">Jeux Video</a></th></tr>
<tr class="sujet ligne_booleen cBackCouleurTab1 ">
<td scope="row" class="sujetCase3"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;new=0&amp;nojs=0" class="cCatTopic" title="Sujet n°200553">[TU] VR autonome et lunettes XR</a></td>
<td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=603&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;new=0&amp;nojs=0" class="cCatTopic">603</a></td>
<td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=471&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;new=0&amp;nojs=0#t15677896"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" alt="flag" /></a></td>
</tr>
</table>
<a href="/forum2.php?config=hfr.inc&amp;cat=prive&amp;post=1596454&amp;page=1&amp;nojs=0#bas" class="red">Vous avez 1 nouveau message privé</a>"""

class HfrSnapshotTest {

    @Test
    fun buildsSnapshotFromLoggedInPage() {
        val fetchedAt = 1717200000000.0
        val snapshot = Hfr.snapshot(LOGGED_IN_PAGE, fetchedAt)

        assertTrue(snapshot.loggedIn)
        assertEquals(fetchedAt, snapshot.fetchedAt)
        assertEquals(1, snapshot.mps)
        assertEquals(mapOf("5" to "Jeux Video"), snapshot.categories)

        assertEquals(1, snapshot.topics.size)
        val topic = snapshot.topics.single()
        assertEquals("200553", topic.topicId)
        assertEquals("5", topic.categoryId)
        assertEquals(132, topic.nbUnread, "total 603 pages, last read 471 -> 132 unread")
    }

    @Test
    fun buildsEmptySnapshotWhenLoggedOut() {
        val snapshot = Hfr.snapshot("<p>Aucun sujet que vous avez lu n'est connu</p>", 42.0)

        assertFalse(snapshot.loggedIn)
        assertEquals(42.0, snapshot.fetchedAt)
        assertEquals(emptyList(), snapshot.topics)
        assertEquals(emptyMap(), snapshot.categories)
        assertEquals(0, snapshot.mps)
    }
}
