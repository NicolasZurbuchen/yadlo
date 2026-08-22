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

    /**
     * A handful of Plus screens, promoted because this is the moment they are worth opening.
     *
     * **It is not a shortcut, and the tap it saves is not the point.** Plus is a table of contents
     * where sixteen rows carry the same weight and nothing knows what month it is — *Devenir
     * bénévole* sits at the same volume as *Politique de confidentialité*, and someone in November
     * has no reason to open the tab at all. Accueil is the only surface in the app that knows the
     * Phase, so this is where a thing can be raised at the point it becomes actionable and dropped
     * again afterwards.
     *
     * **[items] is as long as the Phase deserves and no longer**, which is why the count runs from
     * one to three rather than filling a grid. Padding a promotion surface with whatever is left
     * over turns it into a smaller copy of Plus, and *that* really would be worth less than the tap
     * it saves. LIVE gets no block at all — see the UiMapper.
     *
     * [title] belongs to the Phase rather than to the block, because it is the block's whole
     * argument: *Préparer sa venue* over payment and transport says why those two and why now, and
     * one neutral heading over all five phases would say nothing.
     */
    data class QuickAccess(
        val title: UiText,
        val items: List<QuickAccessItemUiModel>,
    ) : HomeBlockUiModel
}

/**
 * One promoted tile.
 *
 * [url] is set for exactly the entries that leave the app and null for the rest, mirroring
 * `PlusRowUiModel`: a destination inside the app is a fixed key the navigator already knows, while
 * a link out is an address that only the content can supply.
 */
data class QuickAccessItemUiModel(
    val entry: QuickAccessEntryUiModel,
    val url: String?,
)
