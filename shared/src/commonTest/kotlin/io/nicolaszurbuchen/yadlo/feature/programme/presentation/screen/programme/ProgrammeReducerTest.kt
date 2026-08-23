package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class ProgrammeReducerTest {
    private val reducer = ProgrammeStoreFactory.ReducerImpl

    @Test
    fun contentUpdated_nothingPickedYet_opensOnTheDayTheExecutorChose() {
        val state = ProgrammeState(now = NOW)

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals("2026:sat", result.selectedDayId)
        assertEquals(content(), result.content)
    }

    @Test
    fun contentUpdated_nothingPickedYet_opensOnTheViewTheExecutorChose() {
        val state = ProgrammeState(now = NOW)

        val result =
            with(reducer) {
                state.reduce(contentUpdated(defaultView = ProgrammeViewUiModel.CATALOGUE))
            }

        assertEquals(ProgrammeViewUiModel.CATALOGUE, result.selectedView)
    }

    @Test
    fun contentUpdated_visitorAlreadyChoseAView_leavesItAlone() {
        // The whole reason this is a start view rather than a redirect. A content refresh arriving
        // while someone reads the Catalogue must not put them back on the timetable.
        val state = ProgrammeState(now = NOW, content = content(), selectedView = ProgrammeViewUiModel.CATALOGUE)

        val result =
            with(reducer) {
                state.reduce(contentUpdated(defaultView = ProgrammeViewUiModel.PROGRAMME))
            }

        assertEquals(ProgrammeViewUiModel.CATALOGUE, result.selectedView)
    }

    @Test
    fun viewSelected_switchesTheViewWithoutClearingTheFilters() {
        // The Category chips filter both views, so carrying them across is the point: somebody who
        // narrowed the timetable to "sur l'eau" and switched to browsing still means sur l'eau.
        val state =
            ProgrammeState(
                now = NOW,
                selectedView = ProgrammeViewUiModel.PROGRAMME,
                selectedDayId = "2026:sat",
                selectedCategoryIds = setOf("eau"),
            )

        val result = with(reducer) { state.reduce(ProgrammeMessage.ViewSelected(ProgrammeViewUiModel.CATALOGUE)) }

        assertEquals(ProgrammeViewUiModel.CATALOGUE, result.selectedView)
        assertEquals(setOf("eau"), result.selectedCategoryIds)
        // Kept rather than cleared: switching back has to land where it was left.
        assertEquals("2026:sat", result.selectedDayId)
    }

    @Test
    fun contentUpdated_visitorAlreadyPickedADay_leavesItAlone() {
        // A refresh arriving while someone is reading Sunday must not throw them back to today.
        val state = ProgrammeState(now = NOW, content = content(), selectedDayId = "2026:sun")

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals("2026:sun", result.selectedDayId)
    }

    @Test
    fun contentUpdated_pickedDayIsGoneFromTheNewContent_fallsBackRatherThanShowingNothing() {
        val state = ProgrammeState(now = NOW, content = content(), selectedDayId = "2025:sat")

        val result = with(reducer) { state.reduce(contentUpdated()) }

        assertEquals("2026:sat", result.selectedDayId)
    }

    @Test
    fun ticked_advancesTheInstantEveryPillIsMeasuredAgainst_andTouchesNothingElse() {
        val state =
            ProgrammeState(
                now = NOW,
                content = content(),
                selectedDayId = "2026:sat",
                selectedCategoryIds = setOf("musique"),
            )
        val later = Instant.parse("2026-07-11T16:01:00+02:00")

        val result = with(reducer) { state.reduce(ProgrammeMessage.Ticked(later)) }

        assertEquals(later, result.now)
        assertEquals("2026:sat", result.selectedDayId)
        assertEquals(setOf("musique"), result.selectedCategoryIds)
    }

    @Test
    fun daySelected_switchesTheDayWithoutClearingTheFilters() {
        // Switching day is not starting over: someone filtering for the children's corner wants it
        // on Sunday too.
        val state = ProgrammeState(now = NOW, selectedDayId = "2026:sat", selectedCategoryIds = setOf("enfants"))

        val result = with(reducer) { state.reduce(ProgrammeMessage.DaySelected("2026:sun")) }

        assertEquals("2026:sun", result.selectedDayId)
        assertEquals(setOf("enfants"), result.selectedCategoryIds)
    }

    @Test
    fun categoriesChanged_replacesTheSelectionWholesale() {
        val state = ProgrammeState(now = NOW, selectedCategoryIds = setOf("musique"))

        val result = with(reducer) { state.reduce(ProgrammeMessage.CategoriesChanged(setOf("eau", "terre"))) }

        assertEquals(setOf("eau", "terre"), result.selectedCategoryIds)
    }

    private fun contentUpdated(
        defaultDayId: String? = "2026:sat",
        defaultView: ProgrammeViewUiModel = ProgrammeViewUiModel.PROGRAMME,
    ) = ProgrammeMessage.ContentUpdated(
        content = content(),
        defaultDayId = defaultDayId,
        defaultView = defaultView,
    )

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
