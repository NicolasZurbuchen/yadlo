package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory

sealed interface StandsIntent {
    /** Null is *Tout*. One mark at a time — see [StandsState.selectedMark]. */
    data class MarkSelected(
        val mark: String?,
    ) : StandsIntent

    data class StandClicked(
        val happeningId: String,
    ) : StandsIntent
}

sealed interface StandsLabel {
    data class NavigateToHappening(
        val happeningId: String,
    ) : StandsLabel
}

sealed interface StandsAction {
    data object ObserveDirectory : StandsAction
}

sealed interface StandsMessage {
    data class DirectoryUpdated(
        val directory: StandDirectory,
    ) : StandsMessage

    data class MarkSelected(
        val mark: String?,
    ) : StandsMessage
}

/**
 * **One mark at a time, not a set.** Combining `végan` and `sans gluten` reads as an intersection to
 * whoever wrote it and as a union to whoever reads it, and on six stands the difference is one
 * scroll either way. A single chip is unambiguous and is also how someone actually asks the
 * question: *what can I eat*, not *what satisfies all of these*.
 *
 * The filter is not persisted. It answers a question asked once at the counter, and a chip that
 * survived into the next launch would silently hide two thirds of the list.
 *
 * [kind] is which half of the stands this is, and it arrives with the destination rather than the
 * content — so the bar reads correctly while the list is still loading.
 */
data class StandsState(
    val kind: StandsKind,
    val directory: StandDirectory? = null,
    val selectedMark: String? = null,
)
