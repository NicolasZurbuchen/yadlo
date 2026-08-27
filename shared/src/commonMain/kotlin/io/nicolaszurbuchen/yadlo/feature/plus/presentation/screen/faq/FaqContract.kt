package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FaqEntry

/** Empty: the answers are on the page, not behind a disclosure. */
sealed interface FaqIntent

/** Empty: nothing here leaves the app. */
sealed interface FaqLabel

sealed interface FaqAction {
    data object ObserveFaq : FaqAction
}

sealed interface FaqMessage {
    data class FaqUpdated(
        val entries: List<FaqEntry>,
    ) : FaqMessage
}

data class FaqState(
    val entries: List<FaqEntry>? = null,
)
