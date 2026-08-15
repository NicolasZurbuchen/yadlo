package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview

/** Empty: every row on the root navigates, and nothing else on it can be operated. */
sealed interface PlusIntent

/** Empty: nothing on the root leaves the app. The rows that will are one screen deeper. */
sealed interface PlusLabel

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
