package io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.DrawableResource
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

/**
 * One place a thing can be found elsewhere, ready to draw.
 *
 * Shared rather than owned by Accueil because three screens now end the same way — the foot of the
 * home stack, the foot of Plus, and the *Liens* section of a fiche — and the alternative was one
 * feature reaching into another's screen package for a row of marks.
 *
 * **It covers a website as well as a network**, which is why [icon] is not called a brand mark. The
 * content's own `link.type` set puts `website` alongside `instagram` and `spotify`, and on a fiche
 * they are the same offer: somewhere else this artist exists. A globe is a weaker mark than a brand
 * logo and that is correct — it is the one destination whose identity is the thing you tapped from.
 *
 * [icon] is null for a platform the app ships no mark for. The row falls back to [name] then, which
 * is why the model carries a name at all: the content can add a platform before the app ships its
 * mark, and when it does the row shows the word instead of dropping the link.
 *
 * [name] is [UiText] rather than a String because the two callers write it differently. The
 * association's own networks are named by the content — a brand name is not copy and does not
 * translate — while *Site web* on a fiche is the one label here that is a French sentence rather
 * than a proper noun.
 */
data class SocialLinkUiModel(
    val id: String,
    val name: UiText,
    val icon: DrawableResource?,
    val url: String,
)

/**
 * Keyed on the content's own id, against the marks bundled in `composeResources/drawable`. They are
 * monochrome vectors, so they tint with the rest of the row.
 *
 * The ten ids are `link.type`'s closed set in `content/SCHEMA.md`, which is a superset of the four
 * the association itself publishes — the fiche is what needed the other six, and keying both call
 * sites off one function is what stops the fiche and the footer disagreeing about what Instagram
 * looks like.
 *
 * Null for anything else, and that is the interesting case rather than an oversight — see
 * [SocialLinkUiModel.icon].
 *
 * It lives beside [SocialLinkUiModel] rather than in a UiMapper file because a UiMapper file may
 * hold nothing but its single State-to-UiModel function.
 */
fun socialIconFor(id: String): DrawableResource? =
    when (id) {
        "website" -> Res.drawable.ic_website
        "instagram" -> Res.drawable.ic_instagram
        "facebook" -> Res.drawable.ic_facebook
        "youtube" -> Res.drawable.ic_youtube
        "tiktok" -> Res.drawable.ic_tiktok
        "spotify" -> Res.drawable.ic_spotify
        "appleMusic" -> Res.drawable.ic_apple_music
        "soundcloud" -> Res.drawable.ic_soundcloud
        "bandcamp" -> Res.drawable.ic_bandcamp
        "beatport" -> Res.drawable.ic_beatport
        else -> null
    }
