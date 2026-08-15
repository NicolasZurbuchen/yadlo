package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistGroup
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistStand
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.wishlist_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WishlistUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoadingAndSaysNothingElse() {
        val model = WishlistState().toUiModel()

        assertTrue(model.isLoading)
        assertTrue(model.groups.isEmpty())
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_readAndEmpty_pointsAtPlusRatherThanOfferingAnAddFlow() {
        val model = WishlistState(groups = emptyList()).toUiModel()

        assertEquals(UiText.Resource(Res.string.wishlist_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_withSomethingKept_saysNothingAboutBeingEmpty() {
        assertNull(WishlistState(groups = listOf(food())).toUiModel().emptyMessage)
    }

    @Test
    fun toUiModel_group_keepsTheCategoryNameTheContentAuthored() {
        val group = WishlistState(groups = listOf(food())).toUiModel().groups.single()

        // Not uppercased here: the section header's own type slot carries the tracking and weight,
        // so the model holds the word the content published.
        assertEquals("Restauration", group.name)
        assertEquals("restauration", group.id)
    }

    @Test
    fun toUiModel_marks_readAsOneLineWithTheFichesSeparator() {
        val stand = WishlistState(groups = listOf(food())).toUiModel().groups.single().stands.first()

        assertEquals("végan · bio", stand.marks)
    }

    @Test
    fun toUiModel_aStandWithNoMarks_writesNothingRatherThanAnEmptyLine() {
        val stand = WishlistState(groups = listOf(food())).toUiModel().groups.single().stands.last()

        assertNull(stand.marks)
        assertNull(stand.offering)
    }

    @Test
    fun toUiModel_groupsAndStands_keepTheOrderTheDomainPutThemIn() {
        val model = WishlistState(groups = listOf(food(), makers())).toUiModel()

        assertEquals(listOf("restauration", "createurs"), model.groups.map { it.id })
        assertEquals(listOf("Vegan Fabrik", "Guliko"), model.groups.first().stands.map { it.name })
    }

    private fun food() =
        WishlistGroup(
            categoryId = "restauration",
            categoryName = "Restauration",
            stands =
                listOf(
                    WishlistStand(
                        id = "vegan-fabrik",
                        name = "Vegan Fabrik",
                        offering = "Cuisine végétale",
                        marks = listOf("végan", "bio"),
                    ),
                    WishlistStand(id = "guliko", name = "Guliko", offering = null, marks = emptyList()),
                ),
        )

    private fun makers() =
        WishlistGroup(
            categoryId = "createurs",
            categoryName = "Créateurs",
            stands =
                listOf(
                    WishlistStand(
                        id = "la-fanfrelucherie",
                        name = "La Fanfrelucherie",
                        offering = "Accessoires",
                        marks = emptyList(),
                    ),
                ),
        )
}
