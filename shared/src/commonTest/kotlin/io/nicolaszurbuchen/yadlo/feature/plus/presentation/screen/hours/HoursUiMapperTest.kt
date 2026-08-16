package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.OpeningDay
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_empty
import yadlo.shared.generated.resources.hours_estimated
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HoursUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoading() {
        val model = HoursState().toUiModel()

        assertTrue(model.isLoading)
        assertTrue(model.days.isEmpty())
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_readAndEmpty_saysTheHoursAreNotPublished() {
        assertEquals(
            UiText.Resource(Res.string.hours_empty),
            HoursState(days = emptyList()).toUiModel().emptyMessage,
        )
    }

    @Test
    fun toUiModel_theWindow_isWrittenAsARangeInTheFestivalsZone() {
        val model = HoursState(days = listOf(friday())).toUiModel()

        // 16:00 reads 16:00 for everyone on the beach, whatever their phone thinks the zone is.
        assertEquals("16:00 – 02:00", model.days.single().window)
    }

    @Test
    fun toUiModel_aWindowCrossingMidnight_readsAsOneNightRatherThanTwoDays() {
        val model = HoursState(days = listOf(saturday())).toUiModel()

        // Saturday closes at 03:00 on the Sunday. A FestivalDay is a window, so this is one line.
        assertEquals("12:00 – 03:00", model.days.single().window)
    }

    @Test
    fun toUiModel_oneDerivedDay_putsTheCaveatOnTheWholeScreen() {
        val model = HoursState(days = listOf(friday(), saturday().copy(hoursAreConfirmed = false))).toUiModel()

        assertEquals(UiText.Resource(Res.string.hours_estimated), model.caveat)
    }

    @Test
    fun toUiModel_everyDayPublished_carriesNoCaveat() {
        assertNull(HoursState(days = listOf(friday(), saturday())).toUiModel().caveat)
    }

    @Test
    fun toUiModel_daysKeepTheOrderTheDomainPutThemIn() {
        val model = HoursState(days = listOf(friday(), saturday())).toUiModel()

        assertEquals(listOf("Vendredi", "Samedi"), model.days.map { it.name })
    }

    private fun friday() =
        OpeningDay(
            id = "2026:fri",
            name = "Vendredi",
            opensAt = Instant.parse("2026-07-10T16:00:00+02:00"),
            closesAt = Instant.parse("2026-07-11T02:00:00+02:00"),
            firstStartsAt = Instant.parse("2026-07-10T17:00:00+02:00"),
            lastEndsAt = Instant.parse("2026-07-11T01:30:00+02:00"),
            hoursAreConfirmed = true,
        )

    private fun saturday() =
        OpeningDay(
            id = "2026:sat",
            name = "Samedi",
            opensAt = Instant.parse("2026-07-11T12:00:00+02:00"),
            closesAt = Instant.parse("2026-07-12T03:00:00+02:00"),
            firstStartsAt = Instant.parse("2026-07-11T10:00:00+02:00"),
            lastEndsAt = Instant.parse("2026-07-12T02:30:00+02:00"),
            hoursAreConfirmed = true,
        )
}
