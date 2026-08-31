package io.nicolaszurbuchen.yadlo.design.uimodel

import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.ic_apple_music
import yadlo.shared.generated.resources.ic_bandcamp
import yadlo.shared.generated.resources.ic_beatport
import yadlo.shared.generated.resources.ic_facebook
import yadlo.shared.generated.resources.ic_instagram
import yadlo.shared.generated.resources.ic_soundcloud
import yadlo.shared.generated.resources.ic_spotify
import yadlo.shared.generated.resources.ic_tiktok
import yadlo.shared.generated.resources.ic_website
import yadlo.shared.generated.resources.ic_youtube
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * The ids come out of the content, so this is the one place the app decides what an authored string
 * means. Getting a pairing wrong is invisible in review — two monochrome glyphs at 24dp — and shows
 * up as a Bandcamp mark on a Spotify link.
 */
class SocialLinkUiModelTest {
    @Test
    fun socialIconFor_mapsEveryPublishedIdToItsOwnMark() {
        // A table rather than ten asserts: the interesting failure is two ids landing on one glyph,
        // which reads as a working row and is wrong on both.
        val mapped = IDS.associateWith { socialIconFor(it) }

        assertEquals(
            mapOf(
                "website" to Res.drawable.ic_website,
                "instagram" to Res.drawable.ic_instagram,
                "facebook" to Res.drawable.ic_facebook,
                "youtube" to Res.drawable.ic_youtube,
                "tiktok" to Res.drawable.ic_tiktok,
                "spotify" to Res.drawable.ic_spotify,
                "appleMusic" to Res.drawable.ic_apple_music,
                "soundcloud" to Res.drawable.ic_soundcloud,
                "bandcamp" to Res.drawable.ic_bandcamp,
                "beatport" to Res.drawable.ic_beatport,
            ),
            mapped,
        )
    }

    @Test
    fun socialIconFor_givesEachPlatformADistinctMark() {
        assertEquals(IDS.size, IDS.mapNotNull { socialIconFor(it) }.distinct().size)
    }

    @Test
    fun socialIconFor_anUnknownPlatformHasNoMarkRatherThanAWrongOne() {
        // The content can name a platform before the app ships a glyph for it. `SocialLinksRow`
        // falls back to the platform's name, so null is the answer that keeps the link usable.
        assertNull(socialIconFor("mastodon"))
        assertNull(socialIconFor(""))
    }

    @Test
    fun socialIconFor_matchesTheIdExactlyRatherThanLoosely() {
        // The ids are authored by hand in the content, and a near miss should fall back to the
        // name rather than quietly resolve — camelCase in particular is easy to get wrong.
        assertNull(socialIconFor("applemusic"))
        assertNull(socialIconFor("Instagram"))
        assertNull(socialIconFor(" instagram"))
    }

    private companion object {
        val IDS =
            listOf(
                "website",
                "instagram",
                "facebook",
                "youtube",
                "tiktok",
                "spotify",
                "appleMusic",
                "soundcloud",
                "bandcamp",
                "beatport",
            )
    }
}
