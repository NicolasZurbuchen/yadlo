package io.nicolaszurbuchen.yadlo.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class TabTest {
    @Test
    fun tab_roots_areAllDistinct() {
        // Two tabs sharing a root would silently share a back stack: navigating in one would move
        // the other, and neither would look broken until someone noticed the wrong screen.
        val roots = Tab.entries.map { it.root }

        assertEquals(Tab.entries.size, roots.distinct().size, "Two tabs declare the same root: $roots")
    }

    @Test
    fun tab_declarationOrder_matchesTheBarOrder() {
        // The bar renders Tab.entries in order, so this list is the bottom bar left to right.
        assertEquals(
            listOf(Tab.HOME, Tab.PROGRAMME, Tab.MON_YADLO, Tab.PLUS),
            Tab.entries,
        )
    }
}
