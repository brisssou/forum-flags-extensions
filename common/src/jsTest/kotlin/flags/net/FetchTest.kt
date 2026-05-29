package flags.net

import flags.site.Hfr
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * One verbatim topic row (post 200553, 132 unread) served by a throwaway local
 * HTTP server — the Node analog of WireMock. The integration test exercises the
 * real fetch -> text() -> parseUnread pipeline over real HTTP; only the live
 * forum's cookie auth and current markup remain E2E.
 */
private val ONE_ROW = """<table>
<tr class="sujet ligne_booleen cBackCouleurTab3 "  onmouseover="this.style.backgroundColor='#DEDFDF'" onmouseout="this.style.backgroundColor='#F7F7F7'">
					<td class="sujetCase1 cBackCouleurTab4 "><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/closedb_new.gif" title="Nouveau message" alt="On" /></td>
			<td class="sujetCase2"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/sondage.gif" alt="" /></td><td scope="row" class="sujetCase3" ondblclick="location.href='/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0'"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=1&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic" title="Sujet n°200553">[TU] VR autonome et lunettes XR</a></td><td class="sujetCase4"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=603&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0" class="cCatTopic">603</a></td><td class="sujetCase5"><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=471&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#t15677896"><img src="https://forum-images.hardware.fr/themes_static/images_forum/1/favoris.gif" title="Aller au dernier message lu sur ce sujet (p.471)" alt="flag" /></a></td><td class="sujetCase6 cBackCouleurTab4 "><a rel="nofollow" href="/profilebdd.php?config=hfr.inc&amp;pseudo=librearbitre" class="Tableau">librearbit​re</a></td>
				  <td class="sujetCase7">24105</td>
				  <td class="sujetCase8">2044579</td>
				  <td class="sujetCase9 cBackCouleurTab4 "><a href="/forum2.php?config=hfr.inc&amp;cat=5&amp;subcat=579&amp;post=200553&amp;page=603&amp;p=1&amp;sondage=0&amp;owntopic=1&amp;trash=0&amp;trash_post=0&amp;print=0&amp;numreponse=0&amp;quote_only=0&amp;new=0&amp;nojs=0#bas" class="Tableau">22-05-2026&nbsp;à&nbsp;09:26<br /><b>joe75</b></a></td><td class="sujetCase10"><input type="checkbox" name="topic1"  value="200553" /><input type="hidden" name="valuecat1" value="5" /><input type="hidden" name="valueforum1" value="hardwarefr" /></td></tr>
</table>"""

class FetchTest {

    /** The mock server can't observe credentials, so a stub guards that bit. */
    @Test
    fun sendsSessionCredentials() = run {
        var seenCredentials: String? = null
        val global = js("globalThis")
        val original = global.fetch
        global.fetch = { _: String, init: dynamic ->
            seenCredentials = init.credentials as String
            Promise.resolve<dynamic>(js("({ text: function () { return Promise.resolve(''); } })"))
        }
        // finally restores the global on every path, or a rejection would leak
        // the stub into later tests.
        fetchPage("https://forum.hardware.fr/forum1f.php")
            .finally { global.fetch = original }
            .then { assertEquals("include", seenCredentials, "session cookie must be sent") }
    }

    @Test
    fun fetchesAndParsesOverHttp() = Promise { resolve, reject ->
        val http = js("require('http')")
        val server = http.createServer({ _: dynamic, res: dynamic ->
            res.writeHead(200, js("({ 'Content-Type': 'text/html; charset=utf-8' })"))
            res.end(ONE_ROW)
        })
        server.listen(0, {
            val port = server.address().port as Int
            fetchPage("http://127.0.0.1:$port/")
                .finally { server.close() }
                .then { body ->
                    val topics = Hfr.parseUnread(body)
                    assertEquals(1, topics.size, "served one topic row")
                    assertEquals("200553", topics[0].topicId)
                    assertEquals(132, topics[0].nbUnread)
                    resolve(Unit)
                }
                .catch { e -> reject(e) }
        })
    }
}
