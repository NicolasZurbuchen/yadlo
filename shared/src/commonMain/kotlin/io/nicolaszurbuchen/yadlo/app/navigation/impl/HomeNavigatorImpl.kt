package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.app.navigation.Tab
import io.nicolaszurbuchen.yadlo.app.navigation.TabNavigator
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.AnnouncementsDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class HomeNavigatorImpl(
    private val appNavigator: AppNavigator,
    private val tabNavigator: TabNavigator,
) : HomeNavigator {
    /**
     * A tab switch, not a push: the hero's destination is a tab root, and that tab keeps whatever
     * depth the visitor left it at.
     */
    override fun navigateToProgramme() {
        tabNavigator.select(Tab.PROGRAMME)
    }

    override fun navigateToAnnouncements() {
        appNavigator.navigateTo(AnnouncementsDestination)
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
