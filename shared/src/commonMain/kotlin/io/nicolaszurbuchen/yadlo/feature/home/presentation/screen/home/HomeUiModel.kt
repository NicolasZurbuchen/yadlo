package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.DrawableResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.ic_facebook
import yadlo.shared.generated.resources.ic_instagram
import yadlo.shared.generated.resources.ic_tiktok
import yadlo.shared.generated.resources.ic_youtube

/**
 * Accueil is a block stack whose contents change with the Phase, so the screen renders a list it is
 * handed rather than deciding for itself which blocks exist. That puts the phase-to-blocks decision
 * in the UiMapper, where a test can assert it.
 */
data class HomeUiModel(
    val isLoading: Boolean,
    val blocks: List<HomeBlockUiModel>,
)

sealed interface HomeBlockUiModel {
    /**
     * A day count, not a clock. The prototype is unambiguous — one large `J-19` — and it is the
     * right unit: nobody plans around the seconds until a festival nineteen days away.
     */
    data class Countdown(
        val daysText: UiText,
        val subtitle: String,
    ) : HomeBlockUiModel

    data class Hero(
        val kicker: UiText,
        val title: UiText,
        val body: UiText,
    ) : HomeBlockUiModel

    data class ThankYou(
        val title: UiText,
        val body: UiText,
    ) : HomeBlockUiModel

    data class Figures(
        val title: UiText,
        val items: List<FigureUiModel>,
        /** Null when every figure is confirmed; the caveat Provenance owes the reader otherwise. */
        val caveat: UiText?,
    ) : HomeBlockUiModel

    /**
     * [hasMore] is false when the block is already showing every annonce there is — the action that
     * opens the full list is hidden then, because a button leading to what you are already reading
     * is the same problem as a button that does nothing.
     */
    data class Announcements(
        val title: UiText,
        val items: List<AnnouncementUiModel>,
        val hasMore: Boolean,
    ) : HomeBlockUiModel

    data class Social(
        val items: List<SocialUiModel>,
    ) : HomeBlockUiModel
}

data class FigureUiModel(
    val id: String,
    val value: String,
    val label: String,
)

/**
 * [icon] is null for a network the app ships no mark for. The card falls back to [name] then, which
 * is why the domain model carries a name at all — a platform nobody has heard of yet still renders.
 */
data class SocialUiModel(
    val id: String,
    val name: String,
    val icon: DrawableResource?,
    val url: String,
)

/**
 * Keyed on the content's own id, against the brand marks bundled in `composeResources/drawable`.
 * They are monochrome single-path vectors, so they tint with the rest of the row.
 *
 * Null for anything else, and that is the interesting case rather than an oversight: the content
 * can add a network before the app ships its mark, and when it does the row shows the name instead
 * of dropping it.
 *
 * It lives beside [SocialUiModel] rather than in the UiMapper because a UiMapper file may hold
 * nothing but the single State-to-UiModel function.
 */
fun socialIconFor(id: String): DrawableResource? =
    when (id) {
        "instagram" -> Res.drawable.ic_instagram
        "facebook" -> Res.drawable.ic_facebook
        "youtube" -> Res.drawable.ic_youtube
        "tiktok" -> Res.drawable.ic_tiktok
        else -> null
    }
