package flags.site

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verbatim category header rows copied from
 * common/src/jsTest/resources/hfr-draps-sample.html. Each `tr.fondForum1fCat`
 * holds a reset link (`href="#1"`, with the cat id only inside its onclick) and
 * the section link (`a.cHeader`, cat id in its href). Inlined so the test is
 * hermetic while exercising the real linkedom parser on real markup.
 */
private val TWO_HEADERS = """<table>
<tr class="cBackHeader fondForum1fCat" ondblclick="location.href='/forum1.php?config=hfr.inc&amp;cat=3&amp;page=1&amp;owntopic=1&amp;nojs=0&amp;subcatgroup=0'">
		<th class="padding" colspan="10"><a href="#1" onclick="if (!(warning1('/user/suppressflag.php?id_user=7025&amp;config=hfr.inc&amp;owntopic=1&amp;cat=3&amp;codehex=deadbeef','Etes vous sur de vouloir effacer vos drapeaux sur cette catégorie ?'))) return false;" title="Réinitialiser les drapeaux de cette catégorie"><img class="right" src="https://forum-images.hardware.fr/themes_static/images_forum/1/flagn1.gif" alt="" /></a><a href="/forum1.php?config=hfr.inc&amp;cat=3&amp;page=1&amp;owntopic=1&amp;nojs=0&amp;subcatgroup=0" class="cHeader">Video &amp; Son</a></th>
</tr>
<tr class="cBackHeader fondForum1fCat" ondblclick="location.href='/forum1.php?config=hfr.inc&amp;cat=5&amp;page=1&amp;owntopic=1&amp;nojs=0&amp;subcatgroup=0'">
		<th class="padding" colspan="10"><a href="#1" onclick="if (!(warning1('/user/suppressflag.php?id_user=7025&amp;config=hfr.inc&amp;owntopic=1&amp;cat=5&amp;codehex=deadbeef','Etes vous sur de vouloir effacer vos drapeaux sur cette catégorie ?'))) return false;" title="Réinitialiser les drapeaux de cette catégorie"><img class="right" src="https://forum-images.hardware.fr/themes_static/images_forum/1/flagn1.gif" alt="" /></a><a href="/forum1.php?config=hfr.inc&amp;cat=5&amp;page=1&amp;owntopic=1&amp;nojs=0&amp;subcatgroup=0" class="cHeader">Jeux Video</a></th>
</tr>
</table>"""

class HfrCategoriesTest {

    @Test
    fun parsesCategoryIdsAndNames() {
        val categories = Hfr.parseCategories(TWO_HEADERS)

        // Exactly two entries: the reset links (href="#1") must not produce any.
        assertEquals(2, categories.size, "expected one entry per section header")
        // Name is entity-decoded by the DOM ("&amp;" -> "&").
        assertEquals("Video & Son", categories["3"])
        assertEquals("Jeux Video", categories["5"])
    }
}
