package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistGroup
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistStand
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WishlistReducerTest {
    private val reducer = WishlistStoreFactory.ReducerImpl

    @Test
    fun groupsUpdated_firstEmission_holdsTheKeptStands() {
        val result = with(reducer) { WishlistState().reduce(WishlistMessage.GroupsUpdated(listOf(food()))) }

        assertEquals(listOf("restauration"), result.groups?.map { it.categoryId })
    }

    @Test
    fun groupsUpdated_beforeAnyEmission_thereIsNoListRatherThanAnEmptyOne() {
        // Null is "not read yet". An emptied Wishlist arrives as an empty list, and the screen
        // owes those two different answers.
        assertNull(WishlistState().groups)
    }

    @Test
    fun groupsUpdated_theLastStandRemovedOnItsFiche_becomesAnEmptyListNotANullOne() {
        val state = WishlistState(groups = listOf(food()))

        val result = with(reducer) { state.reduce(WishlistMessage.GroupsUpdated(emptyList())) }

        assertTrue(result.groups?.isEmpty() == true)
    }

    private fun food() =
        WishlistGroup(
            categoryId = "restauration",
            categoryName = "Restauration",
            stands = listOf(WishlistStand(id = "guliko", name = "Guliko", offering = null, imageUrl = null, dietary = emptyMap())),
        )
}
