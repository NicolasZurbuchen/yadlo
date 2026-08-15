package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview

/**
 * Two rows of the root leave the app instead of opening a screen.
 *
 * Neither carries its URL: the tab is the only thing that knows them, and putting an address on a
 * row would make the screen responsible for a fact it never draws. The store reads them back off
 * the overview it is already holding.
 */
sealed interface PlusIntent {
    data object NewsletterClicked : PlusIntent

    data object ReportClicked : PlusIntent
}

sealed interface PlusLabel {
    data class OpenUrl(
        val url: String,
    ) : PlusLabel
}

sealed interface PlusAction {
    data object ObserveOverview : PlusAction
}

sealed interface PlusMessage {
    data class OverviewUpdated(
        val overview: PlusOverview,
    ) : PlusMessage
}

/**
 * A null [overview] is "the bundle has not landed yet", which is a different screen from a tab with
 * nothing on it. The second state is not reachable in practice and is drawn anyway: every row is
 * derived from a published section, so a bundle stripped of all of them would leave four empty
 * cards rather than a crash.
 */
data class PlusState(
    val overview: PlusOverview? = null,
)
