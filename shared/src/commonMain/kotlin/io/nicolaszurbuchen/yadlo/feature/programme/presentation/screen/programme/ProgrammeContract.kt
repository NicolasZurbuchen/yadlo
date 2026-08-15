package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.time.Instant

sealed interface ProgrammeIntent {
    data class DaySelected(
        val dayId: String,
    ) : ProgrammeIntent

    /**
     * Toggled, not selected: the chips are a multi-select — "musique et enfants" is the question a
     * parent at a music festival actually has.
     */
    data class CategoryToggled(
        val categoryId: String,
    ) : ProgrammeIntent

    data object AllCategoriesSelected : ProgrammeIntent

    /**
     * You tap a Slot and you land on the Happening's fiche. There is no screen for a single Slot:
     * an activity running three days has one description, one price and one photograph.
     */
    data class SlotClicked(
        val happeningId: String,
    ) : ProgrammeIntent
}

sealed interface ProgrammeLabel {
    data class NavigateToHappening(
        val happeningId: String,
    ) : ProgrammeLabel
}

sealed interface ProgrammeAction {
    data object ObserveContent : ProgrammeAction

    data object StartTicking : ProgrammeAction
}

sealed interface ProgrammeMessage {
    /**
     * [defaultDayId] travels with the content because it is read off it — which day the screen opens
     * on is a question only the days themselves and the clock can answer, and the reducer has
     * neither.
     */
    data class ContentUpdated(
        val content: ProgrammeContent,
        val defaultDayId: String?,
    ) : ProgrammeMessage

    data class Ticked(
        val now: Instant,
    ) : ProgrammeMessage

    data class DaySelected(
        val dayId: String,
    ) : ProgrammeMessage

    data class CategoriesChanged(
        val categoryIds: Set<String>,
    ) : ProgrammeMessage
}

/**
 * [now] is a field rather than a call at render time for the same reason as on Accueil: every pill
 * and every progress bar on screen has to be measured against one reading of the clock, and no part
 * of the app asks a clock it does not own.
 *
 * [selectedCategoryIds] empty means *Tout* — an absent filter rather than a filter that excludes
 * everything, which is what makes "deselect the last chip" fall back to the whole day.
 */
data class ProgrammeState(
    val now: Instant,
    val content: ProgrammeContent? = null,
    val selectedDayId: String? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
)
