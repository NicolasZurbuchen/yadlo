package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.MonYadloContent
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.PlannedDay
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.PlannedSlot
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_empty
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class MonYadloUiMapperTest {
    // region loading and empty

    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingAndSaysNothingElse() {
        val model = MonYadloState(now = NOW).toUiModel()

        assertTrue(model.isLoading)
        assertTrue(model.days.isEmpty())
        assertEquals(0, model.wishlistCount)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_readAndEmpty_pointsAtTheProgrammeRatherThanSpinning() {
        val model = state(MonYadloContent(days = emptyList(), wishlistCount = 0)).toUiModel()

        assertEquals(UiText.Resource(Res.string.mon_yadlo_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_anEmptyPlanWithAWishlist_stillSaysThePlanIsEmpty() {
        // The tile is the way to the other half and is drawn whatever its count, so an empty Plan
        // must not take it off the screen with it.
        val model = state(MonYadloContent(days = emptyList(), wishlistCount = 3)).toUiModel()

        assertEquals(UiText.Resource(Res.string.mon_yadlo_empty), model.emptyMessage)
        assertEquals(3, model.wishlistCount)
    }

    @Test
    fun toUiModel_withSomethingSaved_saysNothingAboutBeingEmpty() {
        assertNull(state(content()).toUiModel().emptyMessage)
    }

    // endregion

    // region the rail

    @Test
    fun toUiModel_theRail_writesTheDayNameAndItsDateWithoutTheYear() {
        val day = state(content()).toUiModel().days.single()

        assertEquals("Samedi", day.name)
        assertEquals("11.07", day.dateText)
    }

    @Test
    fun toUiModel_theRailDate_comesFromTheDayAndNotFromTheSlotOnIt() {
        // The Saturday runs to 02:00 and the only thing saved on it is a 01:00 set, which by the
        // calendar falls on the Sunday. A FestivalDay is a window, not a date — deriving the rail
        // from the Slot would file that set under a day the festival never programmed.
        val model = state(saturdayNightOnly()).toUiModel()

        assertEquals("11.07", model.days.single().dateText)
        assertEquals("01:00 – 02:30", model.days.single().rows.single().timeText)
    }

    // endregion

    // region rows

    @Test
    fun toUiModel_row_writesTheTimeOnceAsARange() {
        val row = state(content()).toUiModel().days.single().rows.single()

        assertEquals("20:30 – 22:00", row.timeText)
        assertEquals("Caesure", row.name)
    }

    @Test
    fun toUiModel_row_carriesTheHappeningIdBecauseTappingItOpensTheFiche() {
        assertEquals("caesure", state(content()).toUiModel().days.single().rows.single().happeningId)
    }

    @Test
    fun toUiModel_rowRunningNow_readsTheSameWordAsTheProgramme() {
        val model = state(content(), now = Instant.parse("2026-07-11T21:00:00+02:00")).toUiModel()

        assertEquals(UiText.Resource(Res.string.slot_state_running), model.days.single().rows.single().stateLabel)
    }

    @Test
    fun toUiModel_rowEnding_countsTheMinutesDown() {
        val model = state(content(), now = Instant.parse("2026-07-11T21:48:00+02:00")).toUiModel()

        assertEquals(
            UiText.Resource(Res.string.slot_state_ending, listOf("12")),
            model.days.single().rows.single().stateLabel,
        )
    }

    @Test
    fun toUiModel_rowOver_saysSoAndStays() {
        val model = state(content(), now = Instant.parse("2026-07-11T23:00:00+02:00")).toUiModel()

        // By the Sunday a Plan is mostly what you went to, and that is the point of this screen.
        assertEquals(1, model.days.single().rows.size)
        assertEquals(UiText.Resource(Res.string.slot_state_over), model.days.single().rows.single().stateLabel)
        assertEquals(SlotLiveStateUiModel.Over, model.days.single().rows.single().state)
    }

    @Test
    fun toUiModel_rowStartingWithinTheHour_countsInMinutes() {
        val model = state(content(), now = Instant.parse("2026-07-11T20:15:00+02:00")).toUiModel()

        assertEquals(
            UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("15")),
            model.days.single().rows.single().stateLabel,
        )
    }

    @Test
    fun toUiModel_rowAMinuteOutsideTheWindow_saysNothingYet() {
        // 19:29 against a 20:30 downbeat — one minute past the hour the countdown opens at.
        val model = state(content(), now = Instant.parse("2026-07-11T19:29:00+02:00")).toUiModel()

        assertNull(model.days.single().rows.single().stateLabel)
    }

    @Test
    fun toUiModel_rowFurtherOutThanTheCountdownWindow_saysNothingAtAll() {
        val model = state(content(), now = Instant.parse("2026-07-11T09:00:00+02:00")).toUiModel()

        assertNull(model.days.single().rows.single().stateLabel)
        assertEquals(SlotLiveStateUiModel.Upcoming, model.days.single().rows.single().state)
    }

    @Test
    fun toUiModel_daysAndRows_keepTheOrderTheDomainPutThemIn() {
        val model = state(twoDays()).toUiModel()

        assertEquals(listOf("2026:fri", "2026:sat"), model.days.map { it.id })
        assertEquals(listOf("DJ ALF"), model.days.first().rows.map { it.name })
    }

    // endregion

    private fun state(
        content: MonYadloContent,
        now: Instant = NOW,
    ) = MonYadloState(now = now, content = content)

    private fun content() =
        MonYadloContent(
            wishlistCount = 2,
            days = listOf(saturday()),
        )

    private fun twoDays() =
        MonYadloContent(
            wishlistCount = 0,
            days =
                listOf(
                    PlannedDay(
                        id = "2026:fri",
                        name = "Vendredi",
                        start = Instant.parse("2026-07-10T16:00:00+02:00"),
                        slots =
                            listOf(
                                PlannedSlot(
                                    id = "2026:dj-alf-fri",
                                    happeningId = "dj-alf",
                                    name = "DJ ALF",
                                    categoryId = "musique",
                                    categoryName = "Musique",
                                    start = Instant.parse("2026-07-10T17:00:00+02:00"),
                                    end = Instant.parse("2026-07-10T18:30:00+02:00"),
                                ),
                            ),
                    ),
                    saturday(),
                ),
        )

    private fun saturdayNightOnly() =
        MonYadloContent(
            wishlistCount = 0,
            days =
                listOf(
                    PlannedDay(
                        id = "2026:sat",
                        name = "Samedi",
                        start = Instant.parse("2026-07-11T12:00:00+02:00"),
                        slots =
                            listOf(
                                PlannedSlot(
                                    id = "2026:silent-party-sat",
                                    happeningId = "silent-party",
                                    name = "Silent Party",
                                    categoryId = "silent",
                                    categoryName = "Silent Party",
                                    start = Instant.parse("2026-07-12T01:00:00+02:00"),
                                    end = Instant.parse("2026-07-12T02:30:00+02:00"),
                                ),
                            ),
                    ),
                ),
        )

    private fun saturday() =
        PlannedDay(
            id = "2026:sat",
            name = "Samedi",
            start = Instant.parse("2026-07-11T12:00:00+02:00"),
            slots =
                listOf(
                    PlannedSlot(
                        id = "2026:caesure-sat",
                        happeningId = "caesure",
                        name = "Caesure",
                        categoryId = "musique",
                        categoryName = "Musique",
                        start = Instant.parse("2026-07-11T20:30:00+02:00"),
                        end = Instant.parse("2026-07-11T22:00:00+02:00"),
                    ),
                ),
        )

    private companion object {
        val NOW = Instant.parse("2026-07-11T15:00:00+02:00")
    }
}
