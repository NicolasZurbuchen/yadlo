package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements.AnnouncementsRoute
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeRoute
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.QuickAccessEntryUiModel
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class HomeNavKeyHandler(
    private val navigator: HomeNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<HomeMainDestination> {
            HomeRoute(
                onNavigateToSearch = { navigator.navigateToSearch() },
                onNavigateToProgramme = { navigator.navigateToProgramme() },
                onNavigateToAnnouncements = { navigator.navigateToAnnouncements() },
                // Exhaustive over the enum, so a promoted entry added later without a destination
                // is a compile error rather than a dead row. NEWSLETTER never reaches here — it
                // carries a url and the Route sends it to the browser instead.
                onNavigateToQuickAccess = { entry ->
                    when (entry) {
                        QuickAccessEntryUiModel.PAYMENT -> navigator.navigateToPayment()
                        QuickAccessEntryUiModel.ACCESS -> navigator.navigateToAccess()
                        QuickAccessEntryUiModel.VOLUNTEERING -> navigator.navigateToVolunteering()
                        QuickAccessEntryUiModel.CONTACT -> navigator.navigateToContact()
                        QuickAccessEntryUiModel.STORY -> navigator.navigateToStory()
                        QuickAccessEntryUiModel.NEWSLETTER -> Unit
                    }
                },
            )
        }

        entry<AnnouncementsDestination> {
            AnnouncementsRoute(onNavigateBack = { navigator.navigateBack() })
        }
    }
}
