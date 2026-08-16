package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.OpeningDay

/** Empty: the screen answers one question and nothing on it can be operated. */
sealed interface HoursIntent

/** Empty: nothing here leaves the app. */
sealed interface HoursLabel

sealed interface HoursAction {
    data object ObserveDays : HoursAction
}

sealed interface HoursMessage {
    data class DaysUpdated(
        val days: List<OpeningDay>,
    ) : HoursMessage
}

/**
 * **No clock.** Every other screen that writes a time ticks against one; this one writes a
 * timetable, which is the same at 15:00 as it is at 02:00. Highlighting "today" would be the first
 * thing to want here and would also be the first thing to get wrong — a FestivalDay is a window
 * that crosses midnight, so "today" is not a calendar date.
 */
data class HoursState(
    val days: List<OpeningDay>? = null,
)
