package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Horaires* — the one question this screen exists to answer, per day.
 *
 * **The programme window is deliberately not here.** The screen used to print, under each day, when
 * the first Slot started and the last one ended, plus a note explaining that a 10:00 yoga on a site
 * that opens at 12:00 is not a bug. That is three facts to answer one question. The domain still
 * derives the programme instants — see `OpeningDay` — and the day they earn a place back is the day
 * somebody asks for them rather than the day they were available.
 *
 * [caveat] stays: it appears when a day's window was derived rather than published, and it is the
 * difference between a timetable and a guess.
 */
data class HoursUiModel(
    val isLoading: Boolean,
    val days: List<OpeningDayUiModel>,
    val caveat: UiText?,
    val emptyMessage: UiText?,
)

/** [window] is the answer, and on this screen it is the whole of it. */
data class OpeningDayUiModel(
    val id: String,
    val name: String,
    val window: String,
)
