package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.common.content.domain.model.DietaryCoverage
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_all_dairy_free
import yadlo.shared.generated.resources.dietary_all_vegan
import yadlo.shared.generated.resources.dietary_mark_vegan
import yadlo.shared.generated.resources.dietary_mark_vegetarian
import yadlo.shared.generated.resources.dietary_some_vegetarian
import yadlo.shared.generated.resources.stands_empty
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StandsUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingButAlreadyKnowsItsTitle() {
        val model = StandsState(kind = StandsKindUiModel.FOOD).toUiModel()

        // The title comes from the entry that was tapped, not from the content, so the bar reads
        // correctly while the list is still arriving.
        assertTrue(model.isLoading)
        assertEquals(UiText.Resource(StandsKindUiModel.FOOD.title), model.title)
        assertTrue(model.stands.isEmpty())
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_thePhotograph_reachesTheCardItIsMostlyMadeOf() {
        val model = state().toUiModel()

        assertEquals("https://example.test/vegan-fabrik.webp", model.stands.first().imageUrl)
    }

    @Test
    fun toUiModel_theTitle_matchesTheRowThatOpenedIt() {
        val model = StandsState(kind = StandsKindUiModel.MAKERS, directory = directory()).toUiModel()

        assertEquals(UiText.Resource(StandsKindUiModel.MAKERS.title), model.title)
    }

    @Test
    fun toUiModel_nothingPublished_saysSoRatherThanBlamingTheFilter() {
        val model =
            StandsState(
                kind = StandsKindUiModel.FOOD,
                directory = StandDirectory(stands = emptyList(), marks = emptyList()),
            ).toUiModel()

        assertEquals(UiText.Resource(Res.string.stands_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_aFilterThatMatchesNothing_saysSomethingTheReaderCanActOn() {
        val model = state(setOf("halal")).toUiModel()

        // Two different empties. Only one of them is the reader's to fix, and telling them the
        // stands are unpublished when they have simply over-filtered would be a lie.
        assertEquals(UiText.Resource(Res.string.stands_no_match), model.emptyMessage)
    }

    @Test
    fun toUiModel_theFirstChip_clearsTheFilterRatherThanBeingAMark() {
        val first = state().toUiModel().chips.first()

        assertNull(first.mark)
        assertEquals(UiText.Resource(Res.string.stands_filter_all), first.label)
        assertTrue(first.isSelected)
    }

    @Test
    fun toUiModel_aChipCarriesTheContentsSlugEvenThoughItShowsTheAppsWord() {
        // The slug is what the filter compares against, so it has to survive the label lookup.
        assertEquals(listOf(null, "vegan", "vegetarien"), state().toUiModel().chips.map { it.mark })
    }

    @Test
    fun toUiModel_aHalfWithNoMarks_offersOnlyTout() {
        // Créateurs publishes none, and the screen reads that as "no chip row at all" rather than
        // drawing a lone *Tout* that filters nothing.
        val model =
            StandsState(
                kind = StandsKindUiModel.MAKERS,
                directory = StandDirectory(stands = listOf(listing("la-fanfrelucherie")), marks = emptyList()),
            ).toUiModel()

        assertEquals(1, model.chips.size)
    }

    @Test
    fun toUiModel_aSelectedChip_isTheOnlyOneMarkedSo() {
        val model = state(setOf("vegetarien")).toUiModel()

        assertEquals(listOf("vegetarien"), model.chips.filter { it.isSelected }.map { it.mark })
    }

    @Test
    fun toUiModel_noFilter_showsEveryStandOfThatHalf() {
        assertEquals(listOf("vegan-fabrik", "de-lor-bokit", "guliko"), state().toUiModel().stands.map { it.id })
    }

    @Test
    fun toUiModel_filteringByAMarkOnlyOneDishCarries_keepsTheStand() {
        // De l'Or Bokit sells one végé bokit and nothing else vegetarian. "Can I eat here" is
        // answered yes, which is the only question the chip was asked.
        assertEquals(
            listOf("de-lor-bokit"),
            state(setOf("vegetarien")).toUiModel().stands.map { it.id },
        )
    }

    @Test
    fun toUiModel_aStandMatchedThroughOneDish_saysSoRatherThanClaimingTheWholeTruck() {
        // The difference between "sells one vegan bokit" and "is vegan", which is the whole reason
        // the coverage is derived rather than authored.
        assertEquals(
            listOf(Res.string.dietary_some_vegetarian),
            state(setOf("vegetarien")).toUiModel().stands.single().dietary.map { it.label },
        )
    }

    @Test
    fun toUiModel_aStandWhereEveryDishCarriesTheMark_saysTheWholeTruck() {
        assertEquals(
            listOf(Res.string.dietary_all_vegan, Res.string.dietary_all_dairy_free),
            state().toUiModel().stands.first().dietary.map { it.label },
        )
    }

    @Test
    fun toUiModel_aStandWithNothingToSay_saysNothingRatherThanAnEmptyRow() {
        assertTrue(state().toUiModel().stands.last().dietary.isEmpty())
    }

    @Test
    fun toUiModel_aChip_isLabelledInTheAppsWordsRatherThanTheContentsSlug() {
        // The slug is a lookup key; "Sans gluten" is what a reader is offered.
        assertEquals(
            listOf(Res.string.stands_filter_all, Res.string.dietary_mark_vegan, Res.string.dietary_mark_vegetarian),
            state().toUiModel().chips.map { (it.label as UiText.Resource).id },
        )
    }

    @Test
    fun toUiModel_aMarkThisBuildHasNoGlyphFor_losesItsChipRatherThanShowingASlug() {
        val state = StandsState(kind = StandsKindUiModel.FOOD, directory = directory(marks = listOf("sans-noix")))

        assertEquals(listOf(Res.string.stands_filter_all), state.toUiModel().chips.map { (it.label as UiText.Resource).id })
    }

    @Test
    fun toUiModel_twoMarks_keepOnlyTheStandsThatCarryBoth() {
        // The whole reason the set is an AND. Vegan Fabrik is vegan and De l'Or Bokit has a
        // vegetarian dish; neither is both, so both go. Showing the merely-vegan one to someone who
        // also needs gluten-free is pointing them at food they cannot eat.
        val model = state(setOf("vegan", "vegetarien")).toUiModel()

        assertEquals(emptyList(), model.stands.map { it.id })
        assertEquals(Res.string.stands_no_match, (model.emptyMessage as UiText.Resource).id)
    }

    @Test
    fun toUiModel_twoMarksOneStandCarriesBothOf_keepsIt() {
        val model = state(setOf("vegan", "sans-lactose")).toUiModel()

        assertEquals(listOf("vegan-fabrik"), model.stands.map { it.id })
    }

    @Test
    fun toUiModel_bothChipsRead_asSelected() {
        val selected = state(setOf("vegan", "vegetarien")).toUiModel().chips.filter { it.isSelected }

        // Multi-select is only usable if the row shows which ones are on.
        assertEquals(listOf("vegan", "vegetarien"), selected.map { it.mark })
    }

    @Test
    fun toUiModel_withSomethingListed_saysNothingAboutBeingEmpty() {
        assertNull(state().toUiModel().emptyMessage)
    }

    private fun state(selectedMarks: Set<String> = emptySet()) =
        StandsState(kind = StandsKindUiModel.FOOD, directory = directory(), selectedMarks = selectedMarks)

    private fun directory(marks: List<String> = listOf("vegan", "vegetarien")) =
        StandDirectory(
            marks = marks,
            stands =
                listOf(
                    listing(
                        id = "vegan-fabrik",
                        offering = "Cuisine végétale",
                        dietary =
                            mapOf("vegan" to DietaryCoverage.ALL, "sans-lactose" to DietaryCoverage.ALL),
                    ),
                    listing(
                        id = "de-lor-bokit",
                        offering = "Cuisine guadeloupéenne",
                        dietary = mapOf("vegetarien" to DietaryCoverage.SOME),
                    ),
                    listing(id = "guliko", offering = "Cuisine géorgienne"),
                ),
        )

    private fun listing(
        id: String,
        offering: String? = null,
        dietary: Map<String, DietaryCoverage> = emptyMap(),
    ) = StandListing(
        id = id,
        name = id,
        offering = offering,
        imageUrl = "https://example.test/$id.webp",
        dietary = dietary,
    )
}
