package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeSlot
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.programme_price_from
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_hours
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class ProgrammeUiMapperTest {
    // region loading and the two empties

    @Test
    fun toUiModel_noBundleYet_isLoadingAndSaysNothingElse() {
        val model = ProgrammeState(now = QUARTER_TO_FOUR).toUiModel()

        assertEquals(true, model.isLoading)
        assertNull(model.emptyMessage)
        assertTrue(model.rows.isEmpty())
    }

    @Test
    fun toUiModel_editionPublishedWithNoSlots_saysSoAndDropsTheDayChipsWithTheList() {
        val model =
            state(content = content(slots = emptyList()), selectedDayId = "2026:sat").toUiModel()

        // Offering three days to switch between when none of them has anything reads as a screen
        // that failed to load, rather than as a programme that is not out yet.
        assertTrue(model.days.isEmpty())
        assertTrue(model.categories.isEmpty())
        assertEquals(Res.string.programme_empty_unpublished, model.emptyMessage.resourceId())
    }

    @Test
    fun toUiModel_filterMatchesNothing_keepsTheChipsBecauseTheyAreTheWayOut() {
        val model =
            state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("enfants")).toUiModel()

        assertTrue(model.rows.isEmpty())
        assertEquals(Res.string.programme_empty_filter, model.emptyMessage.resourceId())
        assertTrue(model.days.isNotEmpty())
        assertTrue(model.categories.isNotEmpty())
    }

    // endregion

    // region chips

    @Test
    fun toUiModel_dayChips_markOnlyTheSelectedOne() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals(listOf(false, true), model.days.map { it.isSelected })
        assertEquals(listOf("Vendredi", "Samedi"), model.days.map { it.name })
    }

    @Test
    fun toUiModel_noCategoryChosen_leavesEveryChipUnselected() {
        // Empty is *Tout* — an absent filter, not a filter that excludes everything.
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertTrue(model.categories.none { it.isSelected })
        assertEquals(3, model.rows.size)
    }

    // endregion

    // region filtering

    @Test
    fun toUiModel_showsOnlyTheSelectedDay() {
        val model = state(selectedDayId = "2026:fri").toUiModel()

        assertEquals(listOf("2026:amc-fri"), model.rows.map { it.id })
    }

    @Test
    fun toUiModel_twoCategoriesChosen_showsBoth() {
        val model =
            state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("musique", "eau")).toUiModel()

        // The Silent Party is the third Saturday Slot and it is the one left out.
        assertEquals(listOf("2026:gladiasup-sat", "2026:dubside-sat"), model.rows.map { it.id })
    }

    // endregion

    // region what a row says

    @Test
    fun toUiModel_timeRange_isWrittenInTheFestivalsOwnZone() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals("16:00 – 18:00", model.rows.single { it.id == "2026:dubside-sat" }.timeText)
    }

    @Test
    fun toUiModel_slotRunningPastMidnight_readsAsTheSmallHoursRatherThanRollingPast24() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals("20:00 – 02:00", model.rows.single { it.id == "2026:silent-sat" }.timeText)
    }

    @Test
    fun toUiModel_categoryIsWrittenOutAsWellAsColoured() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.id == "2026:dubside-sat" }

        assertEquals("musique", row.categoryId)
        assertEquals("Musique", row.categoryName)
    }

    @Test
    fun toUiModel_tapTarget_isTheHappeningRatherThanTheSlot() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.id == "2026:dubside-sat" }

        assertEquals("dubside", row.happeningId)
    }

    // endregion

    // region price

    @Test
    fun toUiModel_artistSlot_showsNoPriceAtAll() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.id == "2026:dubside-sat" }

        assertNull(row.priceText)
    }

    @Test
    fun toUiModel_activityWithOneTariff_writesTheAmount() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.id == "2026:gladiasup-sat" }

        assertEquals(UiText.Raw("CHF 5"), row.priceText)
    }

    @Test
    fun toUiModel_activityWithSeveralTariffs_leadsWithTheLowest() {
        // CHF 25 adulte and CHF 15 moins de 16 ans. A row showing only the adult price prices a
        // family out of something they can afford.
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.id == "2026:silent-sat" }

        assertEquals(Res.string.programme_price_from, row.priceText.resourceId())
        assertEquals(listOf("CHF 15"), (row.priceText as UiText.Resource).args)
    }

    @Test
    fun toUiModel_freeActivity_saysSoRatherThanShowingNothing() {
        val row = state(selectedDayId = "2026:fri").toUiModel().rows.single { it.id == "2026:amc-fri" }

        assertEquals(Res.string.price_free, row.priceText.resourceId())
    }

    // endregion

    // region live state

    @Test
    fun toUiModel_moreThanFourHoursOut_saysNothing() {
        // Beyond the window the start time already says everything, and "dans 26h" is noise.
        val row = rowAt(Instant.parse("2026-07-11T11:00:00+02:00"), "2026:dubside-sat")

        assertEquals(SlotLiveStateUiModel.Upcoming, row.state)
        assertNull(row.stateLabel)
    }

    @Test
    fun toUiModel_insideTheWindowButHoursOut_countsInWholeHours() {
        val row = rowAt(Instant.parse("2026-07-11T13:30:00+02:00"), "2026:dubside-sat")

        assertEquals(Res.string.slot_state_starts_in_hours, row.stateLabel.resourceId())
        // Floored: at 2h30 the answer someone wants is "not for a while".
        assertEquals(listOf("2"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_minutesOut_countsInMinutes() {
        val row = rowAt(QUARTER_TO_FOUR, "2026:dubside-sat")

        assertEquals(Res.string.slot_state_starts_in_minutes, row.stateLabel.resourceId())
        assertEquals(listOf("15"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_secondsOut_neverSaysDansZeroMin() {
        val row = rowAt(Instant.parse("2026-07-11T15:59:30+02:00"), "2026:dubside-sat")

        // It still has not started, and one is the smallest true thing to say.
        assertEquals(listOf("1"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_running_saysEnCoursAndCarriesHowFarThrough() {
        val row = rowAt(Instant.parse("2026-07-11T17:00:00+02:00"), "2026:dubside-sat")

        assertEquals(Res.string.slot_state_running, row.stateLabel.resourceId())
        assertEquals(0.5f, assertIs<SlotLiveStateUiModel.Running>(row.state).progress)
    }

    @Test
    fun toUiModel_inTheLastTwentyMinutes_warnsAndKeepsTheProgress() {
        val row = rowAt(Instant.parse("2026-07-11T17:45:00+02:00"), "2026:dubside-sat")

        assertEquals(Res.string.slot_state_ending, row.stateLabel.resourceId())
        assertEquals(listOf("15"), (row.stateLabel as UiText.Resource).args)
        assertEquals(0.875f, assertIs<SlotLiveStateUiModel.Ending>(row.state).progress)
    }

    @Test
    fun toUiModel_atTheEndInstant_isAlreadyOver() {
        val row = rowAt(Instant.parse("2026-07-11T18:00:00+02:00"), "2026:dubside-sat")

        assertEquals(SlotLiveStateUiModel.Over, row.state)
        assertEquals(Res.string.slot_state_over, row.stateLabel.resourceId())
    }

    @Test
    fun toUiModel_sevenHourActivityAndATwoHourSet_readIdenticallyWhileBothAreOn() {
        // DECISIONS.md § One live state for every Slot: a seven-hour open activity and a two-hour
        // DJ set say the same thing, and only the fill behind them differs.
        val rows = state(now = Instant.parse("2026-07-11T17:00:00+02:00"), selectedDayId = "2026:sat").toUiModel().rows

        val activity = rows.single { it.id == "2026:gladiasup-sat" }
        val set = rows.single { it.id == "2026:dubside-sat" }

        assertEquals(Res.string.slot_state_running, activity.stateLabel.resourceId())
        assertEquals(Res.string.slot_state_running, set.stateLabel.resourceId())
    }

    // endregion

    // region the day's axis

    @Test
    fun toUiModel_scale_readsFromTheFirstThingOnToTheHourTheSiteCloses() {
        // Saturday's window is 12:00 to 03:00, and GladiaSUP is the earliest Slot at 12:00.
        val scale = state(selectedDayId = "2026:sat").toUiModel().scale

        assertEquals("12:00", scale?.startText)
        assertEquals("19:30", scale?.middleText)
        assertEquals("03:00", scale?.endText)
    }

    @Test
    fun toUiModel_slotStartingBeforeTheSiteOpens_widensTheAxisRatherThanFallingOffIt() {
        // The beach at Préverenges is public, so the morning yoga runs from 10:00 on a day the
        // site opens at 12:00. Its bar has to start at zero, not at a negative fraction.
        val model =
            state(content = content(slots = saturdaySlots() + morningYoga()), selectedDayId = "2026:sat").toUiModel()

        assertEquals("10:00", model.scale?.startText)
        assertEquals(0f, model.rows.first { it.id == "2026:yoga-sat" }.barStart)
    }

    @Test
    fun toUiModel_everyRowCarriesItsPlaceOnTheDay_finishedOnesIncluded() {
        // The bar is where the shape of the day lives — what overlaps what, how much of the
        // afternoon something covers. A row that dropped it on ending would take that with it.
        val rows = state(now = Instant.parse("2026-07-12T02:59:00+02:00"), selectedDayId = "2026:sat").toUiModel().rows

        assertTrue(rows.all { it.state == SlotLiveStateUiModel.Over })
        assertTrue(rows.all { it.barEnd > it.barStart })
    }

    @Test
    fun toUiModel_slotRunningToTheEndOfTheAxis_reachesTheFarSide() {
        // The Silent Party closes at 02:00 and Saturday's window runs to 03:00, so it stops short.
        val row = rowAt(QUARTER_TO_FOUR, "2026:silent-sat")

        // 12:00 to 03:00 is a fifteen-hour axis; 20:00 is eight hours in and 02:00 is fourteen.
        assertEquals(0.533f, row.barStart, ONE_PIXEL_ON_A_PHONE)
        assertEquals(0.933f, row.barEnd, ONE_PIXEL_ON_A_PHONE)
    }

    @Test
    fun toUiModel_filteringDoesNotRescaleTheAxis() {
        // Two rows compared across a filter change have to sit at the same place on the bar, so
        // the axis is measured over every Slot of the day rather than the ones left showing.
        val unfiltered = state(selectedDayId = "2026:sat").toUiModel()
        val filtered = state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("musique")).toUiModel()

        assertEquals(unfiltered.scale, filtered.scale)
        assertEquals(
            unfiltered.rows.single { it.id == "2026:dubside-sat" }.barStart,
            filtered.rows.single { it.id == "2026:dubside-sat" }.barStart,
        )
    }

    // endregion

    private fun rowAt(
        now: Instant,
        rowId: String,
    ) = state(now = now, selectedDayId = "2026:sat").toUiModel().rows.single { it.id == rowId }

    private fun state(
        now: Instant = QUARTER_TO_FOUR,
        content: ProgrammeContent = content(),
        selectedDayId: String? = null,
        selectedCategoryIds: Set<String> = emptySet(),
    ) = ProgrammeState(
        now = now,
        content = content,
        selectedDayId = selectedDayId,
        selectedCategoryIds = selectedCategoryIds,
    )

    private fun content(slots: List<ProgrammeSlot> = saturdaySlots() + amc()) =
        ProgrammeContent(
            days =
                listOf(
                    day("2026:fri", "Vendredi", "2026-07-10T16:00:00+02:00", "2026-07-11T02:00:00+02:00"),
                    day("2026:sat", "Samedi", "2026-07-11T12:00:00+02:00", "2026-07-12T03:00:00+02:00"),
                ),
            categories =
                listOf(
                    Category(id = "musique", name = "Musique", order = 1),
                    Category(id = "silent", name = "Silent Party", order = 2),
                    Category(id = "eau", name = "Sur l'eau", order = 3),
                ),
            slots = slots,
        )

    private fun day(
        id: String,
        name: String,
        start: String,
        end: String,
    ) = FestivalDay(
        id = id,
        name = name,
        date = start.substringBefore('T'),
        start = Instant.parse(start),
        end = Instant.parse(end),
        provenance = Provenance.CONFIRMED,
    )

    /**
     * Outside Saturday's window on purpose: the site opens at 12:00 and the beach is public, so the
     * yoga is the case the axis has to widen for.
     */
    private fun morningYoga() =
        listOf(
            slot(
                id = "2026:yoga-sat",
                happeningId = "yoga",
                name = "Acro-yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                start = "2026-07-11T10:00:00+02:00",
                end = "2026-07-11T11:00:00+02:00",
                price = null,
            ),
        )

    /** In the order the UseCase hands them over: by start, then by the shorter of two. */
    private fun saturdaySlots() =
        listOf(
            slot(
                id = "2026:gladiasup-sat",
                happeningId = "gladiasup",
                name = "GladiaSUP",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                start = "2026-07-11T12:00:00+02:00",
                end = "2026-07-11T19:00:00+02:00",
                price = tariff(5.0),
            ),
            slot(
                id = "2026:dubside-sat",
                happeningId = "dubside",
                name = "Dubside",
                categoryId = "musique",
                categoryName = "Musique",
                start = "2026-07-11T16:00:00+02:00",
                end = "2026-07-11T18:00:00+02:00",
                price = null,
            ),
            slot(
                id = "2026:silent-sat",
                happeningId = "silent-party",
                name = "Silent Party",
                categoryId = "silent",
                categoryName = "Silent Party",
                start = "2026-07-11T20:00:00+02:00",
                end = "2026-07-12T02:00:00+02:00",
                price =
                    Price(
                        free = false,
                        tiers =
                            listOf(
                                Price.Tier(label = "Adulte", amount = Money(25.0, "CHF"), per = null),
                                Price.Tier(label = "Moins de 16 ans", amount = Money(15.0, "CHF"), per = null),
                            ),
                        deposit = null,
                        provenance = Provenance.CONFIRMED,
                    ),
            ),
        )

    private fun amc() =
        listOf(
            slot(
                id = "2026:amc-fri",
                happeningId = "amc",
                name = "AMC",
                categoryId = "musique",
                categoryName = "Musique",
                start = "2026-07-10T21:30:00+02:00",
                end = "2026-07-10T22:30:00+02:00",
                price = Price(free = true, tiers = emptyList(), deposit = null, provenance = Provenance.CONFIRMED),
                dayId = "2026:fri",
            ),
        )

    private fun tariff(amount: Double) =
        Price(
            free = false,
            tiers = listOf(Price.Tier(label = null, amount = Money(amount, "CHF"), per = null)),
            deposit = null,
            provenance = Provenance.CONFIRMED,
        )

    private fun slot(
        id: String,
        happeningId: String,
        name: String,
        categoryId: String,
        categoryName: String,
        start: String,
        end: String,
        price: Price?,
        dayId: String = "2026:sat",
    ) = ProgrammeSlot(
        id = id,
        dayId = dayId,
        happeningId = happeningId,
        name = name,
        categoryId = categoryId,
        categoryName = categoryName,
        start = Instant.parse(start),
        end = Instant.parse(end),
        price = price,
    )

    private fun UiText?.resourceId() = (this as? UiText.Resource)?.id

    private companion object {
        /** The moment the prototype was argued from: Dubside fifteen minutes out. */
        val QUARTER_TO_FOUR = Instant.parse("2026-07-11T15:45:00+02:00")

        /** Roughly a pixel of a 360dp bar. Tighter than that is asserting the arithmetic twice. */
        const val ONE_PIXEL_ON_A_PHONE = 0.003f
    }
}
