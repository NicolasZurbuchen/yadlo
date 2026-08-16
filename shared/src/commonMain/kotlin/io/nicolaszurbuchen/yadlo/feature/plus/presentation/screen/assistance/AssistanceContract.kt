package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AssistanceGuide

sealed interface AssistanceIntent {
    data class NumberClicked(
        val number: String,
    ) : AssistanceIntent

    data class LostPropertyClicked(
        val email: String,
    ) : AssistanceIntent
}

sealed interface AssistanceLabel {
    data class OpenUrl(
        val url: String,
    ) : AssistanceLabel
}

sealed interface AssistanceAction {
    data object ObserveGuide : AssistanceAction
}

sealed interface AssistanceMessage {
    data class GuideUpdated(
        val guide: AssistanceGuide?,
    ) : AssistanceMessage
}

data class AssistanceState(
    val guide: AssistanceGuide? = null,
    val hasLoaded: Boolean = false,
)
