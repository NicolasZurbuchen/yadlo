package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.CatalogueEntry
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeSlot
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.price_from
import yadlo.shared.generated.resources.programme_empty_filter
import yadlo.shared.generated.resources.programme_empty_unpublished
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
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
    fun toUiModel_editionPublishedWithNoSlots_saysSoAndDropsTheWholeSelectorRowWithTheList() {
        val model =
            state(content = content(slots = emptyList()), selectedDayId = "2026:sat").toUiModel()

        // Offering five things to point at when none of them has anything reads as a screen that
        // failed to load, rather than as a programme that is not out yet.
        assertTrue(model.scopes.isEmpty())
        assertTrue(model.categories.isEmpty())
        assertEquals(Res.string.programme_empty_unpublished, model.emptyMessage.resourceId())
    }

    @Test
    fun toUiModel_filterMatchesNothing_keepsTheChipsBecauseTheyAreTheWayOut() {
        val model =
            state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("enfants")).toUiModel()

        assertTrue(model.rows.isEmpty())
        assertEquals(Res.string.programme_empty_filter, model.emptyMessage.resourceId())
        assertTrue(model.scopes.isNotEmpty())
        assertTrue(model.categories.isNotEmpty())
    }

    // endregion

    // region chips

    @Test
    fun toUiModel_selectorRow_readsDecouvrirThenTousThenTheDaysInOrder() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        // Widest to narrowest, with the one that is not a day at the head of the row so it cannot
        // read as a fourth one.
        assertEquals(
            listOf(
                ProgrammeScopeState.Catalogue.id,
                ProgrammeScopeState.AllDays.id,
                "2026:fri",
                "2026:sat",
            ),
            model.scopes.map { it.id },
        )
    }

    @Test
    fun toUiModel_selectorRow_marksOnlyTheOneInScope() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals(
            listOf("2026:sat"),
            model.scopes.filter { it.isSelected }.map { it.id },
        )
    }

    @Test
    fun toUiModel_dayLabels_comeOutOfTheContentAndAreShortenedToFitTheRow() {
        // The name is the content's, so a day the association calls something else keeps it and
        // nothing here has to translate a weekday. Three letters because the row holds five chips.
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals(
            listOf("Ven", "Sam"),
            model.scopes.mapNotNull { (it.label as? UiText.Raw)?.value },
        )
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

        assertEquals(listOf("amc"), model.rows.map { it.happeningId })
    }

    @Test
    fun toUiModel_twoCategoriesChosen_showsBoth() {
        val model =
            state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("musique", "eau")).toUiModel()

        // The Silent Party is the third Saturday Slot and it is the one left out.
        assertEquals(listOf("gladiasup", "dubside"), model.rows.map { it.happeningId })
    }

    // endregion

    // region what a row says

    @Test
    fun toUiModel_timeRange_isWrittenInTheFestivalsOwnZone() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals("16:00 – 18:00", model.rows.single { it.happeningId == "dubside" }.slots.single().timeText)
    }

    @Test
    fun toUiModel_slotRunningPastMidnight_readsAsTheSmallHoursRatherThanRollingPast24() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals(
            "20:00 – 02:00",
            model.rows.single { it.happeningId == "silent-party" }.slots.single().timeText,
        )
    }

    @Test
    fun toUiModel_categoryIsWrittenOutAsWellAsColoured() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.happeningId == "dubside" }

        assertEquals("musique", row.categoryId)
        assertEquals("Musique", row.categoryName)
    }

    @Test
    fun toUiModel_tapTarget_isTheHappeningRatherThanTheSlot() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.happeningId == "dubside" }

        assertEquals("dubside", row.happeningId)
    }

    // endregion

    // region price

    @Test
    fun toUiModel_artistSlot_showsNoPriceAtAll() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.happeningId == "dubside" }

        assertNull(row.priceText)
    }

    @Test
    fun toUiModel_activityWithOneTariff_writesTheAmount() {
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.happeningId == "gladiasup" }

        assertEquals(UiText.Raw("CHF 5"), row.priceText)
    }

    @Test
    fun toUiModel_activityWithSeveralTariffs_leadsWithTheLowest() {
        // CHF 25 adulte and CHF 15 moins de 16 ans. A row showing only the adult price prices a
        // family out of something they can afford.
        val row = state(selectedDayId = "2026:sat").toUiModel().rows.single { it.happeningId == "silent-party" }

        assertEquals(Res.string.price_from, row.priceText.resourceId())
        assertEquals(listOf("CHF 15"), (row.priceText as UiText.Resource).args)
    }

    @Test
    fun toUiModel_freeActivity_saysSoRatherThanShowingNothing() {
        val row = state(selectedDayId = "2026:fri").toUiModel().rows.single { it.happeningId == "amc" }

        assertEquals(Res.string.price_free, row.priceText.resourceId())
    }

    // endregion

    // region live state

    @Test
    fun toUiModel_hoursOut_saysNothing() {
        // Beyond the window the start time already says everything, and "dans 5h" is noise.
        val row = rowAt(Instant.parse("2026-07-11T11:00:00+02:00"), "dubside")

        assertEquals(SlotLiveStateUiModel.Upcoming, row.state)
        assertNull(row.stateLabel)
    }

    @Test
    fun toUiModel_aMinuteOutsideTheWindow_saysNothingYet() {
        // 14:59 against a 16:00 downbeat. The boundary is the only thing separating a pill from no
        // pill on a row whose start time has not changed, so it is asserted rather than inferred.
        val row = rowAt(Instant.parse("2026-07-11T14:59:00+02:00"), "dubside")

        assertEquals(SlotLiveStateUiModel.Upcoming, row.state)
        assertNull(row.stateLabel)
    }

    @Test
    fun toUiModel_minutesOut_countsInMinutes() {
        val row = rowAt(QUARTER_TO_FOUR, "dubside")

        assertEquals(Res.string.slot_state_starts_in_minutes, row.stateLabel.resourceId())
        assertEquals(listOf("15"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_secondsOut_neverSaysDansZeroMin() {
        val row = rowAt(Instant.parse("2026-07-11T15:59:30+02:00"), "dubside")

        // It still has not started, and one is the smallest true thing to say.
        assertEquals(listOf("1"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_running_saysEnCoursAndCarriesHowFarThrough() {
        val row = rowAt(Instant.parse("2026-07-11T17:00:00+02:00"), "dubside")

        assertEquals(Res.string.slot_state_running, row.stateLabel.resourceId())
        assertEquals(0.5f, assertIs<SlotLiveStateUiModel.Running>(row.state).progress)
    }

    @Test
    fun toUiModel_inTheLastTwentyMinutes_warnsAndKeepsTheProgress() {
        val row = rowAt(Instant.parse("2026-07-11T17:45:00+02:00"), "dubside")

        assertEquals(Res.string.slot_state_ending, row.stateLabel.resourceId())
        assertEquals(listOf("15"), (row.stateLabel as UiText.Resource).args)
        assertEquals(0.875f, assertIs<SlotLiveStateUiModel.Ending>(row.state).progress)
    }

    @Test
    fun toUiModel_atTheEndInstant_isAlreadyOver() {
        val row = rowAt(Instant.parse("2026-07-11T18:00:00+02:00"), "dubside")

        assertEquals(SlotLiveStateUiModel.Over, row.state)
        assertEquals(Res.string.slot_state_over, row.stateLabel.resourceId())
    }

    @Test
    fun toUiModel_sevenHourActivityAndATwoHourSet_readIdenticallyWhileBothAreOn() {
        // DECISIONS.md § One live state for every Slot: a seven-hour open activity and a two-hour
        // DJ set say the same thing, and only the fill behind them differs.
        val rows = state(now = Instant.parse("2026-07-11T17:00:00+02:00"), selectedDayId = "2026:sat").toUiModel().rows

        val activity = rows.single { it.happeningId == "gladiasup" }
        val set = rows.single { it.happeningId == "dubside" }

        assertEquals(Res.string.slot_state_running, activity.stateLabel.resourceId())
        assertEquals(Res.string.slot_state_running, set.stateLabel.resourceId())
    }

    // endregion

    // region a row is a Happening on a day

    @Test
    fun toUiModel_aHappeningRunningSeveralTimesInADay_isOneRowWithEveryHourOnIt() {
        val model = mergedSaturday()

        // Three rows with one name, one price and one photograph said these were three activities
        // rather than three chances at one.
        assertEquals(
            listOf("14:00 – 15:00", "16:00 – 17:00", "18:00 – 19:00"),
            model.rows.single { it.happeningId == "sup-yoga" }.slots.map { it.timeText },
        )
    }

    @Test
    fun toUiModel_eachHourOfAMergedRow_keepsItsOwnPlaceOnTheDay() {
        // One track carrying three marks is the clearest thing the merge does — a segment per hour,
        // never one spanning 14:00 to 19:00, which would claim five hours of the afternoon.
        val row = mergedSaturday().rows.single { it.happeningId == "sup-yoga" }

        // A fifteen-hour axis from 12:00: 14:00 is two hours in, 18:00 is six.
        assertEquals(0.133f, row.slots.first().barStart, ONE_PIXEL_ON_A_PHONE)
        assertEquals(0.4f, row.slots.last().barStart, ONE_PIXEL_ON_A_PHONE)
    }

    @Test
    fun toUiModel_oneHourRunning_takesTheRowOverTheOneThatIsFinished() {
        // 16:30: the 14:00 is over, the 16:00 is running, the 18:00 has not started. The row has
        // exactly one useful thing to say and it is the live one.
        val row =
            rowAt(
                now = Instant.parse("2026-07-11T16:30:00+02:00"),
                happeningId = "sup-yoga",
                slots = saturdayWithSupYoga(),
            )

        assertEquals(Res.string.slot_state_running, row.stateLabel.resourceId())
        // And the hour that has gone still says so on its own, which is what dims it in the row.
        assertEquals(SlotLiveStateUiModel.Over, row.slots.first().state)
    }

    @Test
    fun toUiModel_oneHourFinishedAndAnotherStillToCome_doesNotSayTermine() {
        // 15:30, between the first and the second. Saying "terminé" here hides the rest of the
        // afternoon, which is the one thing about a merged row a reader could call a lie.
        val row =
            rowAt(
                now = Instant.parse("2026-07-11T15:30:00+02:00"),
                happeningId = "sup-yoga",
                slots = saturdayWithSupYoga(),
            )

        assertEquals(Res.string.slot_state_starts_in_minutes, row.stateLabel.resourceId())
        assertEquals(listOf("30"), (row.stateLabel as UiText.Resource).args)
    }

    @Test
    fun toUiModel_everyHourFinished_isTheOnlyWayARowSaysTermine() {
        val row =
            rowAt(
                now = Instant.parse("2026-07-11T19:00:00+02:00"),
                happeningId = "sup-yoga",
                slots = saturdayWithSupYoga(),
            )

        assertEquals(SlotLiveStateUiModel.Over, row.state)
        assertEquals(Res.string.slot_state_over, row.stateLabel.resourceId())
    }

    @Test
    fun toUiModel_twoHappeningsAtOnce_areStillTwoRows() {
        // Merging is by Happening, never by time: GladiaSUP and the SUP Yoga overlap all afternoon
        // and are two different things to choose between, which is what the list is for.
        val model = mergedSaturday()

        assertEquals(
            listOf("gladiasup", "sup-yoga", "dubside", "silent-party"),
            model.rows.map { it.happeningId },
        )
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
        assertEquals(0f, model.rows.first { it.happeningId == "yoga" }.slots.single().barStart)
    }

    @Test
    fun toUiModel_everyRowCarriesItsPlaceOnTheDay_finishedOnesIncluded() {
        // The bar is where the shape of the day lives — what overlaps what, how much of the
        // afternoon something covers. A row that dropped it on ending would take that with it.
        val rows = state(now = Instant.parse("2026-07-12T02:59:00+02:00"), selectedDayId = "2026:sat").toUiModel().rows

        assertTrue(rows.all { it.state == SlotLiveStateUiModel.Over })
        assertTrue(rows.all { row -> row.slots.all { it.barEnd > it.barStart } })
    }

    @Test
    fun toUiModel_slotRunningToTheEndOfTheAxis_reachesTheFarSide() {
        // The Silent Party closes at 02:00 and Saturday's window runs to 03:00, so it stops short.
        val row = rowAt(QUARTER_TO_FOUR, "silent-party")

        // 12:00 to 03:00 is a fifteen-hour axis; 20:00 is eight hours in and 02:00 is fourteen.
        assertEquals(0.533f, row.slots.single().barStart, ONE_PIXEL_ON_A_PHONE)
        assertEquals(0.933f, row.slots.single().barEnd, ONE_PIXEL_ON_A_PHONE)
    }

    @Test
    fun toUiModel_filteringDoesNotRescaleTheAxis() {
        // Two rows compared across a filter change have to sit at the same place on the bar, so
        // the axis is measured over every Slot of the day rather than the ones left showing.
        val unfiltered = state(selectedDayId = "2026:sat").toUiModel()
        val filtered = state(selectedDayId = "2026:sat", selectedCategoryIds = setOf("musique")).toUiModel()

        assertEquals(unfiltered.scale, filtered.scale)
        assertEquals(
            unfiltered.rows.single { it.happeningId == "dubside" }.slots.single().barStart,
            filtered.rows.single { it.happeningId == "dubside" }.slots.single().barStart,
        )
    }

    // endregion

    // region the Catalogue

    @Test
    fun toUiModel_catalogue_hasNoAxisBecauseNothingOnItHasAnHour() {
        val model = state(selectedScope = ProgrammeScopeState.Catalogue).toUiModel()

        assertNull(model.scale)
        assertTrue(model.rows.isEmpty())
        assertEquals(listOf("amc", "sup-yoga"), model.catalogue.map { it.id })
    }

    @Test
    fun toUiModel_catalogue_keepsTheDayChipsBecauseTheyAreTheWayOutOfIt() {
        val model = state(selectedScope = ProgrammeScopeState.Catalogue).toUiModel()

        // They are not filtering the Catalogue — nothing on it has a day. Tapping Samedi here means
        // show me the Saturday, and a Saturday is a timetable.
        assertEquals(
            listOf("2026:fri", "2026:sat"),
            model.scopes.map { it.id }.filter { it !in FIXED_SCOPE_IDS },
        )
        assertEquals(
            listOf(ProgrammeScopeState.Catalogue.id),
            model.scopes.filter { it.isSelected }.map { it.id },
        )
    }

    @Test
    fun toUiModel_catalogueView_keepsTheCategoryChipsBecauseTheyFilterBothViews() {
        val model =
            state(
                selectedScope = ProgrammeScopeState.Catalogue,
                selectedCategoryIds = setOf("eau"),
            ).toUiModel()

        assertTrue(model.categories.isNotEmpty())
        assertEquals(listOf("sup-yoga"), model.catalogue.map { it.id })
    }

    @Test
    fun toUiModel_catalogueViewWithNoCategoryChosen_showsEverything() {
        // Empty is *Tout* here too, and it is the state the view opens in.
        val model = state(selectedScope = ProgrammeScopeState.Catalogue).toUiModel()

        assertEquals(2, model.catalogue.size)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_catalogueFilterMatchesNothing_saysSoRatherThanShowingAnEmptyGrid() {
        val model =
            state(
                selectedScope = ProgrammeScopeState.Catalogue,
                selectedCategoryIds = setOf("silent"),
            ).toUiModel()

        assertTrue(model.catalogue.isEmpty())
        assertEquals(Res.string.programme_empty_filter, model.emptyMessage.resourceId())
        assertTrue(model.categories.isNotEmpty())
    }

    @Test
    fun toUiModel_catalogueCard_carriesThePictureAndTheGenresARowHasNoRoomFor() {
        val card = state(selectedScope = ProgrammeScopeState.Catalogue).toUiModel().catalogue.first()

        assertEquals("AMC", card.name)
        assertEquals("Musique", card.categoryName)
        assertEquals("https://example.test/amc.webp", card.imageUrl)
        assertEquals(listOf("Electro"), card.genres)
        assertEquals("Electro lausannoise.", card.description)
    }

    @Test
    fun toUiModel_noBundleYet_offersNothingToPointAt() {
        assertTrue(ProgrammeState(now = QUARTER_TO_FOUR).toUiModel().scopes.isEmpty())
    }

    // endregion

    // region the whole weekend at once

    @Test
    fun toUiModel_allDays_sectionsTheListByDayInOrder() {
        val model = state(selectedScope = ProgrammeScopeState.AllDays).toUiModel()

        assertEquals(listOf("2026:fri", "2026:sat"), model.sections.map { it.id })
        assertEquals(listOf("amc"), model.sections.first().rows.map { it.happeningId })
        assertEquals(
            listOf("gladiasup", "dubside", "silent-party"),
            model.sections.last().rows.map { it.happeningId },
        )
    }

    @Test
    fun toUiModel_allDays_givesEachDayItsOwnHeaderAndItsOwnAxis() {
        val model = state(selectedScope = ProgrammeScopeState.AllDays).toUiModel()

        // Friday opens at 16:00 and Saturday at 12:00, which is exactly why one reading in the
        // chrome could not have been right about both.
        assertEquals(listOf("Vendredi", "Samedi"), model.sections.map { it.header?.name })
        assertEquals("16:00", model.sections.first().header?.scale?.startText)
        assertEquals("12:00", model.sections.last().header?.scale?.startText)
    }

    @Test
    fun toUiModel_allDays_writesNoScaleInTheChrome() {
        // It travels with the headers instead. A single reading over three days would be wrong
        // about two of them, which is worse than no reading at all.
        assertNull(state(selectedScope = ProgrammeScopeState.AllDays).toUiModel().scale)
    }

    @Test
    fun toUiModel_allDays_measuresEachRowAgainstItsOwnDay() {
        val allDays = state(selectedScope = ProgrammeScopeState.AllDays).toUiModel()
        val saturdayAlone = state(selectedDayId = "2026:sat").toUiModel()

        // A bar means the same thing whichever scope drew it: where this Slot sits on its own day.
        assertEquals(
            saturdayAlone.rows.single { it.happeningId == "dubside" }.slots.single().barStart,
            allDays.rows.single { it.happeningId == "dubside" }.slots.single().barStart,
        )
    }

    @Test
    fun toUiModel_allDays_leavesOutADayTheFilterEmptied() {
        // Friday is AMC alone, so filtering to the water activities empties it. A header with
        // nothing under it reads as a screen that failed rather than as a quiet Friday.
        val model =
            state(selectedScope = ProgrammeScopeState.AllDays, selectedCategoryIds = setOf("eau")).toUiModel()

        assertEquals(listOf("2026:sat"), model.sections.map { it.id })
    }

    @Test
    fun toUiModel_oneDay_writesNoHeaderBecauseTheChipAboveAlreadySaysWhich() {
        val model = state(selectedDayId = "2026:sat").toUiModel()

        assertEquals(listOf(null), model.sections.map { it.header })
        assertEquals("12:00", model.scale?.startText)
    }

    @Test
    fun toUiModel_rowIdsCarryTheirDay_soTwoCopiesOfOneHappeningCanShareAScreen() {
        // Under *Tous* the same activity's Friday and Saturday rows are both on screen, and a key
        // that repeated would make the list reuse one row's state for the other.
        val model = state(selectedScope = ProgrammeScopeState.AllDays).toUiModel()

        assertEquals(model.rows.size, model.rows.map { it.id }.toSet().size)
    }

    // endregion

    private fun rowAt(
        now: Instant,
        happeningId: String,
        slots: List<ProgrammeSlot> = saturdaySlots() + amc(),
    ) = state(now = now, content = content(slots = slots), selectedDayId = "2026:sat")
        .toUiModel()
        .rows
        .single { it.happeningId == happeningId }

    private fun mergedSaturday() = state(content = content(slots = saturdayWithSupYoga()), selectedDayId = "2026:sat").toUiModel()

    /**
     * [selectedDayId] is sugar for the scope most of these tests want, since a day is what the
     * timetable half of this mapper has always been written against.
     */
    private fun state(
        now: Instant = QUARTER_TO_FOUR,
        content: ProgrammeContent = content(),
        selectedScope: ProgrammeScopeState? = null,
        selectedDayId: String? = null,
        selectedCategoryIds: Set<String> = emptySet(),
    ) = ProgrammeState(
        now = now,
        content = content,
        selectedScope = selectedScope ?: selectedDayId?.let { ProgrammeScopeState.Day(it) },
        selectedCategoryIds = selectedCategoryIds,
    )

    /** Every row on screen, whichever day each one came off — one day or three. */
    private val ProgrammeUiModel.rows: List<SlotRowUiModel>
        get() = sections.flatMap { it.rows }

    private fun catalogue() =
        listOf(
            CatalogueEntry(
                id = "amc",
                name = "AMC",
                categoryId = "musique",
                categoryName = "Musique",
                description = "Electro lausannoise.",
                imageUrl = "https://example.test/amc.webp",
                genres = listOf("Electro"),
            ),
            CatalogueEntry(
                id = "sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                description = null,
                imageUrl = null,
                genres = emptyList(),
            ),
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
            catalogue = catalogue(),
            hasPublishedProgramme = slots.isNotEmpty(),
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

    /**
     * Saturday as the UseCase hands it over: one list, sorted by start and then by the shorter of
     * two. Concatenating the two groups instead would put the 14:00 yoga after the 20:00 Silent
     * Party and test an input the app never produces.
     */
    private fun saturdayWithSupYoga() = (saturdaySlots() + supYoga()).sortedWith(compareBy({ it.start }, { it.end }))

    /**
     * Three separate hours of one activity on one day — the case that made a row a Happening rather
     * than a Slot. Deliberately spread across the afternoon so that one reading of the clock can
     * catch one finished, one live and one still to come.
     */
    private fun supYoga() =
        listOf(
            slot(
                id = "2026:sup-yoga-sat-14",
                happeningId = "sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                start = "2026-07-11T14:00:00+02:00",
                end = "2026-07-11T15:00:00+02:00",
                price = tariff(20.0),
            ),
            slot(
                id = "2026:sup-yoga-sat-16",
                happeningId = "sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                start = "2026-07-11T16:00:00+02:00",
                end = "2026-07-11T17:00:00+02:00",
                price = tariff(20.0),
            ),
            slot(
                id = "2026:sup-yoga-sat-18",
                happeningId = "sup-yoga",
                name = "SUP Yoga",
                categoryId = "eau",
                categoryName = "Sur l'eau",
                start = "2026-07-11T18:00:00+02:00",
                end = "2026-07-11T19:00:00+02:00",
                price = tariff(20.0),
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

        /** The two the app names itself. Every other chip in the row is keyed by a day's own id. */
        val FIXED_SCOPE_IDS = setOf(ProgrammeScopeState.Catalogue.id, ProgrammeScopeState.AllDays.id)
    }
}
