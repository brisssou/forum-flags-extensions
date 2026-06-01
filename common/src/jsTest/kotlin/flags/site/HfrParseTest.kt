package flags.site

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verbatim topic rows copied from common/src/jsTest/resources/hfr-draps-sample.html
 * (logged-in capture). Kept inline so the test is hermetic (no resource IO in the
 * Karma test env) while still exercising the real linkedom parser on real markup.
 */
private val TWO_ROWS = """<table>
<tr class="sujet ligne_booleen cBackCouleurTab1 "  onmouseover="this.style.backgroundColor='#DEDFDF'" onmouseout="this.style.backgroundColor='#F7F7F7'">
					<td class="sujetCase1 cBackCouleurTab2 "><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/closedb_new.gif" title="Nouveau message" alt="On" /></td>
			<td class="sujetCase2"><img src="https://forum-images.hardware.fr/icones/message/icon6.gif" alt="" /></td><td scope="row" class="sujetCase3" ondblclick="location.href='/forum2.php?config=hfr.inc&amp;cat=3&amp;subcat=130&amp;post=154877&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0'"><a href="/forum2.php?config=hfr.inc&amp;cat=3&amp;subcat=130&amp;post=154877&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic" title="Sujet n°154877">Démos pour tester ton matériel audio cinéma !</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=3&amp;subcat=130&amp;post=154877&amp;page=5&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">5</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=3&amp;subcat=130&amp;post=154877&amp;page=5&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t2353947"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.5)" alt="flag" /></a></td><td class="sujetCase6 cBackCouleurTab2 "><a rel="nofollow" href="/profilebdd.php?config=hfr.inc&amp;pseudo=TomSawyerBzH" class="Tableau">TomSawyerB​zH</a></td>
				  <td class="sujetCase7">177</td>
				  <td class="sujetCase8">115301</td>
				  <td class="sujetCase9 cBackCouleurTab2 "><a href="/forum2.php?config=hfr.inc&amp;cat=3&amp;subcat=130&amp;post=154877&amp;page=5&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#bas" class="Tableau">30-12-2025&nbsp;à&nbsp;18:19<br /><b>sebnec</b></a></td><td class="sujetCase10"><input type="checkbox" name="topic0"  value="154877" /><input type="hidden" name="valuecat0" value="3" /><input type="hidden" name="valueforum0" value="hardwarefr" /></td></tr>
<tr class="sujet ligne_booleen cBackCouleurTab3 "  onmouseover="this.style.backgroundColor='#DEDFDF'" onmouseout="this.style.backgroundColor='#F7F7F7'">
					<td class="sujetCase1 cBackCouleurTab4 "><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/closedb_new.gif" title="Nouveau message" alt="On" /></td>
			<td class="sujetCase2"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/sondage.gif" alt="" /></td><td scope="row" class="sujetCase3" ondblclick="location.href='/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0'"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic" title="Sujet n°200553">[TU] VR autonome et lunettes XR</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=603&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">603</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=471&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t15677896"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.471)" alt="flag" /></a></td><td class="sujetCase6 cBackCouleurTab4 "><a rel="nofollow" href="/profilebdd.php?config=hfr.inc&amp;pseudo=librearbitre" class="Tableau">librearbit​re</a></td>
				  <td class="sujetCase7">24105</td>
				  <td class="sujetCase8">2044579</td>
				  <td class="sujetCase9 cBackCouleurTab4 "><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=603&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#bas" class="Tableau">22-05-2026&nbsp;à&nbsp;09:26<br /><b>joe75</b></a></td><td class="sujetCase10"><input type="checkbox" name="topic1"  value="200553" /><input type="hidden" name="valuecat1" value="5" /><input type="hidden" name="valueforum1" value="hardwarefr" /></td></tr>
</table>"""

class HfrParseTest {

    @Test
    fun parsesTitleIdsAndUnreadPages() {
        val topics = Hfr.parseUnread(TWO_ROWS)
        assertEquals(2, topics.size, "expected two topic rows")

        val zero = topics.single { it.topicId == "154877" }
        assertEquals("Démos pour tester ton matériel audio cinéma !", zero.title)
        assertEquals("3", zero.categoryId)
        assertEquals(0, zero.nbUnread, "total 5 pages, last read 5 -> 0 unread")

        val unread = topics.single { it.topicId == "200553" }
        assertEquals("[TU] VR autonome et lunettes XR", unread.title)
        assertEquals("5", unread.categoryId)
        assertEquals(132, unread.nbUnread, "total 603 pages, last read 471 -> 132 unread")
    }

    @Test
    fun buildsUrlsFromSiteConfig() {
        assertEquals(
            "https://forum.hardware.fr/forum1f.php?flags4chrome=1&config=hfr.inc&owntopic=1",
            Hfr.drapsUrl(),
        )
        assertEquals(
            "https://forum.hardware.fr/forum1.php?config=hfr.inc&cat=prive&page=1&owntopic=0",
            Hfr.mpsUrl(),
        )
        assertEquals(
            "https://forum.hardware.fr/forum1.php?config=hfr.inc&owntopic=1&cat=5",
            Hfr.ownCatUrl("5"),
        )
    }
}
