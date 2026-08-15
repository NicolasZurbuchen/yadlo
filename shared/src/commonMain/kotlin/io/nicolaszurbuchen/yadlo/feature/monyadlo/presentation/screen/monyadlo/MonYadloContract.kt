package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.MonYadloContent
import kotlin.time.Instant

/**
 * Empty, and declared anyway. This screen recalls; it has no filter, no sort and no toggle, and the
 * two things you can tap are both navigation, which the Route owns. The interface stays so the
 * Store's type parameters are the screen's own rather than borrowed from another one.
 */
sealed interface MonYadloIntent

/** Empty for the same reason: nothing here leaves the app or fires once. */
sealed interface MonYadloLabel

sealed interface MonYadloAction {
    data object ObserveContent : MonYadloAction

    data object StartTicking : MonYadloAction
}

sealed interface MonYadloMessage {
    data class ContentUpdated(
        val content: MonYadloContent,
    ) : MonYadloMessage

    data class Ticked(
        val now: Instant,
    ) : MonYadloMessage
}

/**
 * A null [content] is "not read yet". It never becomes null again: an emptied Plan is a
 * [MonYadloContent] with no days, which is a different screen from one that has not loaded.
 */
data class MonYadloState(
    val now: Instant,
    val content: MonYadloContent? = null,
)
