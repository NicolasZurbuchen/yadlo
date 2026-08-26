package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory

sealed interface StandsIntent {
    /** Null is *Tout*, which clears the lot. Any other mark toggles — see [StandsState.selectedMarks]. */
    data class MarkToggled(
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

    data class MarkToggled(
        val mark: String?,
    ) : StandsMessage
}

/**
 * **[selectedMarks] combine as an AND, and that is the only safe reading.** A set was avoided at
 * first because two chips read as an intersection to whoever wrote them and a union to whoever
 * reads them — but the two readings are not equally wrong here. Someone who needs vegan *and*
 * gluten-free, shown a stand that is merely vegan, is being pointed at food they cannot eat. The
 * union fails toward harm; the intersection fails toward one fewer row.
 *
 * The filter is not persisted. It answers a question asked once at the counter, and a chip that
 * survived into the next launch would silently hide two thirds of the list.
 *
 * [kind] is which half of the stands this is, and it arrives with the destination rather than the
 * content — so the bar reads correctly while the list is still loading. It arrives from the back
 * stack as the presentation mirror, because a NavKey may not name a domain type, and the Store
 * converts it once at construction so that everything downstream reads the kind the content is
 * keyed by.
 */
data class StandsState(
    val kind: StandKind,
    val directory: StandDirectory? = null,
    val selectedMarks: Set<String> = emptySet(),
)
