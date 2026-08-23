package io.nicolaszurbuchen.yadlo.app.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TabNavigatorTest {
    @Test
    fun selectedTab_beforeTheStartTabIsDecided_isHome() {
        // The field's default rather than the app's answer: the shell calls selectStart as soon as
        // it composes, and it does not compose until the content that decides the Phase is Ready.
        val navigator = TabNavigator()

        assertEquals(Tab.HOME, navigator.selectedTab.value)
    }

    // region the start tab

    @Test
    fun selectStart_movesToTheTabThePhaseAsksFor() {
        val navigator = TabNavigator()

        navigator.selectStart(Tab.PROGRAMME)

        assertEquals(Tab.PROGRAMME, navigator.selectedTab.value)
    }

    @Test
    fun selectStart_calledAgain_doesNothing() {
        // The caller is a composition and cannot promise to run exactly once — a rotation is
        // enough to bring it back around.
        val navigator = TabNavigator()

        navigator.selectStart(Tab.PROGRAMME)
        navigator.selectStart(Tab.HOME)

        assertEquals(Tab.PROGRAMME, navigator.selectedTab.value)
    }

    @Test
    fun selectStart_afterTheVisitorHasChosenATab_leavesThemWhereTheyAre() {
        // The case this exists to prevent. A tap is a decision, and content arriving a beat later
        // must not overrule it — nor must a recomposition on the night LIVE begins.
        val navigator = TabNavigator()

        navigator.select(Tab.PLUS)
        navigator.selectStart(Tab.PROGRAMME)

        assertEquals(Tab.PLUS, navigator.selectedTab.value)
    }

    @Test
    fun selectStart_thenTheVisitorMovesOn_doesNotPullThemBack() {
        val navigator = TabNavigator()

        navigator.selectStart(Tab.PROGRAMME)
        navigator.select(Tab.MON_YADLO)
        navigator.selectStart(Tab.PROGRAMME)

        assertEquals(Tab.MON_YADLO, navigator.selectedTab.value)
    }

    // endregion

    // region telling a cold start from a rotation

    @Test
    fun selectStart_theFirstTimeInAProcess_saysSo() {
        // What the shell resets the tab back stacks on. This object lives as long as the process, so
        // the first call is the only moment the process itself is known to be new.
        assertTrue(TabNavigator().selectStart(Tab.HOME))
    }

    @Test
    fun selectStart_everyTimeAfter_saysItIsNotTheFirst() {
        // A rotation, or the content going away and coming back. The stacks were restored for a
        // reason in that case and must be left alone.
        val navigator = TabNavigator()

        navigator.selectStart(Tab.HOME)

        assertFalse(navigator.selectStart(Tab.HOME))
    }

    @Test
    fun selectStart_afterTheVisitorHasChosenATab_isAlsoNotTheFirst() {
        // A tap means the shell has been on screen, so whatever brings it back is not a cold start —
        // and popping the stacks under somebody who is using them would be the worst version of this.
        val navigator = TabNavigator()

        navigator.select(Tab.PLUS)

        assertFalse(navigator.selectStart(Tab.HOME))
    }

    // endregion

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
