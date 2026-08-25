package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.mapper

import io.nicolaszurbuchen.yadlo.feature.home.domain.model.SiteMoment
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.SiteMomentUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class SiteMomentUiMapperTest {
    @Test
    fun toUiModel_beforeTheFirstDay_carriesTheOpeningInstant() {
        assertEquals(SiteMomentUiModel.BeforeFirstDay(INSTANT), SiteMoment.BeforeFirstDay(INSTANT).toUiModel())
    }

    @Test
    fun toUiModel_open_carriesTheClosingInstant() {
        assertEquals(SiteMomentUiModel.Open(INSTANT), SiteMoment.Open(INSTANT).toUiModel())
    }

    @Test
    fun toUiModel_closed_carriesTheReopeningInstant() {
        assertEquals(SiteMomentUiModel.Closed(INSTANT), SiteMoment.Closed(INSTANT).toUiModel())
    }

    @Test
    fun toUiModel_finished_hasNothingToCarry() {
        assertEquals(SiteMomentUiModel.Finished, SiteMoment.Finished.toUiModel())
    }

    @Test
    fun toUiModel_passesTheInstantThroughRatherThanFormattingIt() {
        // The zone is the mapper's business, not this one's: HomeUiMapper renders "16:00" beside
        // every other time on the screen. A conversion that formatted here would put one time on
        // Accueil in a different zone from the rest by the time anyone noticed.
        val opensAt = Instant.parse("2026-07-10T14:00:00Z")

        assertEquals(opensAt, (SiteMoment.BeforeFirstDay(opensAt).toUiModel() as SiteMomentUiModel.BeforeFirstDay).opensAt)
    }

    private companion object {
        val INSTANT = Instant.parse("2026-07-10T16:00:00Z")
    }
}
