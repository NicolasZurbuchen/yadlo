package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Horaires* — the one question this screen exists to answer, per day.
 *
 * [caveat] appears when a day's window was derived rather than published, and [beforeOpeningNote]
 * when something is programmed before the site opens. The second is not an error and must not read
 * as one: the plage de Préverenges is public, so the morning yoga genuinely runs at 10:00 on a day
 * the festival opens at 12:00.
 */
data class HoursUiModel(
    val isLoading: Boolean,
    val days: List<OpeningDayUiModel>,
    val caveat: UiText?,
    val beforeOpeningNote: UiText?,
    val emptyMessage: UiText?,
)

/**
 * [window] is the answer and is set large; [programme] is what fills it and sits under.
 *
 * [programme] is null on a day with nothing scheduled, which is a day the app should still list —
 * the site being open is a fact of its own.
 */
data class OpeningDayUiModel(
    val id: String,
    val name: String,
    val window: String,
    val programme: String?,
)
