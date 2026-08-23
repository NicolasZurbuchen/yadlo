package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.time.Instant

sealed interface ProgrammeIntent {
    /**
     * A tap on the selector row — *Découvrir*, *Tous*, or one of the days.
     *
     * One intent for all five chips, because they are one exclusive choice rather than a view
     * switch sitting above a day filter. See [ProgrammeScopeUiModel].
     */
    data class ScopeSelected(
        val scope: ProgrammeScopeUiModel,
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
     * [defaultScope] travels with the content because it is read off it — what the screen opens on
     * is a question only the published days and the clock can answer, and the reducer has neither.
     */
    data class ContentUpdated(
        val content: ProgrammeContent,
        val defaultScope: ProgrammeScopeUiModel,
    ) : ProgrammeMessage

    data class Ticked(
        val now: Instant,
    ) : ProgrammeMessage

    data class ScopeSelected(
        val scope: ProgrammeScopeUiModel,
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
 * everything, which is what makes "deselect the last chip" fall back to the whole list.
 *
 * **[selectedScope] is null only until the first bundle arrives, and the content writes it exactly
 * once.** What the tab opens on is a start scope, not a redirect — the same distinction
 * `TabNavigator.selectStart` exists for, and for the same reason: ANNOUNCED turns into APPROACHING
 * at midnight on J-7, and somebody reading the Catalogue at 23:59 must not have the screen pulled
 * out from under them as the date turns. Every later emission finds it already set and leaves it
 * alone — the one exception being a chosen day the new content no longer publishes, which is a
 * scope that has stopped existing rather than a choice being overruled.
 */
data class ProgrammeState(
    val now: Instant,
    val content: ProgrammeContent? = null,
    val selectedScope: ProgrammeScopeUiModel? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
)
