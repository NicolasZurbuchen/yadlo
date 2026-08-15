package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandGroup
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.stands_empty
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StandsUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingAndSaysNothingElse() {
        val model = StandsState().toUiModel()

        assertTrue(model.isLoading)
        assertTrue(model.groups.isEmpty())
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_nothingPublished_saysSoRatherThanBlamingTheFilter() {
        val model = StandsState(directory = StandDirectory(groups = emptyList(), marks = emptyList())).toUiModel()

        assertEquals(UiText.Resource(Res.string.stands_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_aFilterThatMatchesNothing_saysSomethingTheReaderCanActOn() {
        val model = StandsState(directory = directory(), selectedMark = "sans lactose").toUiModel()

        // Two different empties. Only one of them is the reader's to fix, and telling them the
        // stands are unpublished when they have simply over-filtered would be a lie.
        assertEquals(UiText.Resource(Res.string.stands_no_match), model.emptyMessage)
    }

    @Test
    fun toUiModel_theFirstChip_clearsTheFilterRatherThanBeingAMark() {
        val model = StandsState(directory = directory()).toUiModel()

        val first = model.chips.first()

        assertNull(first.mark)
        assertEquals(UiText.Resource(Res.string.stands_filter_all), first.label)
        assertTrue(first.isSelected)
    }

    @Test
    fun toUiModel_theChips_areTheContentsOwnWords() {
        val model = StandsState(directory = directory()).toUiModel()

        // `végé` and `sans gluten` are how the festival writes them and how a menu board writes
        // them. Prettifying either would be inventing a vocabulary the stands do not use.
        assertEquals(listOf(null, "végan", "bio", "végé"), model.chips.map { it.mark })
    }

    @Test
    fun toUiModel_aSelectedChip_isTheOnlyOneMarkedSo() {
        val model = StandsState(directory = directory(), selectedMark = "végé").toUiModel()

        assertEquals(listOf("végé"), model.chips.filter { it.isSelected }.map { it.mark })
    }

    @Test
    fun toUiModel_noFilter_showsEveryStand() {
        val model = StandsState(directory = directory()).toUiModel()

        assertEquals(listOf("restauration", "createurs"), model.groups.map { it.id })
        assertEquals(3, model.groups.first().stands.size)
    }

    @Test
    fun toUiModel_filteringByAMarkOnlyOneDishCarries_keepsTheStand() {
        val model = StandsState(directory = directory(), selectedMark = "végé").toUiModel()

        // De l'Or Bokit carries no stand mark and sells one végé bokit. "Can I eat here" is
        // answered yes, which is the only question the chip was asked.
        assertEquals(listOf("de-lor-bokit"), model.groups.single().stands.map { it.id })
    }

    @Test
    fun toUiModel_aStandMatchedThroughItsMenu_stillShowsOnlyItsOwnMarks() {
        val model = StandsState(directory = directory(), selectedMark = "végé").toUiModel()

        // Widening the line would turn "sells one vegan bokit" into "is vegan", which is exactly
        // the claim the stand/item split exists to prevent.
        assertNull(model.groups.single().stands.single().marks)
    }

    @Test
    fun toUiModel_aGroupEmptiedByTheFilter_disappearsRatherThanShowingItsHeader() {
        val model = StandsState(directory = directory(), selectedMark = "végan").toUiModel()

        assertEquals(listOf("restauration"), model.groups.map { it.id })
    }

    @Test
    fun toUiModel_marks_readAsOneLineWithTheFichesSeparator() {
        val model = StandsState(directory = directory()).toUiModel()

        assertEquals("végan · bio", model.groups.first().stands.first().marks)
    }

    @Test
    fun toUiModel_aStandWithNoMarks_writesNothingRatherThanAnEmptyLine() {
        val model = StandsState(directory = directory()).toUiModel()

        assertNull(model.groups.first().stands.last().marks)
    }

    @Test
    fun toUiModel_theCategoryName_isWrittenAsTheContentAuthorsIt() {
        val model = StandsState(directory = directory()).toUiModel()

        // Not uppercased here: the section header's own type slot carries the tracking and weight.
        assertEquals("Restauration", model.groups.first().name)
    }

    @Test
    fun toUiModel_withSomethingListed_saysNothingAboutBeingEmpty() {
        assertNull(StandsState(directory = directory()).toUiModel().emptyMessage)
    }

    private fun directory() =
        StandDirectory(
            marks = listOf("végan", "bio", "végé"),
            groups =
                listOf(
                    StandGroup(
                        categoryId = "restauration",
                        categoryName = "Restauration",
                        stands =
                            listOf(
                                StandListing(
                                    id = "vegan-fabrik",
                                    name = "Vegan Fabrik",
                                    offering = "Cuisine végétale",
                                    marks = listOf("végan", "bio"),
                                    dietaryMatches = setOf("végan", "bio"),
                                ),
                                StandListing(
                                    id = "de-lor-bokit",
                                    name = "De l'Or Bokit",
                                    offering = "Cuisine guadeloupéenne",
                                    marks = emptyList(),
                                    dietaryMatches = setOf("végé"),
                                ),
                                StandListing(
                                    id = "guliko",
                                    name = "Guliko",
                                    offering = "Cuisine géorgienne",
                                    marks = emptyList(),
                                    dietaryMatches = emptySet(),
                                ),
                            ),
                    ),
                    StandGroup(
                        categoryId = "createurs",
                        categoryName = "Créateurs",
                        stands =
                            listOf(
                                StandListing(
                                    id = "la-fanfrelucherie",
                                    name = "La Fanfrelucherie",
                                    offering = "Costumes de seconde main",
                                    marks = emptyList(),
                                    dietaryMatches = emptySet(),
                                ),
                            ),
                    ),
                ),
        )
}
