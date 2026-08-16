package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements.AnnouncementsRoute
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class HomeNavKeyHandler(
    private val navigator: HomeNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<HomeMainDestination> {
            HomeRoute(
                onNavigateToProgramme = { navigator.navigateToProgramme() },
                onNavigateToAnnouncements = { navigator.navigateToAnnouncements() },
            )
        }

        entry<AnnouncementsDestination> {
            AnnouncementsRoute(onNavigateBack = { navigator.navigateBack() })
        }
    }
}
