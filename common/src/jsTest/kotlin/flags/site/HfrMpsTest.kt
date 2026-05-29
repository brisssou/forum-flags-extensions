package flags.site

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Verbatim new-MP notice from hfr-mps-sample.html (1 new message). */
private val MP_NOTICE =
    """<a href="/forum2.php?config=hfr.inc&amp;cat=prive&amp;post=1596454&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=0&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#bas" class="red">Vous avez 1 nouveau message privé</a>"""

/** Verbatim plain messagerie link from hfr-draps-sample.html (no new MP). */
private val MESSAGERIE_LINK =
    """<a href="/forum1.php?config=hfr.inc&amp;cat=prive&amp;page=1&amp;subcat=&amp;sondage=0&amp;owntopic=0&amp;trash=0&amp;trash_post=0&amp;moderation=0&amp;new=0&amp;nojs=0&amp;subcatgroup=0" class="s1Ext">Messages privés</a>"""

class HfrMpsTest {

    @Test
    fun countsNewPrivateMessages() {
        assertEquals(1, Hfr.parseMps("<div>$MP_NOTICE</div>"))
    }

    @Test
    fun ignoresPlainMessagerieLink() {
        // Same cat=prive target, no "Vous avez" notice -> 0, no .red reliance.
        assertEquals(0, Hfr.parseMps("<div>$MESSAGERIE_LINK</div>"))
    }

    @Test
    fun reportsZeroWhenNoNotice() {
        assertEquals(0, Hfr.parseMps("<div>pas de nouveau message</div>"))
    }

    @Test
    fun detectsLoggedOutPage() {
        assertTrue(Hfr.isNotLoggedIn("<p>Aucun sujet que vous avez lu n'est connu</p>"))
    }

    @Test
    fun loggedInPageHasNoLoggedOutMarker() {
        assertFalse(Hfr.isNotLoggedIn("<table><tr class='sujet'></tr></table>"))
    }
}
