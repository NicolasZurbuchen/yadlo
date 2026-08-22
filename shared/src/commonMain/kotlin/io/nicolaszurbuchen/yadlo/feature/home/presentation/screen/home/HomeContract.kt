package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import kotlin.time.Instant

sealed interface HomeIntent {
    data object HeroClicked : HomeIntent

    data class AnnouncementClicked(
        val url: String,
    ) : HomeIntent

    data class SocialClicked(
        val url: String,
    ) : HomeIntent

    data object AllAnnouncementsClicked : HomeIntent

    /**
     * Only the promoted tiles that *leave* the app come through here, carrying the address the
     * screen read off the model. The ones that open a screen go straight to the navigator from the
     * Route — routing them through the store would add a hop that only forwards, and write the
     * navigation decision down in two places. The same split `PlusRoute` makes, for the same reason.
     */
    data class QuickAccessLinkClicked(
        val url: String,
    ) : HomeIntent
}

sealed interface HomeLabel {
    data object NavigateToProgramme : HomeLabel

    data object NavigateToAnnouncements : HomeLabel

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
        val siteMoment: SiteMomentUiModel?,
    ) : HomeMessage

    data class Ticked(
        val now: Instant,
        val phase: PhaseUiModel,
        val siteMoment: SiteMomentUiModel?,
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
    /**
     * Null until an Edition with days is loaded, and meaningless outside [PhaseUiModel.LIVE] — the
     * mapper reads it only in that Phase. Carried beside the Phase rather than derived from it
     * because the two answer different questions: LIVE says the visitor is at the festival, this
     * says whether the beach is currently open.
     */
    val siteMoment: SiteMomentUiModel? = null,
)
