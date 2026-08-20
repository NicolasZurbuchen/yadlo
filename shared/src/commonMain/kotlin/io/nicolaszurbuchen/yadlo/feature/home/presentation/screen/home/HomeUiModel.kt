package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.DrawableResource

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

    /**
     * The Monday after, and the only hero in the app that carries a picture — DECISIONS.md
     * § The thank-you is the one hero with a photograph.
     *
     * [image] is not nullable, unlike a fiche's: this block exists for exactly one Phase and ships
     * with the photograph it was written for, so a null here would describe a state the app cannot
     * reach.
     */
    data class ThankYou(
        val title: UiText,
        val body: UiText,
        val image: DrawableResource,
    ) : HomeBlockUiModel

    data class Figures(
        val title: UiText,
        val items: List<YadloFigureUiModel>,
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
        val items: List<SocialLinkUiModel>,
    ) : HomeBlockUiModel
}
