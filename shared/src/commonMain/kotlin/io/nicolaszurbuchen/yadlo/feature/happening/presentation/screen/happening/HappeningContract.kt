package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail
import kotlin.time.Instant

sealed interface HappeningIntent {
    /** A booking page, an artist's own site, a stand's Instagram — everything here leaves the app. */
    data class LinkClicked(
        val url: String,
    ) : HappeningIntent
}

sealed interface HappeningLabel {
    data class OpenUrl(
        val url: String,
    ) : HappeningLabel
}

sealed interface HappeningAction {
    data object ObserveDetail : HappeningAction

    data object StartTicking : HappeningAction
}

sealed interface HappeningMessage {
    /**
     * A null [detail] is the Happening having gone from the content, not the content having failed
     * to load — the two are different screens, and only the Store can tell them apart.
     */
    data class DetailUpdated(
        val detail: HappeningDetail?,
    ) : HappeningMessage

    data class Ticked(
        val now: Instant,
    ) : HappeningMessage
}

/**
 * [isLoaded] rather than inferring it from a null [detail]: before the first emission there is
 * nothing to say, and afterwards a null means the Happening is gone. A single nullable field would
 * make those two the same screen, and the second one owes the visitor an explanation.
 */
data class HappeningState(
    val now: Instant,
    val detail: HappeningDetail? = null,
    val isLoaded: Boolean = false,
)
