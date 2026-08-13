package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import kotlin.time.Instant

sealed interface HomeIntent {
    /** One intent for both heroes: which tab it opens is the Phase's business, not the screen's. */
    data object HeroClicked : HomeIntent

    data class AnnouncementClicked(
        val url: String,
    ) : HomeIntent
}

sealed interface HomeLabel {
    data object NavigateToProgramme : HomeLabel

    data object NavigateToMonYadlo : HomeLabel

    data class OpenUrl(
        val url: String,
    ) : HomeLabel
}

sealed interface HomeAction {
    data object ObserveContent : HomeAction

    data object StartTicking : HomeAction
}

sealed interface HomeMessage {
    /**
     * The Phase travels with the content because it is derived from it — dispatching them
     * separately would leave a frame where a new edition is on screen under the old Phase.
     */
    data class ContentUpdated(
        val content: HomeContent,
        val phase: PhaseUiModel,
    ) : HomeMessage

    data class Ticked(
        val now: Instant,
        val phase: PhaseUiModel,
    ) : HomeMessage
}

/**
 * [now] is a field rather than a call at render time because the countdown and the Phase must agree
 * on a single reading, and because no part of the app is allowed to ask a clock it does not own.
 */
data class HomeState(
    val now: Instant,
    val phase: PhaseUiModel,
    val content: HomeContent? = null,
)
