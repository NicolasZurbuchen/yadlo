package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Link
import io.nicolaszurbuchen.yadlo.core.content.domain.model.MenuGroup
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Money
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Price
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningKind
import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningSlot
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_mark_gluten_free
import yadlo.shared.generated.resources.dietary_mark_vegetarian
import yadlo.shared.generated.resources.happening_booking_action
import yadlo.shared.generated.resources.happening_booking_required
import yadlo.shared.generated.resources.happening_fact_equipment_not_provided
import yadlo.shared.generated.resources.happening_fact_equipment_provided
import yadlo.shared.generated.resources.happening_fact_supervised
import yadlo.shared.generated.resources.happening_link_website
import yadlo.shared.generated.resources.happening_price_deposit
import yadlo.shared.generated.resources.month_july
import yadlo.shared.generated.resources.price_free
import yadlo.shared.generated.resources.share_happening_activity
import yadlo.shared.generated.resources.share_happening_artist
import yadlo.shared.generated.resources.share_happening_stand
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HappeningUiMapperTest {
    // region loading and missing

    @Test
    fun toUiModel_beforeTheFirstEmission_isLoadingAndNotMissing() {
        val model = HappeningState(now = NOW).toUiModel()

        assertTrue(model.isLoading)
        assertFalse(model.isMissing)
    }

    @Test
    fun toUiModel_loadedWithNoDetail_isMissingRatherThanLoading() {
        // The Happening has gone from the content, which reads differently from "not yet".
        val model = HappeningState(now = NOW, detail = null, isLoaded = true).toUiModel()

        assertFalse(model.isLoading)
        assertTrue(model.isMissing)
    }

    @Test
    fun toUiModel_missing_carriesNoSectionsToDraw() {
        val model = HappeningState(now = NOW, detail = null, isLoaded = true).toUiModel()

        assertEquals("", model.title)
        assertTrue(model.slots.isEmpty())
        assertTrue(model.tags.isEmpty())
        assertNull(model.price)
    }

    // endregion

    // region head

    @Test
    fun toUiModel_theCategoryIsWrittenOutAboveTheTitle() {
        val model = state(detail(categoryName = "Sur l'eau")).toUiModel()

        // Never only coloured: in July sun, on a phone, it is the word that survives.
        assertEquals("SUR L'EAU", model.categoryLabel)
    }

    @Test
    fun toUiModel_theCategoryIdTravelsSoTheScreenCanColourItself() {
        assertEquals("eau", state(detail(categoryId = "eau")).toUiModel().categoryId)
    }

    @Test
    fun toUiModel_tagsAreCarriedAsAuthored() {
        val model = state(detail(tags = listOf("House", "Disco"))).toUiModel()

        assertEquals(listOf("House", "Disco"), model.tags)
    }

    // endregion

    // region date rows

    @Test
    fun toUiModel_slotRow_writesTheDayAndTheTimeOnceAsARange() {
        val model = state(detail(slots = listOf(saturdayFourToSix()))).toUiModel()

        assertEquals("Samedi", model.slots.single().dayName)
        assertEquals("16:00 – 18:00", model.slots.single().timeText)
    }

    @Test
    fun toUiModel_slotRow_datesTheRowFromTheFestivalDayRatherThanFromTheSlot() {
        // The one case that separates the two: a set that runs past midnight belongs to the evening
        // it started, and dating the row off its own start would print the morning after.
        val model = state(detail(slots = listOf(saturdayLate()))).toUiModel()

        assertEquals("Samedi", model.slots.single().dayName)
        assertEquals("11", model.slots.single().dayNumber)
        assertEquals(UiText.Resource(Res.string.month_july), model.slots.single().monthName)
    }

    @Test
    fun toUiModel_slotRunningNow_readsTheSameWordAsTheProgrammeRowItWasOpenedFrom() {
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T17:00:00+02:00")).toUiModel()

        assertEquals(UiText.Resource(Res.string.slot_state_running), model.slots.single().stateLabel)
        assertTrue(model.slots.single().state is SlotLiveStateUiModel.Running)
    }

    @Test
    fun toUiModel_slotEnding_countsTheMinutesDown() {
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T17:48:00+02:00")).toUiModel()

        assertEquals(UiText.Resource(Res.string.slot_state_ending, listOf("12")), model.slots.single().stateLabel)
    }

    @Test
    fun toUiModel_slotOver_saysSoAndStaysOnTheFiche() {
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T20:00:00+02:00")).toUiModel()

        // A three-day activity whose Friday is over still has a Saturday: dropping the past row
        // would take the shape of the run with it.
        assertEquals(1, model.slots.size)
        assertEquals(UiText.Resource(Res.string.slot_state_over), model.slots.single().stateLabel)
    }

    @Test
    fun toUiModel_slotStartingWithinTheHour_countsInMinutes() {
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T15:45:00+02:00")).toUiModel()

        assertEquals(UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("15")), model.slots.single().stateLabel)
    }

    @Test
    fun toUiModel_slotAMinuteOutsideTheWindow_saysNothingYet() {
        // 14:59 against a 16:00 downbeat — one minute past the hour the countdown opens at. The
        // boundary is worth a test of its own because it is the only thing separating a pill from
        // no pill on a row whose start time has not changed.
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T14:59:00+02:00")).toUiModel()

        assertNull(model.slots.single().stateLabel)
    }

    @Test
    fun toUiModel_slotFurtherOutThanTheCountdownWindow_saysNothingAtAll() {
        val model = state(detail(slots = listOf(saturdayFourToSix())), now = Instant.parse("2026-07-11T08:00:00+02:00")).toUiModel()

        // The day and the start time already say everything; "dans 8h" is noise.
        assertNull(model.slots.single().stateLabel)
        assertEquals(SlotLiveStateUiModel.Upcoming, model.slots.single().state)
    }

    // endregion

    // region hearts

    @Test
    fun toUiModel_aPlannedSlot_carriesItsOwnFilledHeart() {
        val model = state(detail(slots = listOf(saturdayFourToSix(planned = true)))).toUiModel()

        assertTrue(model.slots.single().planned)
    }

    @Test
    fun toUiModel_aSlotThatIsOver_stillCarriesItsHeart() {
        val model =
            state(
                detail(slots = listOf(saturdayFourToSix(planned = true))),
                now = Instant.parse("2026-07-11T20:00:00+02:00"),
            ).toUiModel()

        // The whole row stays tappable after the fact. Saving the Friday of a three-day activity
        // once it has run is how someone marks what they went to, not a mistake to prevent.
        assertTrue(model.slots.single().planned)
    }

    @Test
    fun toUiModel_aStand_putsItsOneHeartInTheBar() {
        val model = state(detail(wishlisted = false)).toUiModel()

        assertEquals(false, model.wishlisted)
    }

    @Test
    fun toUiModel_anythingThatIsNotAStand_hasNoHeartInTheBarAtAll() {
        // Null rather than false: the bar draws nothing, rather than drawing an empty heart that
        // would save a Happening whose Slots are each already savable below it.
        assertNull(state(detail()).toUiModel().wishlisted)
    }

    @Test
    fun toUiModel_whileLoading_hasNoHeartAnywhere() {
        val model = HappeningState(now = NOW).toUiModel()

        assertNull(model.wishlisted)
        assertTrue(model.slots.isEmpty())
    }

    // endregion

    // region price

    @Test
    fun toUiModel_freeActivity_saysSoRatherThanShowingNoPriceSection() {
        val model = state(detail(price = free())).toUiModel()

        assertEquals(listOf(UiText.Resource(Res.string.price_free)), model.price?.tiers?.map { it.amount })
        assertNull(model.price?.tiers?.single()?.label)
    }

    @Test
    fun toUiModel_tieredActivity_keepsEveryTierAndItsLabel() {
        val model = state(detail(price = silentPartyPrice())).toUiModel()

        assertEquals(listOf("Adulte", "Moins de 16 ans"), model.price?.tiers?.map { it.label })
        assertEquals(
            listOf(UiText.Raw("CHF 25"), UiText.Raw("CHF 15")),
            model.price?.tiers?.map { it.amount },
        )
    }

    @Test
    fun toUiModel_deposit_isItsOwnLineAndIsNeverSummedIntoATier() {
        val model = state(detail(price = silentPartyPrice())).toUiModel()

        // CHF 25 + CHF 50 = CHF 75 is wrong in the direction that stops someone coming.
        assertEquals(UiText.Resource(Res.string.happening_price_deposit, listOf("CHF 50")), model.price?.deposit)
        assertEquals("Caution casque.", model.price?.depositNote)
        assertFalse(model.price?.tiers.orEmpty().any { it.amount == UiText.Raw("CHF 75") })
    }

    @Test
    fun toUiModel_aPerUnitTier_writesTheUnitBesideTheAmount() {
        val price =
            Price(
                free = false,
                tiers = listOf(Price.Tier(label = null, amount = Money(10.0, "CHF"), per = "personne")),
                deposit = null,
                provenance = Provenance.CONFIRMED,
            )

        assertEquals(UiText.Raw("CHF 10 / personne"), state(detail(price = price)).toUiModel().price?.tiers?.single()?.amount)
    }

    @Test
    fun toUiModel_noPrice_drawsNoPriceSection() {
        assertNull(state(detail()).toUiModel().price)
    }

    // endregion

    // region booking

    @Test
    fun toUiModel_bookingWithAPage_isAnActionThatLeavesTheApp() {
        val model = state(detail(bookingRequired = true, bookingUrl = "https://booking.example/")).toUiModel()

        assertEquals(UiText.Resource(Res.string.happening_booking_action), model.booking?.label)
        assertEquals("https://booking.example/", model.booking?.url)
    }

    @Test
    fun toUiModel_bookingWithoutAPage_stillSaysABookingIsNeeded() {
        val model = state(detail(bookingRequired = true, bookingUrl = null)).toUiModel()

        // Someone who turns up without a ticket has lost the evening, not a tap.
        assertEquals(UiText.Resource(Res.string.happening_booking_required), model.booking?.label)
        assertNull(model.booking?.url)
    }

    @Test
    fun toUiModel_noBookingNeeded_drawsNoBookingRow() {
        assertNull(state(detail(bookingRequired = false)).toUiModel().booking)
    }

    // endregion

    // region facts

    @Test
    fun toUiModel_equipmentProvided_isAFact() {
        val model = state(detail(equipmentProvided = true)).toUiModel()

        assertEquals(listOf(UiText.Resource(Res.string.happening_fact_equipment_provided)), model.facts)
    }

    @Test
    fun toUiModel_equipmentNotProvided_isAlsoAFact() {
        // "Apportez votre tapis" is the difference between a good morning and a wasted trip.
        val model = state(detail(equipmentProvided = false)).toUiModel()

        assertEquals(listOf(UiText.Resource(Res.string.happening_fact_equipment_not_provided)), model.facts)
    }

    @Test
    fun toUiModel_supervisionUnknown_saysNothingRatherThanSayingUnsupervised() {
        // An absent flag means unknown. "Non encadré" on a slackline is a warning nobody wrote.
        assertTrue(state(detail(supervised = null)).toUiModel().facts.isEmpty())
        assertTrue(state(detail(supervised = false)).toUiModel().facts.isEmpty())
    }

    @Test
    fun toUiModel_supervised_isAFact() {
        assertEquals(
            listOf(UiText.Resource(Res.string.happening_fact_supervised)),
            state(detail(supervised = true)).toUiModel().facts,
        )
    }

    @Test
    fun toUiModel_suitability_isCarriedAsTheProseItIs() {
        val model = state(detail(suitability = "De 4 à 12 ans, deux heures maximum")).toUiModel()

        assertEquals(listOf(UiText.Raw("De 4 à 12 ans, deux heures maximum")), model.facts)
    }

    @Test
    fun toUiModel_facts_leadWithWhoItSuitsBeforeTheEquipment() {
        val model = state(detail(suitability = "Familles", equipmentProvided = true, supervised = true)).toUiModel()

        assertEquals(
            listOf(
                UiText.Raw("Familles"),
                UiText.Resource(Res.string.happening_fact_equipment_provided),
                UiText.Resource(Res.string.happening_fact_supervised),
            ),
            model.facts,
        )
    }

    // endregion

    // region menu

    @Test
    fun toUiModel_menuItem_writesItsPriceAsMoney() {
        val model = state(detail(menu = listOf(plats()))).toUiModel()

        assertEquals("CHF 15", model.menu.single().items.first().priceText)
    }

    @Test
    fun toUiModel_menuItemWithNoMarks_carriesNoneRatherThanAnEmptyLine() {
        val model = state(detail(menu = listOf(plats()))).toUiModel()

        assertTrue(model.menu.single().items.first().dietary.isEmpty())
    }

    @Test
    fun toUiModel_menuItemMarks_areJoinedAsWords() {
        val model = state(detail(menu = listOf(plats()))).toUiModel()

        // Text, never pictograms: no legend to learn, and no symbol that means "contains" in one
        // country and "free from" in another.
        assertEquals(
            listOf(Res.string.dietary_mark_vegetarian, Res.string.dietary_mark_gluten_free),
            model.menu.single().items[1].dietary.map { it.label },
        )
    }

    @Test
    fun toUiModel_menuGroupIdAndName_bothSurvive() {
        val model = state(detail(menu = listOf(plats()))).toUiModel()

        // The id keys the group in the list and the name is the section header it is drawn under.
        assertEquals("plats", model.menu.single().id)
        assertEquals("Plats", model.menu.single().name)
    }

    // endregion

    // region links

    @Test
    fun toUiModel_websiteLink_isTheOneLabelThatTranslates() {
        val model = state(detail(links = listOf(Link(type = "website", url = "https://djalf.ch/")))).toUiModel()

        assertEquals(UiText.Resource(Res.string.happening_link_website), model.links.single().name)
    }

    @Test
    fun toUiModel_platformLinks_keepTheirOwnCasing() {
        val links =
            listOf(
                Link(type = "tiktok", url = "https://tiktok.com/"),
                Link(type = "soundcloud", url = "https://soundcloud.com/"),
            )

        // Brand names are not copy and do not translate, so they never enter strings.xml.
        assertEquals(
            listOf(UiText.Raw("TikTok"), UiText.Raw("SoundCloud")),
            state(detail(links = links)).toUiModel().links.map { it.name },
        )
    }

    @Test
    fun toUiModel_anUnknownPlatform_fallsBackToWhatTheContentAuthored() {
        val model = state(detail(links = listOf(Link(type = "mixcloud", url = "https://mixcloud.com/")))).toUiModel()

        // `type` stays a String precisely so a new platform renders rather than failing to parse.
        // With no mark to draw, the row falls back to this word — which is why it is carried at all.
        assertEquals(UiText.Raw("mixcloud"), model.links.single().name)
        assertNull(model.links.single().icon)
    }

    @Test
    fun toUiModel_aKnownPlatform_carriesItsMark() {
        val links =
            listOf(
                Link(type = "website", url = "https://djalf.ch/"),
                Link(type = "beatport", url = "https://beatport.com/"),
            )

        // One function keys both this row and Accueil's footer, so the two cannot disagree about
        // what Instagram looks like — and `website` is in the set, because on a fiche an artist's
        // own site is one more place they exist rather than a different kind of offer.
        assertEquals(
            listOf(socialIconFor("website"), socialIconFor("beatport")),
            state(detail(links = links)).toUiModel().links.map { it.icon },
        )
    }

    // endregion

    // region the share message

    @Test
    fun toUiModel_shareBody_namesTheThingWhatItIsWhenItRunsAndAnAddressThatWorksWithoutTheApp() {
        val state = state(detail(tags = listOf("House", "Disco"), slots = listOf(saturdayFourToSix())))

        assertEquals(
            "Dubside\n" +
                "House · Disco\n" +
                "Samedi 11.07.2026, 16:00 – 18:00\n" +
                "\n" +
                "Yadlo 2026\n" +
                "https://www.yadlo.ch/",
            state.shareBody(),
        )
    }

    @Test
    fun toUiModel_shareOpening_namesTheKindBecauseThatIsTheOnePartThatIsCopy() {
        assertEquals(Res.string.share_happening_artist, state(detail(kind = HappeningKind.ARTIST)).shareResource())
        assertEquals(Res.string.share_happening_activity, state(detail(kind = HappeningKind.ACTIVITY)).shareResource())
        assertEquals(Res.string.share_happening_stand, state(detail(kind = HappeningKind.STAND)).shareResource())
    }

    @Test
    fun toUiModel_shareBodyForAStand_leansOnItsOfferingBecauseItHasNoDates() {
        // The case that sent this back for a second pass: a Stand has no Slots, so without the
        // offering the message was a name and an address and read as though something was missing.
        val state = state(detail(kind = HappeningKind.STAND, tags = listOf("Cuisine végétale"), slots = emptyList()))

        assertEquals("Dubside\nCuisine végétale\n\nYadlo 2026\nhttps://www.yadlo.ch/", state.shareBody())
    }

    @Test
    fun toUiModel_shareBodyForSeveralDates_putsEachOnItsOwnLine() {
        // Joined onto one line they ran past the width of a message bubble.
        val state = state(detail(slots = listOf(saturdayFourToSix(), sundayFourToSix())))

        assertEquals(
            "Dubside\nSamedi 11.07.2026, 16:00 – 18:00\nDimanche 12.07.2026, 16:00 – 18:00\n\nYadlo 2026\nhttps://www.yadlo.ch/",
            state.shareBody(),
        )
    }

    @Test
    fun toUiModel_shareBodyWhenTheContentPublishesNoWebsite_stillNamesTheEdition() {
        // The published file always carries one — validate.js sees to that — but a bundle cached
        // by an older build does not, and that has to cost a line rather than the message.
        val state = state(detail(festivalWebsite = null, slots = emptyList()))

        assertEquals("Dubside\n\nYadlo 2026", state.shareBody())
    }

    @Test
    fun toUiModel_shareTextWhileLoading_isNullSoTheActionCanHideItself() {
        val state = HappeningState(now = NOW, detail = null, isLoaded = false)

        assertNull(state.toUiModel().shareText)
    }

    /** The one argument of the opening sentence — the whole message below its first line. */
    private fun HappeningState.shareBody(): String {
        val text = toUiModel().shareText
        assertIs<UiText.Resource>(text)

        return text.args.single().toString()
    }

    private fun HappeningState.shareResource(): StringResource {
        val text = toUiModel().shareText
        assertIs<UiText.Resource>(text)

        return text.id
    }

    // endregion

    private fun state(
        detail: HappeningDetail,
        now: Instant = NOW,
    ) = HappeningState(now = now, detail = detail, isLoaded = true)

    private fun detail(
        kind: HappeningKind = HappeningKind.ARTIST,
        categoryId: String = "musique",
        categoryName: String = "Musique",
        imageUrl: String? = null,
        tags: List<String> = emptyList(),
        slots: List<HappeningSlot> = emptyList(),
        price: Price? = null,
        bookingRequired: Boolean = false,
        bookingUrl: String? = null,
        equipmentProvided: Boolean? = null,
        suitability: String? = null,
        supervised: Boolean? = null,
        menu: List<MenuGroup> = emptyList(),
        links: List<Link> = emptyList(),
        wishlisted: Boolean? = null,
        festivalWebsite: String? = "https://www.yadlo.ch/",
    ) = HappeningDetail(
        id = "dubside",
        name = "Dubside",
        kind = kind,
        categoryId = categoryId,
        categoryName = categoryName,
        imageUrl = imageUrl,
        description = null,
        tags = tags,
        dietary = emptyMap(),
        slots = slots,
        price = price,
        bookingUrl = bookingUrl,
        bookingRequired = bookingRequired,
        equipmentProvided = equipmentProvided,
        suitability = suitability,
        supervised = supervised,
        menu = menu,
        links = links,
        wishlisted = wishlisted,
        editionName = "Yadlo 2026",
        festivalWebsite = festivalWebsite,
    )

    private fun saturdayFourToSix(planned: Boolean = false) =
        HappeningSlot(
            id = "2026:dubside-sat",
            dayName = "Samedi",
            dayStart = Instant.parse("2026-07-11T12:00:00+02:00"),
            start = Instant.parse("2026-07-11T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T18:00:00+02:00"),
            planned = planned,
        )

    private fun sundayFourToSix() =
        HappeningSlot(
            id = "2026:dubside-sun",
            dayName = "Dimanche",
            dayStart = Instant.parse("2026-07-12T12:00:00+02:00"),
            start = Instant.parse("2026-07-12T16:00:00+02:00"),
            end = Instant.parse("2026-07-12T18:00:00+02:00"),
            planned = false,
        )

    /** Starts on the Saturday and ends on the Sunday, which is the Saturday as far as a fiche goes. */
    private fun saturdayLate() =
        HappeningSlot(
            id = "2026:silent-party-sat",
            dayName = "Samedi",
            dayStart = Instant.parse("2026-07-11T12:00:00+02:00"),
            start = Instant.parse("2026-07-12T01:00:00+02:00"),
            end = Instant.parse("2026-07-12T02:30:00+02:00"),
            planned = false,
        )

    private fun free() = Price(free = true, tiers = emptyList(), deposit = null, provenance = Provenance.CONFIRMED)

    private fun silentPartyPrice() =
        Price(
            free = false,
            tiers =
                listOf(
                    Price.Tier(label = "Adulte", amount = Money(25.0, "CHF"), per = null),
                    Price.Tier(label = "Moins de 16 ans", amount = Money(15.0, "CHF"), per = null),
                ),
            deposit = Price.Deposit(amount = Money(50.0, "CHF"), note = "Caution casque."),
            provenance = Provenance.CONFIRMED,
        )

    private fun plats() =
        MenuGroup(
            id = "plats",
            name = "Plats",
            description = null,
            source = "Lu sur une ardoise",
            items =
                listOf(
                    MenuGroup.Item(
                        name = "Assiette de mezzés",
                        price = Money(15.0, "CHF"),
                        description = null,
                        marks = emptyList(),
                        provenance = Provenance.UNVERIFIED,
                    ),
                    MenuGroup.Item(
                        name = "Seitan à la cantonaise",
                        price = Money(18.0, "CHF"),
                        description = "Riz, légumes croquants",
                        marks = listOf("sans-gluten", "vegetarien"),
                        provenance = Provenance.UNVERIFIED,
                    ),
                ),
        )

    private companion object {
        /** The Saturday at 15:45 — the moment the Programme prototype was argued from. */
        val NOW = Instant.parse("2026-07-11T15:45:00+02:00")
    }
}
