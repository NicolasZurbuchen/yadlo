package io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel

import org.jetbrains.compose.resources.DrawableResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.ic_facebook
import yadlo.shared.generated.resources.ic_instagram
import yadlo.shared.generated.resources.ic_tiktok
import yadlo.shared.generated.resources.ic_youtube

/**
 * One of the association's networks, ready to draw.
 *
 * Shared rather than owned by Accueil because two screens now end the same way — the foot of the
 * home stack and the foot of Plus — and the alternative was one feature reaching into another's
 * screen package for a row of four icons.
 *
 * [icon] is null for a network the app ships no mark for. The row falls back to [name] then, which
 * is why the model carries a name at all: the content can add a platform before the app ships its
 * mark, and when it does the row shows the word instead of dropping the link.
 */
data class SocialLinkUiModel(
    val id: String,
    val name: String,
    val icon: DrawableResource?,
    val url: String,
)

/**
 * Keyed on the content's own id, against the brand marks bundled in `composeResources/drawable`.
 * They are monochrome single-path vectors, so they tint with the rest of the row.
 *
 * Null for anything else, and that is the interesting case rather than an oversight — see
 * [SocialLinkUiModel.icon].
 *
 * It lives beside [SocialLinkUiModel] rather than in a UiMapper because a UiMapper file may hold
 * nothing but its single State-to-UiModel function.
 */
fun socialIconFor(id: String): DrawableResource? =
    when (id) {
        "instagram" -> Res.drawable.ic_instagram
        "facebook" -> Res.drawable.ic_facebook
        "youtube" -> Res.drawable.ic_youtube
        "tiktok" -> Res.drawable.ic_tiktok
        else -> null
    }
