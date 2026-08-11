package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class HomeNavKeyHandler : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<HomeDestination> { HomeRoute() }
    }
}
