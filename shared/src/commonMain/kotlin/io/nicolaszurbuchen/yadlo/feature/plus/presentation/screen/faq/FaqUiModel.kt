package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import io.nicolaszurbuchen.yadlo.infra.text.UiText

/**
 * *Questions fréquentes* — the ordinary questions that had nowhere else to live.
 *
 * One entry today, and it is the one that revealed the screen was missing: *is entry free?* No tab
 * answered it and no prototype had a place for it, which is what the whole project is about — the
 * association's information is split between a stale site and a live Instagram, so the plainest
 * questions have no single home.
 */
data class FaqUiModel(
    val isLoading: Boolean,
    val entries: List<FaqEntryUiModel>,
    val emptyMessage: UiText?,
)

/**
 * **The answer is on the page, not behind a disclosure.** An accordion saves room on a list of
 * forty; on a list of one it hides the only thing the screen has to say, and costs a tap to reach
 * an answer someone opened the app in a queue to find.
 */
data class FaqEntryUiModel(
    val id: String,
    val question: String,
    val answer: String,
)
