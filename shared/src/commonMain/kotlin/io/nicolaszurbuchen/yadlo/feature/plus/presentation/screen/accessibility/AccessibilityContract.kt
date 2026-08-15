package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AccessibilityGuide

sealed interface AccessibilityIntent {
    data class ContactClicked(
        val email: String,
    ) : AccessibilityIntent
}

sealed interface AccessibilityLabel {
    data class OpenUrl(
        val url: String,
    ) : AccessibilityLabel
}

sealed interface AccessibilityAction {
    data object ObserveGuide : AccessibilityAction
}

sealed interface AccessibilityMessage {
    data class GuideUpdated(
        val guide: AccessibilityGuide?,
    ) : AccessibilityMessage
}

data class AccessibilityState(
    val guide: AccessibilityGuide? = null,
    val hasLoaded: Boolean = false,
)
