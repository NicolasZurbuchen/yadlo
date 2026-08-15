package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Accessibilité* — what is confirmed, what is confirmed not to be, and somebody to ask.
 *
 * [nothingPublished] is the state the screen was designed around rather than an edge case: the
 * festival publishes nothing on the subject today, so the honest page is one that says so and hands
 * over an address. A reassuring, vague page is what story 40 exists to prevent, and it is also the
 * page that gets someone to drive thirty kilometres for nothing.
 */
data class AccessibilityUiModel(
    val isLoading: Boolean,
    val available: List<AccessibilityFactUiModel>,
    val unavailable: List<AccessibilityFactUiModel>,
    val contactEmail: String?,
    val nothingPublished: Boolean,
    val emptyMessage: UiText?,
)

/** [note] is where a yes or a no needs a condition on it — "à l'entrée seulement". */
data class AccessibilityFactUiModel(
    val id: String,
    val name: String,
    val note: String?,
)
