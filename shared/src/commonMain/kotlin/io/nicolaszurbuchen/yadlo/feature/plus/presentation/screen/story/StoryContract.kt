package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StoryPage

/** Empty: the page is read, not operated. */
sealed interface StoryIntent

/** Empty: nothing here leaves the app. */
sealed interface StoryLabel

sealed interface StoryAction {
    data object ObserveStory : StoryAction
}

sealed interface StoryMessage {
    data class StoryUpdated(
        val page: StoryPage?,
    ) : StoryMessage
}

/**
 * A null [page] is the bundle not having landed yet, and nothing else. The row that opens this
 * screen is derived from the same story block, so "published without a story" is not a state the
 * navigation can reach — a second flag beside the null would be a field describing an impossibility.
 */
data class StoryState(
    val page: StoryPage? = null,
)
