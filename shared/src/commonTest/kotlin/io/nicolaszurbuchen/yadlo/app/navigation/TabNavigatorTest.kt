package io.nicolaszurbuchen.yadlo.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class TabNavigatorTest {
    @Test
    fun selectedTab_initially_isHome() {
        // DECISIONS.md has the start tab following the Phase - Accueil for 361 days, Programme
        // for the four days of the festival. Until the edition's days are loaded this is the
        // 361-day answer, and this test is what will change when the Phase arrives.
        val navigator = TabNavigator()

        assertEquals(Tab.HOME, navigator.selectedTab.value)
    }

    @Test
    fun select_movesToTheGivenTab() {
        val navigator = TabNavigator()

        navigator.select(Tab.PROGRAMME)

        assertEquals(Tab.PROGRAMME, navigator.selectedTab.value)
    }

    @Test
    fun select_sameTabTwice_staysOnThatTab() {
        val navigator = TabNavigator()

        navigator.select(Tab.PLUS)
        navigator.select(Tab.PLUS)

        assertEquals(Tab.PLUS, navigator.selectedTab.value)
    }
}
