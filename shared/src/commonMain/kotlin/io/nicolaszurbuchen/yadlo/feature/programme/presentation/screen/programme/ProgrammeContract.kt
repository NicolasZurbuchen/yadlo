package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.time.Instant

sealed interface ProgrammeIntent {
    /**
     * A tap on the selector row — *Découvrir*, *Tous*, or one of the days.
     *
     * One intent for all five chips, because they are one exclusive choice rather than a view
     * switch sitting above a day filter. See [ProgrammeScopeState].
     *
     * The id rather than the scope itself, so the Screen never names a Contract type — the same
     * arrangement [CategoryToggled] has always had, and the Executor resolves it the same way.
     */
    data class ScopeSelected(
        val scopeId: String,
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
        val defaultScope: ProgrammeScopeState,
    ) : ProgrammeMessage

    data class Ticked(
        val now: Instant,
    ) : ProgrammeMessage

    data class ScopeSelected(
        val scope: ProgrammeScopeState,
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
    val selectedScope: ProgrammeScopeState? = null,
    val selectedCategoryIds: Set<String> = emptySet(),
)

/**
 * What the tab is pointing at — *Découvrir · Tous · Vendredi · Samedi · Dimanche*.
 *
 * **One selection, not a view and a day.** These began as two controls stacked on each other: a
 * segmented toggle over a row of day chips. Three rows of chrome plus an axis left four bands above
 * the first row of an already short list, and the split was never real — every chip in the row
 * answers the same question, *what am I looking at*, and it happens that three of the answers are
 * days. Modelling it as one exclusive selection is what let the two rows become one.
 *
 * [Catalogue] is the odd one only in that it has no time in it at all. Tapping a day from there is
 * how you leave it, which is a job the day chips can do precisely because they cannot filter a
 * Catalogue: they are not filtering it, they are the way out.
 *
 * **It is in the Contract, and suffixed `State`, because that is the category it belongs to.** It
 * was called `ProgrammeScopeUiModel` and it is not one: a UiModel is what a Composable is handed,
 * and nothing renders this — [ScopeChipUiModel] is the rendered thing, a label and a selected flag.
 * Nor does it belong in the domain, which never sees it: no use case takes a scope, the content
 * arrives whole and the UiMapper filters it. What is left is the Store's own vocabulary, which is
 * exactly what a Contract holds and what [ProgrammeState] is the root of.
 */
sealed interface ProgrammeScopeState {
    /**
     * What the chip row is keyed by, and what a tap sends back — so the Screen can name a scope
     * without naming a Contract type. It was `toString()` at the LazyRow's key before, which is a
     * debugging aid standing in for an identity.
     */
    val id: String

    /** Every Happening the Edition offers, no hours, no day. */
    data object Catalogue : ProgrammeScopeState {
        override val id = "catalogue"
    }

    /** The whole weekend in one scroll, each day under its own sticky header. */
    data object AllDays : ProgrammeScopeState {
        override val id = "all-days"
    }

    /** A FestivalDay's own id is the scope's id: there is one scope per day and no other source. */
    data class Day(
        override val id: String,
    ) : ProgrammeScopeState

    companion object {
        /**
         * Anything that is not one of the two fixed scopes is a day, because those two are the
         * app's own words and every other id comes from the content. A day the content called
         * `catalogue` would select the Catalogue instead — a content bug rather than a resolution
         * one, and one the Edition-qualified ids make hard to write by accident.
         */
        fun forId(id: String): ProgrammeScopeState =
            when (id) {
                Catalogue.id -> Catalogue
                AllDays.id -> AllDays
                else -> Day(id)
            }
    }
}
