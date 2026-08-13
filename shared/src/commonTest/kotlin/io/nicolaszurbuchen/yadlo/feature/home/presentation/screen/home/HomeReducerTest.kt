package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class HomeReducerTest {
    private val reducer = HomeStoreFactory.ReducerImpl

    @Test
    fun contentUpdated_storesContentAndThePhaseDerivedFromIt() {
        val state = HomeState(now = NOW, phase = PhaseUiModel.OFF_SEASON)
        val content = homeContent()

        val result =
            with(reducer) {
                state.reduce(HomeMessage.ContentUpdated(content = content, phase = PhaseUiModel.ENDED))
            }

        assertEquals(content, result.content)
        assertEquals(PhaseUiModel.ENDED, result.phase)
    }

    @Test
    fun ticked_advancesTheClockAndThePhaseWithoutTouchingContent() {
        val content = homeContent()
        val state = HomeState(now = NOW, phase = PhaseUiModel.APPROACHING, content = content)
        val later = Instant.parse("2026-07-10T00:00:00+02:00")

        val result =
            with(reducer) {
                state.reduce(HomeMessage.Ticked(now = later, phase = PhaseUiModel.LIVE))
            }

        assertEquals(later, result.now)
        assertEquals(PhaseUiModel.LIVE, result.phase)
        assertEquals(content, result.content)
    }

    private fun homeContent() =
        HomeContent(
            editionName = "Yadlo 2026",
            days = emptyList(),
            hasPublishedProgramme = true,
            announcements = emptyList(),
            figures = emptyList(),
        )

    private companion object {
        val NOW = Instant.parse("2026-07-01T12:00:00+02:00")
    }
}
