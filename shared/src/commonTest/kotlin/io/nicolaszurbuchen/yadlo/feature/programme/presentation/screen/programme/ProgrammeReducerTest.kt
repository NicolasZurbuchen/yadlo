package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ProgrammeReducerTest {
    private val reducer = ProgrammeStoreFactory.ReducerImpl

    @Test
    fun contentUpdated_nothingPickedYet_opensOnTheScopeTheExecutorChose() {
        val state = ProgrammeState(now = NOW)

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals(ProgrammeScopeState.Day("2026:sat"), result.selectedScope)
        assertEquals(content(), result.content)
    }

    @Test
    fun contentUpdated_theProgrammeHasJustDropped_opensOnTheCatalogue() {
        val state = ProgrammeState(now = NOW)

        val result =
            with(reducer) {
                state.reduce(contentUpdated(defaultScope = ProgrammeScopeState.Catalogue))
            }

        assertEquals(ProgrammeScopeState.Catalogue, result.selectedScope)
    }

    @Test
    fun contentUpdated_visitorAlreadyChoseTheCatalogue_leavesItAlone() {
        // The whole reason this is a start scope rather than a redirect. A content refresh arriving
        // while someone reads the Catalogue must not put them back on the timetable.
        val state = ProgrammeState(now = NOW, content = content(), selectedScope = ProgrammeScopeState.Catalogue)

        val result =
            with(reducer) {
                state.reduce(contentUpdated(defaultScope = ProgrammeScopeState.AllDays))
            }

        assertEquals(ProgrammeScopeState.Catalogue, result.selectedScope)
    }

    @Test
    fun contentUpdated_visitorAlreadyPickedADay_leavesItAlone() {
        // A refresh arriving while someone is reading Sunday must not throw them back to today.
        val state =
            ProgrammeState(now = NOW, content = content(), selectedScope = ProgrammeScopeState.Day("2026:sun"))

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals(ProgrammeScopeState.Day("2026:sun"), result.selectedScope)
    }

    @Test
    fun contentUpdated_pickedDayIsGoneFromTheNewContent_fallsBackRatherThanShowingNothing() {
        // A scope that has stopped existing, rather than a choice being overruled — the one case
        // the content is allowed to take back.
        val state =
            ProgrammeState(now = NOW, content = content(), selectedScope = ProgrammeScopeState.Day("2025:sat"))

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals(ProgrammeScopeState.Day("2026:sat"), result.selectedScope)
    }

    @Test
    fun contentUpdated_visitorIsOnTous_isNotADayAndSurvivesAnythingTheContentDoes() {
        val state = ProgrammeState(now = NOW, content = content(), selectedScope = ProgrammeScopeState.AllDays)

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals(ProgrammeScopeState.AllDays, result.selectedScope)
    }

    @Test
    fun scopeSelected_switchesTheListWithoutClearingTheFilters() {
        // The Category chips filter every scope, so carrying them across is the point: somebody who
        // narrowed the timetable to "sur l'eau" and switched to browsing still means sur l'eau.
        val state =
            ProgrammeState(
                now = NOW,
                selectedScope = ProgrammeScopeState.Day("2026:sat"),
                selectedCategoryIds = setOf("eau"),
            )

        val result = with(reducer) { state.reduce(ProgrammeMessage.ScopeSelected(ProgrammeScopeState.Catalogue)) }

        assertEquals(ProgrammeScopeState.Catalogue, result.selectedScope)
        assertEquals(setOf("eau"), result.selectedCategoryIds)
    }

    @Test
    fun ticked_advancesTheInstantEveryPillIsMeasuredAgainst_andTouchesNothingElse() {
        val state =
            ProgrammeState(
                now = NOW,
                content = content(),
                selectedScope = ProgrammeScopeState.Day("2026:sat"),
                selectedCategoryIds = setOf("musique"),
            )
        val later = Instant.parse("2026-07-11T16:01:00+02:00")

        val result = with(reducer) { state.reduce(ProgrammeMessage.Ticked(later)) }

        assertEquals(later, result.now)
        assertEquals(ProgrammeScopeState.Day("2026:sat"), result.selectedScope)
        assertEquals(setOf("musique"), result.selectedCategoryIds)
    }

    @Test
    fun scopeSelected_switchingDayDoesNotStartOver() {
        // Someone filtering for the children's corner wants it on Sunday too.
        val state =
            ProgrammeState(
                now = NOW,
                selectedScope = ProgrammeScopeState.Day("2026:sat"),
                selectedCategoryIds = setOf("enfants"),
            )

        val result =
            with(reducer) {
                state.reduce(ProgrammeMessage.ScopeSelected(ProgrammeScopeState.Day("2026:sun")))
            }

        assertEquals(ProgrammeScopeState.Day("2026:sun"), result.selectedScope)
        assertEquals(setOf("enfants"), result.selectedCategoryIds)
    }

    @Test
    fun categoriesChanged_replacesTheSelectionWholesale() {
        val state = ProgrammeState(now = NOW, selectedCategoryIds = setOf("musique"))

        val result = with(reducer) { state.reduce(ProgrammeMessage.CategoriesChanged(setOf("eau", "terre"))) }

        assertEquals(setOf("eau", "terre"), result.selectedCategoryIds)
    }

    private fun contentUpdated(defaultScope: ProgrammeScopeState = ProgrammeScopeState.Day("2026:sat")) =
        ProgrammeMessage.ContentUpdated(content = content(), defaultScope = defaultScope)

    private fun content() =
        ProgrammeContent(
            days = listOf(day("2026:sat", "Samedi"), day("2026:sun", "Dimanche")),
            categories = emptyList(),
            slots = emptyList(),
            catalogue = emptyList(),
            hasPublishedProgramme = true,
        )

    private fun day(
        id: String,
        name: String,
    ) = FestivalDay(
        id = id,
        name = name,
        date = "2026-07-11",
        start = Instant.parse("2026-07-11T12:00:00+02:00"),
        end = Instant.parse("2026-07-12T03:00:00+02:00"),
        provenance = Provenance.CONFIRMED,
    )

    private companion object {
        val NOW = Instant.parse("2026-07-11T16:00:00+02:00")
    }
}
