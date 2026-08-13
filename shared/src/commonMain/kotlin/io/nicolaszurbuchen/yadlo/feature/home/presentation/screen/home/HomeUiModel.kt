package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

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
     * A day count, not a clock. The prototype is unambiguous — one big `J-19` — and it is the right
     * unit: nobody plans around the seconds until a festival that is nineteen days away.
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

    data class Announcements(
        val title: UiText,
        val items: List<AnnouncementUiModel>,
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

/** [url] null is not an error state: story 85 wants such an annonce plainly untappable. */
data class AnnouncementUiModel(
    val id: String,
    val dateText: String,
    val title: String,
    val body: String?,
    val url: String?,
)

data class SocialUiModel(
    val id: String,
    val name: String,
    val url: String,
)

/**
 * The presentation twin of the domain `Phase`.
 *
 * It exists because the Store converts on the way out, so the domain enum stops at the Store
 * boundary the way `State` does. That is also what lets the UiMapper decide the block stack without
 * importing the domain layer, which the architecture forbids it.
 */
enum class PhaseUiModel {
    OFF_SEASON,
    ANNOUNCED,
    APPROACHING,
    LIVE,
    ENDED,
}
