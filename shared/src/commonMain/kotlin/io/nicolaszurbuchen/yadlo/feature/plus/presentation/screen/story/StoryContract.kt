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

data class StoryState(
    val page: StoryPage? = null,
    val hasLoaded: Boolean = false,
)
