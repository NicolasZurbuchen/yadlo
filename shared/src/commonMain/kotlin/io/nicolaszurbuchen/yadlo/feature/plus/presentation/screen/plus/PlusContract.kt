package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview

/**
 * Three taps on the root leave the app instead of opening a screen.
 *
 * Neither of the two rows carries its URL: the tab is the only thing that knows them, and putting
 * an address on a row would make the screen responsible for a fact it never draws. The store reads
 * them back off the overview it is already holding. A network is the exception and carries its own,
 * because the footer draws four of them and which one was tapped is the only thing distinguishing
 * them.
 */
sealed interface PlusIntent {
    data object NewsletterClicked : PlusIntent

    data object ReportClicked : PlusIntent

    data class SocialClicked(
        val url: String,
    ) : PlusIntent
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
 * A null [overview] means the bundle has not landed yet, and it is the state this screen is in on
 * every single launch — the bootstrapper's flow has not emitted by the time the first frame
 * composes, however briefly. It is not an error state and it is not "the content failed": the shell
 * has already refused to show any tab before the content is Ready, so a failure never gets this
 * far.
 *
 * **One field rather than a `null` and an `isLoading` beside it.** The two would be the same fact
 * written twice and free to disagree; the name the screen wants lives on [PlusUiModel], where the
 * mapper derives it from this and nothing else can set it.
 *
 * The loaded-but-empty case — a bundle published with every section stripped — reduces to no cards
 * and no footer rather than to a crash, because every row is derived from a section that exists.
 */
data class PlusState(
    val overview: PlusOverview? = null,
)
