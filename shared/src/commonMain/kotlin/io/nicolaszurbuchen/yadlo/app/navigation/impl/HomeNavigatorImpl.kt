package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.app.navigation.Tab
import io.nicolaszurbuchen.yadlo.app.navigation.TabNavigator
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeNavigator

/**
 * Switches tab rather than pushing anything: both hero destinations are tab roots, and each tab
 * keeps whatever depth the visitor left it at.
 */
class HomeNavigatorImpl(
    private val tabNavigator: TabNavigator,
) : HomeNavigator {
    override fun navigateToProgramme() {
        tabNavigator.select(Tab.PROGRAMME)
    }

    override fun navigateToMonYadlo() {
        tabNavigator.select(Tab.MON_YADLO)
    }
}
