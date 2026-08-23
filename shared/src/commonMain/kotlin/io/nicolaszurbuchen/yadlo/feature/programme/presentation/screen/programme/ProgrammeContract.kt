package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.time.Instant

sealed interface ProgrammeIntent {
    /**
     * Which of the two lists the tab is showing — the timetable, or the Catalogue.
     *
     * Selected rather than toggled, unlike the Category chips: there are exactly two, the control
     * names both of them, and "the other one" is never what the reader is reaching for.
     */
    data class ViewSelected(
        val view: ProgrammeViewUiModel,
    ) : ProgrammeIntent

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
     * neither. [defaultView] rides along for the same reason and off the same two inputs: the view
     * the tab opens on follows the Phase, and a Phase is the published days plus the clock.
     */
    data class ContentUpdated(
        val content: ProgrammeContent,
        val defaultDayId: String?,
        val defaultView: ProgrammeViewUiModel,
    ) : ProgrammeMessage

    data class ViewSelected(
        val view: ProgrammeViewUiModel,
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
 *
 * **[selectedView] is null only until the first bundle arrives, and the content writes it exactly
 * once.** The view the tab opens on is a start view, not a redirect — the same distinction
 * `TabNavigator.selectStart` exists for, and for the same reason: ANNOUNCED turns into APPROACHING
 * at midnight on J-7, and somebody reading the Catalogue at 23:59 must not have the screen pulled
 * out from under them as the date turns. Every later emission finds it already set and leaves it
 * alone; after that only a tap on the toggle moves it.
 */
data class ProgrammeState(
    val now: Instant,
    val content: ProgrammeContent? = null,
    val selectedView: ProgrammeViewUiModel? = null,
    val selectedDayId: String? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
)
