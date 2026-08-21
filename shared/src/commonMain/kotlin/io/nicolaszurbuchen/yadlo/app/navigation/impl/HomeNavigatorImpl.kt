package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.app.navigation.Tab
import io.nicolaszurbuchen.yadlo.app.navigation.TabNavigator
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.AnnouncementsDestination
import io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation.HomeNavigator
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.AccessDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.PaymentDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.StoryDestination
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation.VolunteeringDestination
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

    // Pushes, not tab switches. [AppNavigator] is attached to whichever stack is showing, so a
    // promoted tile tapped on Accueil opens over Accueil and backs out to it — which is what makes
    // these four a promotion of a Plus screen rather than a shortcut into the Plus tab.
    override fun navigateToPayment() {
        appNavigator.navigateTo(PaymentDestination)
    }

    override fun navigateToAccess() {
        appNavigator.navigateTo(AccessDestination)
    }

    override fun navigateToVolunteering() {
        appNavigator.navigateTo(VolunteeringDestination)
    }

    override fun navigateToStory() {
        appNavigator.navigateTo(StoryDestination)
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
